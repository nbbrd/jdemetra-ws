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

import com.google.common.base.Strings;
import ec.demetra.xml.core.XmlInformationSet;
import ec.demetra.xml.sa.tramoseats.TramoSeatsXmlProcessor;
import ec.demetra.xml.sa.tramoseats.XmlTramoSeatsRequest;
import ec.demetra.xml.sa.tramoseats.XmlTramoSeatsRequests;
import ec.nbb.demetra.Messages;
import ec.nbb.ws.annotations.Compress;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.xml.XmlTsData;
import ec.tss.xml.tramoseats.XmlTramoSeatsSpecification;
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
import javax.ws.rs.GET;
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
@Path("/tramoseats")
@Api(value = "/tramoseats")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces(MediaType.APPLICATION_JSON)
public class TramoSeatsResource {

    private final String[] components = {"sa", "t", "s", "i", "y_f"};
    
    @POST
    @Compress
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns the components of the TramoSeats processing of the given series", response = XmlTsData.class, responseContainer = "Map")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "TramoSeats was successfully processed", response = XmlTsData.class, responseContainer = "Map"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tramoSeats(@ApiParam(name = "tsData", required = true) XmlTsData tsData,
            @ApiParam(name = "spec", defaultValue = "RSAfull") @QueryParam(value = "spec") @DefaultValue("RSAfull") String spec) {
        CompositeResults results = null;
        TramoSeatsSpecification specification;
        Map<String, XmlTsData> compMap = new HashMap<>();
        
        if (Strings.isNullOrEmpty(spec)) {
            specification = TramoSeatsSpecification.RSAfull;
        } else {
            specification = TramoSeatsSpecification.fromString(spec);
        }

        if (tsData == null) {
            throw new IllegalArgumentException(Messages.TS_NULL);
        } else {
            TsData data = tsData.create();
            results = TramoSeatsProcessingFactory.process(data, specification);
        }
        
        if (results == null) {
            throw new IllegalArgumentException(Messages.PROCESSING_ERROR);
        } else {
            for (String c : components) {
                if (results.contains(c)) {
                    TsData compData = results.getData(c, TsData.class);
                    XmlTsData xml = new XmlTsData();
                    xml.copy(compData);
                    compMap.put(c, xml);
                }
            }
        }
        
        return Response.ok().entity(compMap).build();
    }
    
    @GET
    @Path("{spec}")
    @Compress
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Returns the specification schema from a given specification name", response = XmlTramoSeatsSpecification.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "TramoSeats specification schema was successfully returned", response = XmlTramoSeatsSpecification.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tramoseatsSpec(@PathParam("spec") String spec) {
        TramoSeatsSpecification specification;
        if (Strings.isNullOrEmpty(spec)) {
            throw new IllegalArgumentException(String.format(Messages.UNKNOWN_SPEC, spec));
        } else {
            specification = TramoSeatsSpecification.fromString(spec);
        }
        
        XmlTramoSeatsSpecification xml = new XmlTramoSeatsSpecification();
        xml.copy(specification);
        
        return Response.ok().entity(xml).build();
    }
    
    @POST
    @Compress
    @Path("/request")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Returns the requested components of the TramoSeats processing of the given series", response = XmlInformationSet.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "TramoSeats was successfully processed", response = XmlInformationSet.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tramoSeats(@ApiParam(name = "request", required = true) XmlTramoSeatsRequest request) {
        TramoSeatsXmlProcessor processor = new TramoSeatsXmlProcessor();
        XmlInformationSet set = processor.process(request);
        if (set == null) {
            throw new IllegalArgumentException("Unable to process the request, please check your inputs.");
        }
        
        return Response.ok().entity(set).build();
    }
    
    @POST
    @Compress
    @Path("requests")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Returns the requested components of the TramoSeats processing of the given series", response = XmlInformationSet.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "TramoSeats was successfully processed", response = XmlInformationSet.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tramoSeats(@ApiParam(name = "requests", required = true) XmlTramoSeatsRequests requests) {
        TramoSeatsXmlProcessor processor = new TramoSeatsXmlProcessor();
        XmlInformationSet set = processor.process(requests);
        if (set == null) {
            throw new IllegalArgumentException("Unable to process the request, please check your inputs.");
        }
        
        return Response.ok().entity(set).build();
    }
}
