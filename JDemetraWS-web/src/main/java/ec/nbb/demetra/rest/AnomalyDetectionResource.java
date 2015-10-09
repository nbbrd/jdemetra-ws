/*
 * Copyright 2015 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbb.demetra.rest;

import ec.nbb.demetra.filter.Compress;
import ec.nbb.demetra.json.JsonTsPeriod;
import ec.nbb.demetra.model.outlier.Outlier;
import ec.nbb.demetra.model.outlier.OutlierRequest;
import ec.nbb.demetra.model.outlier.OutlierResult;
import ec.nbb.demetra.model.outlier.OutlierResults;
import ec.nbb.demetra.model.outlier.ShadowOutlier;
import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.tss.TsCollection;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.arima.IPreprocessor;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.timeseries.regression.OutlierEstimation;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.simplets.TsData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Mats Maggi
 */
@Path("/outlier")
@Api(value = "/outlier")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class AnomalyDetectionResource {

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Process an outlier detection on given Ts", notes = "Creates an outlier detection", response = OutlierResults.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of outlier detection", response = OutlierResults.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response outlierDetection(@ApiParam(name = "OutlierRequest", required = true) OutlierRequest request) {
        try {
            TsCollection tsCollection = request.getSeries().createTSCollection();
            double critical = request.getCriticalValue();
            String transformation = request.getTransformation();
            TramoSpecification spec = getSpecification(request.getSpecification());
            setShownOutliers(spec, request.isShowAO(), request.isShowLS(), request.isShowTC(), request.isShowSO());
            setTransformation(spec, transformation);
            spec.getOutliers().setCriticalValue(critical);

            OutlierResults results = new OutlierResults();

            IPreprocessor processor = spec.build();

            for (int i = 0; i < tsCollection.getCount(); i++) {
                TsData tsData = tsCollection.get(i).getTsData();
                String name = tsCollection.get(i).getName();
                OutlierResult r = new OutlierResult();
                r.setName(name);

                if (tsData == null || tsData.getLength() == 0) {
                    r.setStatus("NoData");
                } else {
                    OutlierEstimation[] oe;
                    PreprocessingModel model = processor.process(tsData, null);
                    if (model != null) {
                        oe = model.outliersEstimation(true, false);

                        if (oe != null && oe.length > 0) {
                            r.setStatus("Success");
                            for (OutlierEstimation out : oe) {
                                Outlier o = new Outlier();
                                JsonTsPeriod p = new JsonTsPeriod();
                                p.from(out.getPosition());
                                o.setPosition(p);
                                o.setType(out.getCode().toString());
                                o.setStdev(out.getStdev());
                                o.setValue(out.getValue());
                                o.setTStat(out.getTStat());

                                r.add(o);
                            }
                        } else {
                            r.setStatus("NoOutliers");
                        }
                    } else {
                        r.setStatus("Failed");
                    }
                }
                results.add(r);
            }

            return Response.status(Status.OK).entity(results).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private TramoSpecification getSpecification(String spec) {
        switch (spec) {
            case "TR0":
                return TramoSpecification.TR0;
            case "TR1":
                return TramoSpecification.TR1;
            case "TR2":
                return TramoSpecification.TR2;
            case "TR3":
                return TramoSpecification.TR3;
            case "TR4":
                return TramoSpecification.TR4;
            case "TR5":
                return TramoSpecification.TR5;
            case "TRfull":
                return TramoSpecification.TRfull;
            default:
                throw new IllegalArgumentException("Unable to find a spec with name : " + spec);
        }

    }

    private void setShownOutliers(TramoSpecification spec, boolean ao, boolean ls, boolean tc, boolean so) {
        spec.getOutliers().clearTypes();
        if (ao) {
            spec.getOutliers().add(OutlierType.AO);
        }
        if (ls) {
            spec.getOutliers().add(OutlierType.LS);
        }
        if (tc) {
            spec.getOutliers().add(OutlierType.TC);
        }
        if (so) {
            spec.getOutliers().add(OutlierType.SO);
        }

        if (spec.getOutliers().getTypes().length == 0) {
            throw new IllegalArgumentException("At least one outlier type must be shown");
        }
    }

    private void setTransformation(TramoSpecification spec, String transform) {
        spec.getTransform().setFunction(DefaultTransformationType.valueOf(transform));
    }

    @POST
    @Path("/new")
    @Compress
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Process an outlier detection on a given Ts", notes = "Creates an outlier detection", response = ShadowOutlier.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of outlier detection", response = ShadowOutlier.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response outlierDetection(@ApiParam(name = "ShadowTs", required = true) ShadowTs ts) {
        TramoSpecification spec = TramoSpecification.TRfull;
        setShownOutliers(spec, true, true, true, true);
        setTransformation(spec, "None");
        spec.getOutliers().setCriticalValue(2.5);

        List<ShadowOutlier> outliers = new ArrayList<>();

        IPreprocessor processor = spec.build();

        TsData tsData = RestUtils.createTsData(ts);

        OutlierEstimation[] oe;
        PreprocessingModel model = processor.process(tsData, null);
        if (model != null) {
            oe = model.outliersEstimation(true, false);

            if (oe != null && oe.length > 0) {
                for (OutlierEstimation out : oe) {
                    ShadowOutlier o = new ShadowOutlier();
                    int year = out.getPosition().getYear();
                    int placeinyear = out.getPosition().getPosition();
                    o.setPeriod(year * ts.getFreq() + placeinyear);
                    o.setOutlierType(out.getCode());
                    o.setValue(out.getValue());
                    o.setStdev(out.getStdev());

                    outliers.add(o);
                }
            }
        }

        return Response.status(Status.OK).entity(outliers).build();
    }
}
