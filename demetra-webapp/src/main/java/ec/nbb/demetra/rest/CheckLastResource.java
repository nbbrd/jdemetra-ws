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
import ec.nbb.demetra.model.terror.TerrorResult;
import ec.nbb.ws.annotations.Compress;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.IPreprocessor;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
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
 * Resource that executes the Check Last algorithm for given time series.
 *
 * @author Mats Maggi
 */
@Path("/checklast")
@Api(value = "/checklast")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CheckLastResource {

    @POST
    @Compress
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Process a check last on a given Ts", notes = "Creates a check last processing.", response = TerrorResult.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of check last", response = TerrorResult.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response checkLast(
            @ApiParam(name = "ts", required = true) XmlTsData ts,
            @ApiParam(name = "nbLast", required = true, defaultValue = "1") @QueryParam("nbLast") @DefaultValue("1") int nbLast,
            @ApiParam(name = "algorithm", defaultValue = "tramoseats") @QueryParam("algorithm") @DefaultValue("tramoseats") String algorithm,
            @ApiParam(name = "spec", defaultValue = "TRfull") @QueryParam("spec") @DefaultValue("RSAfull") String spec) {
        IPreprocessor p;

        if (ts == null) {
            throw new IllegalArgumentException(Messages.TS_NULL);
        }

        TsData input = ts.create();
        if (input.isEmpty()) {
            throw new IllegalArgumentException(Messages.TS_EMPTY);
        }

        if (nbLast <= 0) {
            throw new IllegalArgumentException(String.format(Messages.POSITIVE_NB_LAST, nbLast));
        }

        spec = mapSpec(algorithm, spec);

        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                p = TramoSpecification.defaultPreprocessor(TramoSpecification.Default.valueOfIgnoreCase(spec));
                break;
            case "x13":
                p = RegArimaSpecification.defaultPreprocessor(RegArimaSpecification.Default.valueOf(spec));
                break;
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, algorithm));
        }

        CheckLast cl = new CheckLast(p);
        cl.setBackCount(nbLast);
        if (cl.check(input)) {
            TerrorResult result = new TerrorResult(ts.name == null ? "CheckLast" : ts.name, cl.getValues(),
                    cl.getForecastsValues(), cl.getScores());
            return Response.ok().entity(result).build();
        } else {
            return Response.serverError().entity(Messages.CHECKLAST_ERROR).build();
        }
    }

    private String mapSpec(String algo, String spec) {
        return spec.toUpperCase().replace("RSA", algo.toLowerCase().equals("tramoseats") ? "TR" : "RG");
    }

    @POST
    @Path("/collection")
    @Compress
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Process a check last on a given list of Ts", notes = "Creates a check last processing.", response = TerrorResult.class, responseContainer = "List")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of check last", response = TerrorResult.class, responseContainer = "List"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response checkLast(
            @ApiParam(name = "tsCollection", required = true) XmlTsData[] tsCollection,
            @ApiParam(name = "nbLast", required = true) @QueryParam("nbLast") @DefaultValue("1") int nbLast,
            @ApiParam(name = "algorithm") @QueryParam("algorithm") @DefaultValue("tramoseats") String algorithm,
            @ApiParam(name = "spec") @QueryParam("spec") @DefaultValue("RSAfull") String spec) {
        IPreprocessor p = null;
        List<TerrorResult> results = new ArrayList<>();

        if (tsCollection == null || tsCollection.length == 0) {
            throw new IllegalArgumentException(Messages.NO_SERIES);
        }

        if (nbLast <= 0) {
            throw new IllegalArgumentException(Messages.POSITIVE_NB_LAST);
        }

        spec = mapSpec(algorithm, spec);

        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                p = TramoSpecification.defaultPreprocessor(TramoSpecification.Default.valueOfIgnoreCase(spec));
                break;
            case "x13":
                p = RegArimaSpecification.defaultPreprocessor(RegArimaSpecification.Default.valueOf(spec));
                break;
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, algorithm));
        }

        CheckLast cl = new CheckLast(p);
        cl.setBackCount(nbLast);

        for (XmlTsData t : tsCollection) {
            if (t != null) {
                TsData data = t.create();
                try {
                    if (cl.check(data)) {
                        results.add(new TerrorResult(t.name, cl.getValues(), cl.getForecastsValues(), cl.getScores()));
                    }
                } catch (Exception ex) {
                    System.out.println(String.format(Messages.TS_CREATION_ERROR, t.name));
                    System.out.println(ex);
                }
            }
        }
        return Response.ok().entity(results).build();
    }
}
