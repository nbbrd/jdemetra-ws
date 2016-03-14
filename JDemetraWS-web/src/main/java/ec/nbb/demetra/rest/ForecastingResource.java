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

import ec.nbb.demetra.Messages;
import ec.nbb.demetra.filter.Compress;
import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mats Maggi
 */
@Path("/forecast")
@Api(value = "/forecast")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ForecastingResource {

    @POST
    @Compress
    @Path("/{algorithm}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Returns the given series ", notes = "Only 'x13' and 'tramoseats' are currently supported", response = ShadowTs.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Forecasting/Backcasting successfully done", response = ShadowTs.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response forecasting(
            @ApiParam(value = "ts", required = true) ShadowTs ts,
            @QueryParam(value = "start") @ApiParam(value = "start") int start,
            @QueryParam(value = "end") @ApiParam(value = "end") int end,
            @PathParam(value = "algorithm") @ApiParam(value = "algorithm", required = true) String algorithm,
            @QueryParam(value = "spec") @ApiParam(value = "spec") String spec) {

        TsData tsData = RestUtils.createTsData(ts);
        TsFrequency freq = tsData.getFrequency();
        int nb = 0, nf = 0;
        TsDomain dom = null;
        if (start > 0 && end > 0) {
            dom = RestUtils.createTsDomain(start, end, freq);
            nb = tsData.getDomain().getStart().minus(dom.getStart());
            nf = dom.getEnd().minus(tsData.getDomain().getEnd());
        }

        TsData b = null, f = null;

        PreprocessingModel model = null;
        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                TramoSeatsSpecification s;
                if (spec != null && !spec.isEmpty()) {
                    s = TramoSeatsSpecification.fromString(spec);
                } else {
                    s = TramoSeatsSpecification.RSAfull;
                }
                model = s.buildPreprocessor(new ProcessingContext()).process(tsData, null);
                break;
            case "x13":
                X13Specification sx;
                if (spec != null && !spec.isEmpty()) {
                    sx = X13Specification.fromString(spec);
                } else {
                    sx = X13Specification.RSA5;
                }
                model = sx.buildPreprocessor().process(tsData, null);
                break;
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, algorithm));
        }

        if (model != null) {
            if (nb > 0) {
                b = model.backcast(nb, false);
            }

            if (nf > 0) {
                f = model.forecast(nf, false);
            }

            if (dom != null) {
                tsData = tsData.fittoDomain(dom);
            }
            
            if (b != null) {
                tsData = tsData.update(b);
            }

            if (f != null) {
                tsData = tsData.update(f);
            }
        }

        return Response.ok().entity(RestUtils.toShadowTs(ts.getName(), tsData)).build();
    }
}
