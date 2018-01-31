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

import ec.nbb.demetra.json.JsonTsCollection;
import ec.nbb.demetra.json.excel.ExcelSeries;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.nbb.demetra.model.rest.utils.SheetAdapter;
import ec.nbb.ws.annotations.Compress;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsFactory;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetFactory;
import ec.tss.tsproviders.spreadsheet.engine.TsImportOptions;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.ObsGathering;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.util.spreadsheet.helpers.ArraySheet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource that generates a random
 * {@link ec.tstoolkit.timeseries.simplets.TsData} or
 * {@link ec.tss.TsCollection}.
 *
 * @author Mats Maggi
 */
@Path("/tsdata")
@Api(value = "/tsdata")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TsDataResource {

    @GET
    @Compress
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Produces the JSON of a random TsData", notes = "Returns a random TsData of a given frequency", response = XmlTsData.class)
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

    @GET
    @Path("/collection")
    @Compress
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Produces the JSON of a random TsCollection", notes = "Returns a random TsCollection of a given frequency", response = JsonTsCollection.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "TsData succesfully created", response = JsonTsCollection.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response randomCollection(
            @ApiParam(value = "frequency", defaultValue = "12") @QueryParam(value = "frequency") @DefaultValue("12") int frequency,
            @ApiParam(value = "nb", defaultValue = "10") @QueryParam(value = "nb") @DefaultValue("10") int nb) {
        TsCollectionInformation ts = new TsCollectionInformation();
        TsFrequency freq;
        try {
            freq = TsFrequency.valueOf(frequency);
        } catch (IllegalArgumentException ex) {
            freq = TsFrequency.Monthly;
        }

        for (int i = 0; i < nb; i++) {
            TsInformation tsinfo = new TsInformation();
            tsinfo.data = TsData.random(freq);
            tsinfo.name = "Series " + i;
            ts.items.add(tsinfo);
        }

        JsonTsCollection json = new JsonTsCollection();
        json.from(ts);

        return Response.ok().entity(json).build();
    }
    
    @POST
    @Compress
    @Path("/range")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Test reading excel range", response = Object[][].class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful read", response = Object[][].class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response rangeTest(@ApiParam(name = "range", required = true) Object[][] range) {
        SheetAdapter sheet = new SheetAdapter(range);
        TsCollectionInformation info = SpreadSheetFactory.getDefault().toTsCollectionInfo(sheet, TsImportOptions.getDefault());
        return Response.ok(range).build();
    }

    @POST
    @Compress
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Test reading excel series", response = ExcelSeries.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful read", response = ExcelSeries.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response excelSeries(@ApiParam(name = "Series", required = true) ExcelSeries series) {
        TsCollectionInformation info = RestUtils.readExcelSeries(series);
        return Response.ok().build();
    }

    @POST
    @Path("/freq")
    @Compress
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Executes a frequency change on the given series", response = ExcelSeries.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful frequency change on the given series", response = ExcelSeries.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response changeFrequency(@ApiParam(name = "Series", required = true) ExcelSeries series,
            @ApiParam(name = "newFreq", required = true) @QueryParam(value = "newFreq") int newFreq,
            @ApiParam(name = "complete", defaultValue = "true") @QueryParam(value = "complete") @DefaultValue("true") boolean complete,
            @ApiParam(name = "conv", defaultValue = "Sum") @QueryParam(value = "conv") @DefaultValue("Average") TsAggregationType conv) {

        TsCollectionInformation info = RestUtils.readExcelSeries(series);
        TsCollection coll = TsFactory.instance.createTsCollection("results");
        info.items.stream().forEach((ts) -> {
            if (ts.hasData()) {
                TsData data = ts.data.changeFrequency(TsFrequency.valueOf(newFreq), conv, complete);
                if (data != null) {
                    coll.add(TsFactory.instance.createTs(ts.name, null, data));
                } else {
                    coll.add(TsFactory.instance.createTs(ts.name));
                }
            } else {
                coll.add(TsFactory.instance.createTs(ts.name));
            }
        });
        // Format results
        ExcelSeries response = RestUtils.toExcelSeries(new TsCollectionInformation(coll, TsInformationType.Data));
        return Response.ok().entity(response).build();
    }

    @POST
    @Path("/movingavg")
    @Compress
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Performs a moving average on the given series", response = ExcelSeries.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing", response = ExcelSeries.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response movingAverage(@ApiParam(name = "Series", required = true) ExcelSeries series,
            @ApiParam(name = "weights", required = true) @QueryParam(value = "weights") List<Double> weights,
            @ApiParam(name = "centered", defaultValue = "true") @QueryParam(value = "centered") @DefaultValue("true") boolean centered,
            @ApiParam(name = "rescale", defaultValue = "true") @QueryParam(value = "rescale") @DefaultValue("true") boolean rescale) {

        TsCollectionInformation info = RestUtils.readExcelSeries(series);
        TsCollection coll = TsFactory.instance.createTsCollection("results");
        double[] w = weights.stream().mapToDouble(Double::doubleValue).toArray();
        info.items.stream().forEach((ts) -> {
            if (ts.hasData()) {
                TsData data = ts.data.movingAverage(w, centered, rescale);
                if (data != null) {
                    coll.add(TsFactory.instance.createTs(ts.name, null, data));
                } else {
                    coll.add(TsFactory.instance.createTs(ts.name));
                }
            } else {
                coll.add(TsFactory.instance.createTs(ts.name));
            }
        });
        // Format results
        ExcelSeries response = RestUtils.toExcelSeries(new TsCollectionInformation(coll, TsInformationType.Data));
        return Response.ok().entity(response).build();
    }

    @POST
    @Path("/movingavg2")
    @Compress
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Performs a moving average on the given series", response = ExcelSeries.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing", response = ExcelSeries.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response movingAverage2(@ApiParam(name = "Series", required = true) ExcelSeries series,
            @ApiParam(name = "mw", required = true) @QueryParam(value = "mw") int mw,
            @ApiParam(name = "nw", required = true) @QueryParam(value = "nw") int nw,
            @ApiParam(name = "centered", defaultValue = "true") @QueryParam(value = "centered") @DefaultValue("true") boolean centered,
            @ApiParam(name = "rescale", defaultValue = "true") @QueryParam(value = "rescale") @DefaultValue("true") boolean rescale) {

        TsCollectionInformation info = RestUtils.readExcelSeries(series);
        TsCollection coll = TsFactory.instance.createTsCollection("results");
        double[] w = buildArray(mw, nw);
        info.items.stream().forEach((ts) -> {
            if (ts.hasData()) {
                TsData data = ts.data.movingAverage(w, centered, rescale);
                if (data != null) {
                    coll.add(TsFactory.instance.createTs(ts.name, null, data));
                } else {
                    coll.add(TsFactory.instance.createTs(ts.name));
                }
            } else {
                coll.add(TsFactory.instance.createTs(ts.name));
            }
        });
        // Format results
        ExcelSeries response = RestUtils.toExcelSeries(new TsCollectionInformation(coll, TsInformationType.Data));
        return Response.ok().entity(response).build();
    }

    private double[] buildArray(int mw, int nw) {
        double[] result = new double[mw + nw - 2];
        for (int n = 0; n < nw - 1; n++) {
            for (int m = 0; m < mw - 1; m++) {
                result[n + m] = result[n + m] + 1;
            }
        }
        return result;
    }

    @POST
    @Path("/movingmedian")
    @Compress
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Performs a moving median on the given series", response = ExcelSeries.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful read", response = ExcelSeries.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response movingMedian(@ApiParam(name = "Series", required = true) ExcelSeries series,
            @ApiParam(name = "nPeriods", required = true) @QueryParam(value = "nPeriods") int nPeriods,
            @ApiParam(name = "centered", defaultValue = "true") @QueryParam(value = "centered") @DefaultValue("true") boolean centered) {

        TsCollectionInformation info = RestUtils.readExcelSeries(series);
        TsCollection coll = TsFactory.instance.createTsCollection("results");
        info.items.stream().forEach((ts) -> {
            if (ts.hasData()) {
                TsData data = ts.data;
                TsData newData = data.movingMedian(nPeriods, centered);
                if (newData != null) {
                    coll.add(TsFactory.instance.createTs(ts.name, null, newData));
                } else {
                    coll.add(TsFactory.instance.createTs(ts.name));
                }
            } else {
                coll.add(TsFactory.instance.createTs(ts.name));
            }
        });
        // Format results
        ExcelSeries response = RestUtils.toExcelSeries(new TsCollectionInformation(coll, TsInformationType.Data));
        return Response.ok().entity(response).build();
    }
}
