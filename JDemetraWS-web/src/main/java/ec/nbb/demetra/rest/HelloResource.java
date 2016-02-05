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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * WebService providing machine name
 *
 * @author Mats Maggi
 */
@Path("/hello")
@Api(value = "/hello")
public class HelloResource {

    @GET
    @Compress
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Says hello", notes = "Says hello !", response = String.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful request", response = String.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response hello(@Context HttpServletRequest req,
            @ApiParam(name = "firstName") @QueryParam(value = "firstName") String firstName) {
        try {
            String name = InetAddress.getLocalHost().getHostName();
            if (firstName != null && !firstName.isEmpty()) {
                name += " - User : " + firstName;
            }
            if (req != null) {
                String remoteHost = req.getRemoteHost();
                String remoteAddr = req.getRemoteAddr();
                int remotePort = req.getRemotePort();
                name += "\n" + remoteHost + " (" + remoteAddr + ":" + remotePort + ")";
            }

            System.out.println("Hello from :\n" + name);
            return Response.status(Response.Status.OK).entity(name).build();
        } catch (UnknownHostException ex) {
            return Response.status(Response.Status.OK).entity("Hello World !").build();
        }
    }

    @GET
    @Compress
    @Path("/async")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Says hello asynchronously", notes = "Says hello !", response = String.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful request", response = String.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public void helloAsync(@Suspended final AsyncResponse response) {
        response.register(new ConnectionCallback() {

            @Override
            public void onDisconnect(AsyncResponse disconnected) {
                response.resume(Response.status(Status.SERVICE_UNAVAILABLE)
                        .entity("Connection lost").build());
            }
        });

        new Thread(new Runnable() {

            @Override
            public void run() {
                String result = veryExpensiveOperation();
                response.resume(result);
            }

            private String veryExpensiveOperation() {
                try {
                    Thread.sleep(10000);
                    return InetAddress.getLocalHost().getHostName();
                } catch (InterruptedException | UnknownHostException ex) {
                    Logger.getLogger(HelloResource.class.getName()).log(Level.SEVERE, null, ex);
                }
                return "Unknown host";
            }
        }).start();
    }
}
