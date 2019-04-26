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
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.nbb.ws.annotations.Compress;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Path("/forecast")
@Api(value = "/forecast")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ForecastingResource {

    @POST
    @Compress
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Returns the given series ", notes = "Only 'x13' and 'tramoseats' are currently supported", response = XmlTsData.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Forecasting/Backcasting successfully done", response = XmlTsData.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response forecasting(
            @ApiParam(value = "ts", required = true) XmlTsData ts,
            @QueryParam(value = "start") @ApiParam(value = "start") int start,
            @QueryParam(value = "end") @ApiParam(value = "end") int end,
            @QueryParam(value = "algorithm") @ApiParam(value = "algorithm", defaultValue = "tramoseats") @DefaultValue("tramoseats") String algorithm,
            @QueryParam(value = "spec") @ApiParam(value = "spec", defaultValue = "TRfull") @DefaultValue("TRfull") String spec) {

        TsData tsData = ts.create();
        TsFrequency freq = tsData.getFrequency();
        int nb = 0, nf = 0;
        TsDomain dom = null;
        if (start > 0 && end > 0) {
            dom = RestUtils.createTsDomain(start, end, freq);
            nb = tsData.getDomain().getStart().minus(dom.getStart());
            nf = dom.getEnd().minus(tsData.getDomain().getEnd());
        }

        TsData b = null, f = null;

        spec = mapSpec(algorithm, spec);
        PreprocessingModel model = getModel(tsData, algorithm, spec);

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

        XmlTsData result = new XmlTsData();
        result.copy(tsData);
        result.name = ts.name;
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("/bynumber")
    @Compress
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Returns the new series with the amount of backcasts and forecasts requested", notes = "Only 'x13' and 'tramoseats' are currently supported", response = XmlTsData.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Forecasting/Backcasting successfully done", response = XmlTsData.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response forecastByNumber(
            @ApiParam(value = "ts", required = true) XmlTsData ts,
            @QueryParam(value = "backcasts") @ApiParam(value = "backcasts", defaultValue = "0") @DefaultValue("0") int backcasts,
            @QueryParam(value = "forecasts") @ApiParam(value = "forecasts", defaultValue = "0") @DefaultValue("0") int forecasts,
            @QueryParam(value = "algorithm") @ApiParam(value = "algorithm", defaultValue = "tramoseats") @DefaultValue("tramoseats") String algorithm,
            @QueryParam(value = "spec") @ApiParam(value = "spec", defaultValue = "TRfull") @DefaultValue("TRfull") String spec) {

        TsData tsData = ts.create();
        TsData b = null, f = null;

        spec = mapSpec(algorithm, spec);
        PreprocessingModel model = getModel(tsData, algorithm, spec);

        if (model != null) {
            if (backcasts > 0) {
                b = model.backcast(backcasts, false);
            }

            if (forecasts > 0) {
                f = model.forecast(forecasts, false);
            }

            if (b != null) {
                tsData = tsData.update(b);
            }

            if (f != null) {
                tsData = tsData.update(f);
            }
        }

        XmlTsData result = new XmlTsData();
        result.copy(tsData);
        result.name = ts.name;
        return Response.ok().entity(result).build();
    }

    private String mapSpec(String algo, String spec) {
        return spec.toUpperCase().replace("RSA", algo.toLowerCase().equals("tramoseats") ? "TR" : "RG");
    }

    private PreprocessingModel getModel(TsData tsData, String algorithm, String spec) {
        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                return TramoSpecification.defaultPreprocessor(TramoSpecification.Default.valueOfIgnoreCase(spec)).process(tsData, null);
            case "x13":
                return RegArimaSpecification.defaultPreprocessor(RegArimaSpecification.Default.valueOf(spec)).process(tsData, null);
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, algorithm));
        }
    }
}
