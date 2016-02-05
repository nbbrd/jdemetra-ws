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

import ec.nbb.demetra.filter.Compress;
import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.nbb.demetra.rest.model.TerrorResult;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.IPreprocessor;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mats Maggi
 */
@Path("/checklast")
@Api(value = "/checklast")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CheckLastResource {

    @POST
    @Compress
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Process a check last on a given list of Ts", notes = "Creates a check last processing.", response = TerrorResult.class, responseContainer = "List")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of check last", response = TerrorResult.class, responseContainer = "List"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response checkLast(
            @ApiParam(name = "tsList", required = true) ShadowTs[] ts,
            @ApiParam(name = "nbLast", required = true) @QueryParam("nbLast") int nbLast,
            @ApiParam(name = "algorithm") @QueryParam("algorithm") String algorithm,
            @ApiParam(name = "spec") @QueryParam("spec") String spec) {
        IPreprocessor p = null;
        List<TerrorResult> results = new ArrayList<>();

        if (ts == null || ts.length == 0) {
            throw new IllegalArgumentException("At least one time series must be provided !");
        }

        if (nbLast <= 0) {
            throw new IllegalArgumentException("NbLast parameter must be > 0");
        }

        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                TramoSeatsSpecification tsSpec = TramoSeatsSpecification.fromString(spec == null ? "" : spec);
                p = tsSpec.buildPreprocessor(new ProcessingContext());
                break;
            case "x13":
                X13Specification x13Spec = X13Specification.fromString(spec == null ? "" : spec);
                p = x13Spec.buildPreprocessor();
                break;
            default:
                throw new IllegalArgumentException("Unrecognized algoritm (" + algorithm + ") !");
        }

        CheckLast cl = new CheckLast(p);
        cl.setBackCount(nbLast);

        for (ShadowTs t : ts) {
            try {
                if (cl.check(RestUtils.createTsData(t))) {
                    TerrorResult r = new TerrorResult(t.getName());
                    r.setValue(cl.getValues());
                    r.setForecast(cl.getForecastsValues());
                    r.setScore(cl.getScores());
                    results.add(r);
                }
            } catch (Exception ex) {
                System.out.println("Unable to create Ts (" + t.getName() + ")");
                System.out.println(ex);
            }
        }

        return Response.ok().entity(results).build();
    }
}
