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

import ec.demetra.xml.core.XmlPeriodSelection;
import ec.demetra.xml.core.XmlTs;
import ec.demetra.xml.core.XmlTsCollection;
import ec.demetra.xml.core.XmlTsData;
import ec.demetra.xml.core.XmlTsMoniker;
import ec.demetra.xml.core.XmlTsPeriod;
import ec.demetra.xml.regression.XmlRegression;
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
@Path("/test/xml")
@Api(value = "/test/xml", hidden = false)
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class TestXmlResource {

    @POST
    @Compress
    @Path("/ts")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlTs", notes = "Returns the given XmlTs object for serialization testing purpose", response = XmlTs.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlTs.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response ts(@ApiParam(value = "ts", required = true) XmlTs ts) {
        return Response.ok().entity(ts).build();
    }

    @POST
    @Compress
    @Path("/tscollection")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given JsonTsCollection", notes = "Returns the given XmlTsCollection object for serialization testing purpose", response = XmlTsCollection.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlTsCollection.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tsCollection(@ApiParam(value = "tsCollection", required = true) XmlTsCollection tsCollection) {
        return Response.ok().entity(tsCollection).build();
    }

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
    public Response tsData(@ApiParam(value = "tsData", required = true) XmlTsData tsData) {
        return Response.ok().entity(tsData).build();
    }

    @POST
    @Compress
    @Path("/tsmoniker")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlTsMoniker", notes = "Returns the given XmlTsMoniker object for serialization testing purpose", response = XmlTsMoniker.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlTsMoniker.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tsMoniker(@ApiParam(value = "tsMoniker", required = true) XmlTsMoniker tsMoniker) {
        return Response.ok().entity(tsMoniker).build();
    }

    @POST
    @Compress
    @Path("/periodselector")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlPeriodSelection", notes = "Returns the given XmlPeriodSelection object for serialization testing purpose", response = XmlPeriodSelection.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlPeriodSelection.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tsPeriodSelector(@ApiParam(value = "periodSelection", required = true) XmlPeriodSelection periodSelector) {
        return Response.ok().entity(periodSelector).build();
    }

    @POST
    @Compress
    @Path("/tsperiod")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlTsPeriod", notes = "Returns the given XmlTsPeriod object for serialization testing purpose", response = XmlTsPeriod.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlTsPeriod.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tsPeriod(@ApiParam(value = "tsPeriod", required = true) XmlTsPeriod tsPeriod) {
        return Response.ok().entity(tsPeriod).build();
    }

    @POST
    @Compress
    @Path("/regression")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "[TEST] Returns the given XmlRegression", notes = "Returns the given XmlRegression object for serialization testing purpose", response = XmlRegression.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Object succesfully returned", response = XmlRegression.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response regression(@ApiParam(value = "tsPeriod", required = true) XmlRegression regression) {
        return Response.ok().entity(regression).build();
    }
}
