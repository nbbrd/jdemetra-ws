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

import ec.nbb.demetra.json.excel.ColorAnalyserOutput;
import ec.nbb.demetra.json.excel.ExcelSeries;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.nbb.ws.annotations.Compress;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.arima.IPreprocessor;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.modelling.arima.tramo.CalendarSpec;
import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
import ec.tstoolkit.modelling.arima.tramo.OutlierSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.modelling.arima.tramo.TransformSpec;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import ec.tstoolkit.timeseries.regression.MissingValueEstimation;
import ec.tstoolkit.timeseries.regression.OutlierEstimation;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
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
@Path("/coloranalyser")
@Api(value = "/coloranalyser")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ColorAnalyserResource {

    static Map<String, ColorAnalyserOutput> process(ExcelSeries series, ColorAnalyserSpec spec) {
        Map<String, ColorAnalyserOutput> rslts = new HashMap<>();
        TramoSpecification tsspec = of(spec);
        IPreprocessor preprocessor = tsspec.build();
        TsCollectionInformation coll = RestUtils.readExcelSeries(series);

        for (TsInformation info : coll.items) {
            PreprocessingModel model = preprocessor.process(info.data, null);
            rslts.put(info.name, of(model));
        }

        return rslts;
    }

    static TramoSpecification of(ColorAnalyserSpec input) {
        // transformation
        TramoSpecification spec = new TramoSpecification();
        TransformSpec transform = spec.getTransform();
        switch (input.transformation) {
            case -1:
                transform.setFunction(DefaultTransformationType.Auto);
                break;
            case 0:
                transform.setFunction(DefaultTransformationType.Log);
                break;
            default:
                transform.setFunction(DefaultTransformationType.None);
                break;
        }
        // calendar
        CalendarSpec calendar = spec.getRegression().getCalendar();
        switch (input.calendarEffect) {
            case -1:
                calendar.getTradingDays().setTradingDaysType(TradingDaysType.TradingDays);
                calendar.getTradingDays().setLeapYear(true);
                calendar.getTradingDays().setAutomatic(true);
                calendar.getEaster().setOption(EasterSpec.Type.IncludeEaster);
                calendar.getEaster().setTest(true);
                break;
            case 0:
                calendar.getTradingDays().disable();
                calendar.getEaster().setOption(EasterSpec.Type.Unused);
                break;
            case 1:
                calendar.getTradingDays().setTradingDaysType(TradingDaysType.WorkingDays);
                calendar.getTradingDays().setLeapYear(true);
                calendar.getEaster().setOption(EasterSpec.Type.IncludeEaster);
                break;
            case 2:
                calendar.getTradingDays().setTradingDaysType(TradingDaysType.TradingDays);
                calendar.getTradingDays().setLeapYear(true);
                calendar.getEaster().setOption(EasterSpec.Type.IncludeEaster);
                break;
        }

        // ami
        if (input.ami) {
            spec.setUsingAutoModel(true);
        } else {
            spec.getArima().airline();
        }
        // outliers
        OutlierSpec outliers = spec.getOutliers();
        outliers.clearTypes();
        if (input.AO) {
            outliers.add(OutlierType.AO);
        }
        if (input.TC) {
            outliers.add(OutlierType.TC);
        }
        if (input.LS) {
            outliers.add(OutlierType.LS);
        }
        if (input.SO) {
            outliers.add(OutlierType.SO);
        }
        outliers.setCriticalValue(input.criticalValue);
        return spec;
    }

    private static ColorAnalyserOutput of(PreprocessingModel model) {
        ColorAnalyserOutput o = new ColorAnalyserOutput();
        o.logs = model.isMultiplicative();
        o.arima = model.description.getSpecification();
        TsPeriod start = model.description.getSeriesDomain().getStart();
        OutlierEstimation[] all = model.outliersEstimation(true, false);
        if (all != null) {
            for (OutlierEstimation out : all) {
                ColorAnalyserOutput.Outlier cur = new ColorAnalyserOutput.Outlier();
                cur.code = out.getCode();
                cur.value = out.getValue();
                cur.stdError = out.getStdev();
                cur.position = out.getPosition().minus(start);
                o.outliers.add(cur);
            }
        }
        MissingValueEstimation[] missings = model.missings(true);
        if (missings != null) {
            for (MissingValueEstimation m : missings) {
                ColorAnalyserOutput.Missing cur = new ColorAnalyserOutput.Missing();
                cur.value = m.getValue();
                cur.stdError = m.getStdev();
                cur.position = m.getPosition().minus(start);
                o.missings.add(cur);
            }
        }
        // calendars
        o.td = model.description.countRegressors(var -> var.isCalendar());
        o.easter = model.description.countRegressors(var -> var.isMovingHoliday()) > 0;

        return o;
    }

    @POST
    @Compress
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns the results of the Color Analyser", response = ColorAnalyserOutput.class, responseContainer = "Map")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Color Analyser results successfully processed", response = ColorAnalyserOutput.class, responseContainer = "Map"),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response colorAnalyser(@ApiParam(name = "series", required = true) ExcelSeries series,
            @ApiParam(name = "transformation", defaultValue = "-1") @QueryParam(value = "transformation") @DefaultValue("-1") int transformation,
            @ApiParam(name = "calendarEffect", defaultValue = "-1") @QueryParam(value = "calendarEffect") @DefaultValue("-1") int calendarEffect,
            @ApiParam(name = "ami", defaultValue = "true") @QueryParam(value = "ami") @DefaultValue("true") boolean ami,
            @ApiParam(name = "ao", defaultValue = "true") @QueryParam(value = "ao") @DefaultValue("true") boolean ao,
            @ApiParam(name = "ls", defaultValue = "true") @QueryParam(value = "ls") @DefaultValue("true") boolean ls,
            @ApiParam(name = "tc", defaultValue = "true") @QueryParam(value = "tc") @DefaultValue("true") boolean tc,
            @ApiParam(name = "so", defaultValue = "false") @QueryParam(value = "so") @DefaultValue("false") boolean so,
            @ApiParam(name = "critical", defaultValue = "0") @QueryParam(value = "critical") @DefaultValue("0") double critical) {
        ColorAnalyserSpec spec = new ColorAnalyserSpec();
        spec.transformation = transformation;
        spec.calendarEffect = calendarEffect;
        spec.ami = ami;
        spec.AO = ao;
        spec.LS = ls;
        spec.TC = tc;
        spec.SO = so;
        spec.criticalValue = critical;

        Map<String, ColorAnalyserOutput> results = process(series, spec);
        return Response.ok().entity(results).build();
    }
}

class ColorAnalyserSpec {

    // modelling
    int transformation = -1; // -1 = Pretest, 0 = Logs, 1 = Levels
    int calendarEffect = -1; // -1 = Auto, 0 = None, 1 = WD, 2 = TD 
    boolean ami = true;
    // outliers
    boolean AO = true, LS = true, TC = true, SO = false;
    double criticalValue = 0; // 0 (default) or any number >= 2 

}
