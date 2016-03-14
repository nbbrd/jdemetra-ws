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
import ec.nbb.demetra.Messages;
import ec.nbb.demetra.filter.Compress;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.xml.XmlTsData;
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
@Path("/tramoseats")
@Api(value = "/tramoseats")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TramoSeatsResource {

    private final String[] components = {"sa", "t", "s", "i", "y_f"};
    
    @POST
    @Compress
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Returns the components of the TramoSeats processing of the given series", response = XmlTsData.class, responseContainer = "Map")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "TramoSeats was successfully processed", response = XmlTsData.class, responseContainer = "Map"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response tramoSeats(@ApiParam(name = "tsData", required = true) XmlTsData tsData,
            @ApiParam(name = "spec", defaultValue = "RSA4") @QueryParam(value = "spec") @DefaultValue("RSA4") String spec) {
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
}
