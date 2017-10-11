/*
 * Copyright 2017 National Bank of Belgium
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

import ec.demetra.xml.core.XmlInformation;
import ec.demetra.xml.core.XmlInformationSet;
import ec.demetra.xml.core.XmlTsData;
import ec.demetra.xml.sa.x13.XmlX13Request;
import ec.nbb.ws.annotations.Compress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mats Maggi
 */
@Path("/test")
@Api(value = "/test", hidden = false)
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class NewXMLResource {

    @POST
    @Compress
    @Path("/tsdata")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlTsData", notes = "Returns the given XmlTsData object for serialization testing purpose", response = XmlTsData.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlTsData.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tsData(@ApiParam(value = "infoset", required = true) ec.demetra.xml.core.XmlTsData data) {
        return Response.ok(data).build();
    }
    
    @POST
    @Compress
    @Path("/info")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlInformation", notes = "Returns the given XmlInformation object for serialization testing purpose", response = XmlInformation.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlInformation.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response infoSet(@ApiParam(value = "infoset", required = true) XmlInformation set) {
        return Response.ok(set).build();
    }
    
    @POST
    @Compress
    @Path("/infoset")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlInformationSet", notes = "Returns the given XmlInformationSet object for serialization testing purpose", response = XmlInformationSet.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlInformationSet.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response infoSet(@ApiParam(value = "infoset", required = true) XmlInformationSet set) {
        return Response.ok(set).build();
    }
    
    @POST
    @Compress
    @Path("/x13/request")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlX13Request", notes = "Returns the given XmlX13Request object for serialization testing purpose", response = XmlX13Request.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlX13Request.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response infoSet(@ApiParam(value = "request", required = true) XmlX13Request set) {
        return Response.ok(set).build();
    }
}

