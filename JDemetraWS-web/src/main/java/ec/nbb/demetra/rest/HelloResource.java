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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * WebService providing machine name
 * @author Mats Maggi
 */
@Path("/hello")
@Api(value = "/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Says hello", notes = "Says hello !", response = String.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful request", response = String.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response hello() {
        try {
            String name = InetAddress.getLocalHost().getHostName();
            return Response.status(Response.Status.OK).entity("Hello : " + name).build();
        } catch (UnknownHostException ex) {
            return Response.status(Response.Status.OK).entity("Hello World !").build();
        }

    }
}
