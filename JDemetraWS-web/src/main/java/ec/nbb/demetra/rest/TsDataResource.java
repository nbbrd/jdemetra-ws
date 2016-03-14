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
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mats Maggi
 */
@Path("/tsdata")
@Api(value = "/tsdata")
@Produces({MediaType.APPLICATION_JSON})
public class TsDataResource {

    @GET
    @Compress
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Random TsData", notes = "Returns a random TsData of a given frequency", response = XmlTsData.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "TsData succesfully created", response = XmlTsData.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response random(
            @ApiParam(value = "frequency", defaultValue = "12") @QueryParam(value = "frequency") @DefaultValue("12") int frequency) {
        TsData ts;
        try {
            TsFrequency freq = TsFrequency.valueOf(frequency);
            ts = TsData.random(freq);
        } catch (IllegalArgumentException ex) {
            ts = TsData.random(TsFrequency.Monthly);
        }
        
        XmlTsData json = new XmlTsData();
        json.copy(ts);
        
        return Response.ok().entity(json).build();
    }
}
