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
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.chart.TsXYDatasets;
import ec.util.chart.ColorScheme;
import ec.util.chart.SeriesFunction;
import ec.util.chart.TimeSeriesChart;
import ec.util.chart.impl.SmartColorScheme;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.chart.swing.SwingColorSchemeSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.ServiceLoader;
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
 * REST resource that creates a chart. The chart is created from a given time
 * series via the POST method or from a randomly generated one via the GET
 * method.
 *
 * @author Mats Maggi
 */
@Path("/chart")
@Api(value = "/chart")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({"image/png", "image/jpeg", "image/svg+xml"})
public class ChartResource {

    @GET
    @Produces({"image/png", "image/jpeg", "image/svg+xml"})
    @ApiOperation(value = "Create a chart image from a randomly generated series. Height, "
            + "width, color scheme, title and the visibility of the legend can be changed.")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "The chart image was succesfully created"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response createChart(
            @ApiParam(name = "w", defaultValue = "400") @QueryParam(value = "w") @DefaultValue("400") int width,
            @ApiParam(name = "h", defaultValue = "300") @QueryParam(value = "h") @DefaultValue("300") int height,
            @ApiParam(name = "scheme", defaultValue = SmartColorScheme.NAME) @QueryParam(value = "scheme") @DefaultValue(SmartColorScheme.NAME) String colorScheme,
            @ApiParam(name = "title") @QueryParam(value = "title") String title,
            @ApiParam(name = "legend", defaultValue = "true") @QueryParam(value = "legend") @DefaultValue("true") boolean legendVisible) throws IOException {
        JTimeSeriesChart chart = create(TsData.random(TsFrequency.Monthly), "MyTsData", width, height, colorScheme, title, legendVisible);

        return Response.ok().entity(chart).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({"image/png", "image/jpeg", "image/svg+xml"})
    @ApiOperation(value = "Create a chart image from a given XmlTsData. Height, "
            + "width, color scheme, title and the visibility of the legend can be changed.")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "The chart image was succesfully created"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response createChart(
            @ApiParam(name = "tsdata", required = true) XmlTsData xmlTsData,
            @ApiParam(name = "w", defaultValue = "400") @QueryParam(value = "w") @DefaultValue("400") int width,
            @ApiParam(name = "h", defaultValue = "300") @QueryParam(value = "h") @DefaultValue("300") int height,
            @ApiParam(name = "scheme", defaultValue = SmartColorScheme.NAME) @QueryParam(value = "scheme") @DefaultValue(SmartColorScheme.NAME) String colorScheme,
            @ApiParam(name = "title") @QueryParam(value = "title") String title,
            @ApiParam(name = "legend", defaultValue = "true") @QueryParam(value = "legend") @DefaultValue("true") boolean legendVisible) throws IOException {
        if (xmlTsData == null) {
            throw new IllegalArgumentException(Messages.TS_NULL);
        }
        TsData data = xmlTsData.create();

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException(Messages.TS_EMPTY);
        }

        JTimeSeriesChart chart = create(data, xmlTsData.name, width, height, colorScheme, title, legendVisible);

        return Response.ok().entity(chart).build();
    }

    private JTimeSeriesChart create(TsData data, String name, int width, int height, String colorScheme, String title, boolean legend) {
        TsXYDatasets.Builder rslt = TsXYDatasets.builder();
        rslt.add(Strings.isNullOrEmpty(name) ? "MySeries" : name, data);

        JTimeSeriesChart chart = new JTimeSeriesChart();
        chart.setSeriesRenderer(SeriesFunction.always(TimeSeriesChart.RendererType.SPLINE));
        chart.setColorSchemeSupport(SwingColorSchemeSupport.from(getColorScheme(colorScheme)));
        chart.setSize(width, height);
        chart.setElementVisible(TimeSeriesChart.Element.LEGEND, legend);

        if (!Strings.isNullOrEmpty(title)) {
            chart.setTitle(title);
        }

        chart.doLayout();
        chart.setDataset(rslt.build());
        return chart;
    }

    private ColorScheme getColorScheme(String name) {
        for (ColorScheme o : ServiceLoader.load(ColorScheme.class)) {
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return new SmartColorScheme();
    }
}
