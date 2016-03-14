/*
 * Copyright 2015 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they will be approved 
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
import ec.nbb.demetra.filter.Compress;
import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.xml.XmlTs;
import ec.tss.xml.XmlTsCollection;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.timeseries.simplets.TsData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource containing all calls regarding the seasonal adjustment of time
 * series
 *
 * @author Mats Maggi
 */
@Path("/sa")
@Api(value = "/sa")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class SeasonalAdjustmentResource {

    @GET
    @Path("/{algorithm}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Returns the available specs for the given algorithm", notes = "Only 'x13' and 'tramoseats' are currently supported", response = String.class, responseContainer = "List")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Algorithm was successfully recognized", response = String.class, responseContainer = "List"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response seasonalAdjustment(
            @ApiParam(name = "algorithm", allowableValues = "tramoseats, x13", defaultValue = "tramoseats") @PathParam("algorithm") String algorithm) {
        List<String> list = null;
        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                list = new ArrayList<>();
                list.add("RSA0");
                list.add("RSA1");
                list.add("RSA2");
                list.add("RSA3");
                list.add("RSA4");
                list.add("RSA5");
                list.add("RSAfull");
                break;
            case "x13":
                list = new ArrayList<>();
                list.add("X11");
                list.add("RSA0");
                list.add("RSA1");
                list.add("RSA2c");
                list.add("RSA3");
                list.add("RSA4c");
                list.add("RSA5c");
                break;
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, algorithm));
        }
        return Response.ok().entity(list).build();
    }

    @POST
    @Compress
    @Path("/{algorithm}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Returns the seasonally adjusted series using the given algorithm and spec", notes = "Only 'x13' and 'tramoseats' are currently supported", response = ShadowTs.class, responseContainer = "List")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Ts is successfully seasonally adjusted", response = ShadowTs.class, responseContainer = "List"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response seasonalAdjustmentShadow(
            @ApiParam(name = "shadowTs", required = true) ShadowTs ts,
            @ApiParam(name = "algorithm", allowableValues = "tramoseats, x13", defaultValue = "tramoseats") @PathParam("algorithm") String algorithm,
            @ApiParam(name = "spec") @QueryParam("spec") String spec) {
        CompositeResults results = null;
        String specName;

        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                TramoSeatsSpecification s = TramoSeatsSpecification.fromString(spec == null ? "" : spec);
                specName = s.toString();
                results = TramoSeatsProcessingFactory.process(RestUtils.createTsData(ts), s);
                break;
            case "x13":
                X13Specification sx = X13Specification.fromString(spec == null ? "" : spec);
                specName = sx.toString();
                results = X13ProcessingFactory.process(RestUtils.createTsData(ts), sx);
                break;
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, algorithm));
        }

        if (results == null) {
            throw new IllegalArgumentException(Messages.PROCESSING_ERROR);
        }

        List<ShadowTs> tsList = new ArrayList<>();
        String[] components = new String[]{"sa", "t", "s", "i"};
        for (String c : components) {
            if (results.contains(c)) {
                tsList.add(RestUtils.toShadowTs(c + (specName != null ? " [" + specName + "]" : ""), results.getData(c, TsData.class)));
            }
        }

        return Response.ok().entity(tsList).build();
    }
    
    @POST
    @Compress
    @Path("/xml/{algorithm}")
    @Consumes({MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML})
    @ApiOperation(value = "Returns the seasonally adjusted series using the given algorithm and spec (uses XML objects)", notes = "Only 'x13' and 'tramoseats' are currently supported", response = ShadowTs.class, responseContainer = "List")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Ts is successfully seasonally adjusted", response = XmlTsCollection.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response seasonalAdjustmentXml(
            @ApiParam(name = "ts", required = true) XmlTs ts,
            @ApiParam(name = "algorithm", allowableValues = "tramoseats, x13", defaultValue = "tramoseats") @PathParam("algorithm") String algorithm,
            @ApiParam(name = "spec") @QueryParam("spec") String spec) {
        CompositeResults results = null;
        String specName;
        
        if (ts == null) {
            throw new IllegalArgumentException(Messages.TS_NULL);
        }
        
        TsInformation realTs = ts.create();
        if (!realTs.hasData()) {
            throw new IllegalArgumentException(Messages.TS_EMPTY);
        }

        switch (algorithm.toLowerCase()) {
            case "tramoseats":
                TramoSeatsSpecification s = TramoSeatsSpecification.fromString(spec == null ? "" : spec);
                specName = s.toString();
                results = TramoSeatsProcessingFactory.process(realTs.data, s);
                break;
            case "x13":
                X13Specification sx = X13Specification.fromString(spec == null ? "" : spec);
                specName = sx.toString();
                results = X13ProcessingFactory.process(realTs.data, sx);
                break;
            default:
                throw new IllegalArgumentException(String.format(Messages.UNKNOWN_METHOD, algorithm));
        }

        if (results == null) {
            throw new IllegalArgumentException(Messages.PROCESSING_ERROR);
        }

        TsCollectionInformation tsList = new TsCollectionInformation(new TsMoniker(), TsInformationType.All);
        String[] components = new String[]{"sa", "t", "s", "i"};
        for (String c : components) {
            if (results.contains(c)) {
                TsInformation result = new TsInformation(c + (specName != null ? " [" + specName + "]" : ""), new TsMoniker(), TsInformationType.All);
                result.data = results.getData(c, TsData.class);
                tsList.items.add(result);
            }
        }

        XmlTsCollection coll = new XmlTsCollection();
        coll.copy(tsList);
        return Response.ok().entity(coll).build();
    }
}
