/*
 * Copyright 2014 National Bank of Belgium
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

import ec.businesscycle.impl.HodrickPrescott;
import ec.nbb.demetra.Messages;
import ec.nbb.ws.annotations.Compress;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import static ec.tss.businesscycle.processors.HodrickPrescottProcessingFactory.defaultLambda;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.timeseries.simplets.TsData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.HashMap;
import java.util.Map;
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
@Path("/hodrickprescott")
@Api(value = "/hodrickprescott")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class HodrickPrescottResource {

    @POST
    @Compress
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Hodrick Prescott on the given series. It returns the following series : Series, Cycle and Trend.", response = XmlTsData.class, responseContainer = "Map")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Hodrick Prescott was successfully processed", response = XmlTsData.class, responseContainer = "Map"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response hodrickPrescott(@ApiParam(name = "tsData", required = true) XmlTsData tsData,
            @ApiParam(name = "method", defaultValue = "TramoSeats") @QueryParam(value = "method") @DefaultValue("TramoSeats") String method,
            @ApiParam(name = "spec", defaultValue = "RSA4") @QueryParam(value = "spec") @DefaultValue("RSA4") String spec,
            @ApiParam(name = "target", defaultValue = "Sa") @QueryParam(value = "target") @DefaultValue("Sa") String target,
            @ApiParam(name = "deflambda", defaultValue = "true") @QueryParam(value = "deflambda") @DefaultValue("true") boolean deflambda,
            @ApiParam(name = "lambda", defaultValue = "1600") @QueryParam(value = "lambda") @DefaultValue("1600") Double lambda,
            @ApiParam(name = "mul", defaultValue = "false") @QueryParam(value = "mul") @DefaultValue("false") boolean mul,
            @ApiParam(name = "lcycle", defaultValue = "0") @QueryParam(value = "lcycle") @DefaultValue("0") Double lcycle) {

        Map<String, XmlTsData> results = new HashMap<>();

        if (tsData == null || tsData.create() == null) {
            throw new IllegalArgumentException(Messages.TS_NULL);
        }

        TsData data = tsData.create();
        if (data.isEmpty()) {
            throw new IllegalArgumentException(Messages.TS_EMPTY);
        }

        // Seasonal Adjustment step
        CompositeResults saResults = null;
        switch (method.toLowerCase()) {
            case "tramoseats":
                TramoSeatsSpecification s = TramoSeatsSpecification.fromString(spec == null ? "" : spec);
                saResults = TramoSeatsProcessingFactory.process(data, s);
                if (saResults == null) {
                    throw new IllegalArgumentException(Messages.PROCESSING_ERROR);
                }
                break;
            case "x13":
                X13Specification sx = X13Specification.fromString(spec == null ? "" : spec);
                saResults = X13ProcessingFactory.process(data, sx);
                if (saResults == null) {
                    throw new IllegalArgumentException(Messages.PROCESSING_ERROR);
                }
                break;
            case "none":
                break;
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, method.toLowerCase()));
        }

        TsData targetData = null;
        switch (target.toLowerCase()) {
            case "original":
                targetData = data;
                break;
            case "trend":
                targetData = saResults != null ? saResults.getData("t", TsData.class) : null;
                break;
            case "sa":
                targetData = saResults != null ? saResults.getData("sa", TsData.class) : null;
                break;
        }

        if (targetData == null) {
            throw new IllegalArgumentException(String.format(Messages.UNKNOWN_TARGET, target));
        }

        // Hendrick Prescott step
        TsData input = targetData.clone();
        if (mul) {
            input = input.log();
        }
        HodrickPrescott hp = new HodrickPrescott();
        if (lcycle != 0) {
            lambda = defaultLambda(lcycle, targetData.getFrequency().intValue());
        } else if (deflambda) {
            lambda = defaultLambda(8, targetData.getFrequency().intValue());
        }
        hp.setLambda(lambda);

        if (!hp.process(input.getValues().internalStorage())) {
            throw new IllegalArgumentException(Messages.CHECKLAST_ERROR);
        } else {
            TsData t = new TsData(targetData.getStart(), hp.getSignal(), false);
            if (mul) {
                t = t.exp();
            }
            XmlTsData trend = new XmlTsData();
            trend.copy(t);
            results.put("trend", trend);

            TsData c = mul ? TsData.divide(targetData, t) : TsData.subtract(targetData, t);
            XmlTsData cycle = new XmlTsData();
            cycle.copy(c);
            results.put("cycle", cycle);
            XmlTsData series = new XmlTsData();
            series.copy(targetData);
            results.put("series", series);
        }

        return Response.ok().entity(results).build();
    }
}
