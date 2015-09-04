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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import ec.nbb.demetra.exception.TerrorException;
import ec.nbb.demetra.rest.model.TerrorRequest;
import ec.nbb.demetra.rest.model.TerrorResult;
import ec.nbb.demetra.rest.model.TerrorResults;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.IPreprocessor;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
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
@Path("/terror")
@Api(value = "/terror", description = "Endpoint for Terror processing")
public class TerrorResource {

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Terror process", notes = "Creates a terror processing.", response = TerrorResults.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of terror", response = TerrorResults.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response terror(@ApiParam(name = "TerrorRequest", required = true) TerrorRequest request) {
        try {
            TsCollection tsCollection = request.getSeries().createTSCollection();
            TerrorResults results = new TerrorResults();
            int nbLast = request.getNbLast();

            if (nbLast <= 0) {
                throw new TerrorException("NbLast parameter must be > 0");
            }

            for (Ts ts : tsCollection) {
                IPreprocessor p = TramoSpecification.defaultPreprocessor(TramoSpecification.Default.valueOf(request.getSpecification()));
                CheckLast cl = new CheckLast(p);
                cl.setBackCount(nbLast);
                if (cl.check(ts.getTsData())) {
                    results.add(new TerrorResult(ts.getName(), cl.getValues(), cl.getForecastsValues(), cl.getScores()));
                }
            }
            return Response.status(Status.OK).entity(results).build();
            
        } catch (TerrorException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).entity("Could not find a default specification from : " + request.getSpecification()).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
