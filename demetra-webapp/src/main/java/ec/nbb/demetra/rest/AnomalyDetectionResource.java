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

import ec.nbb.demetra.Messages;
import ec.nbb.demetra.model.outlier.ShadowOutlier;
import ec.nbb.ws.annotations.Compress;
import ec.tss.xml.XmlTsData;
import ec.tss.xml.XmlTsPeriod;
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mats Maggi
 */
@Path("/outlier")
@Api(value = "/outlier")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AnomalyDetectionResource {

    @POST
    @Compress
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Process an outlier detection on a given Ts", notes = "Creates an outlier detection", response = ShadowOutlier.class, responseContainer = "List")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of outlier detection", response = ShadowOutlier.class, responseContainer = "List"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response outlierDetectionShadow(@ApiParam(name = "ts", required = true) XmlTsData ts,
            @ApiParam(name = "transformation", defaultValue = "None", allowableValues = "None,Auto,Log") @QueryParam(value = "transformation") @DefaultValue("None") String transformation,
            @ApiParam(name = "critical", defaultValue = "0") @QueryParam(value = "critical") @DefaultValue("0") double cv,
            @ApiParam(name = "spec", defaultValue = "TRfull") @QueryParam(value = "spec") @DefaultValue("TRfull") String spec) {

        TramoSpecification tramoSpec = getSpecification(spec);
        setShownOutliers(tramoSpec, true, true, true, true);
        setTransformation(tramoSpec, transformation);
        tramoSpec.getOutliers().setCriticalValue(cv);

        List<ShadowOutlier> outliers = new ArrayList<>();

        IPreprocessor processor = tramoSpec.build();

        TsData tsData = ts.create();

        OutlierEstimation[] oe;
        PreprocessingModel model = processor.process(tsData, null);
        if (model != null) {
            oe = model.outliersEstimation(true, false);

            if (oe != null && oe.length > 0) {
                for (OutlierEstimation out : oe) {
                    ShadowOutlier o = new ShadowOutlier();
                    XmlTsPeriod period = new XmlTsPeriod();
                    period.copy(out.getPosition());
                    o.setPeriod(period);
                    o.setOutlierType(OutlierType.valueOf(out.getCode()));
                    o.setValue(out.getValue());
                    o.setStdev(out.getStdev());

                    outliers.add(o);
                }
            }
        }

        return Response.ok().entity(outliers).build();
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
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, spec));
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
            throw new IllegalArgumentException(Messages.NO_OUTLIERS_TYPE);
        }
    }

    private void setTransformation(TramoSpecification spec, String transform) {
        spec.getTransform().setFunction(DefaultTransformationType.valueOf(transform));
    }
}
