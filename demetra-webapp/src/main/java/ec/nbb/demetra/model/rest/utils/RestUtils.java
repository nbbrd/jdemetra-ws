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
package ec.nbb.demetra.model.rest.utils;

import ec.nbb.demetra.json.excel.ExcelSeries;
import ec.tss.TsCollectionInformation;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetFactory;
import ec.tss.tsproviders.spreadsheet.engine.TsExportOptions;
import ec.tss.tsproviders.spreadsheet.engine.TsImportOptions;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.ObsGathering;
import ec.tstoolkit.data.SubArray;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.util.spreadsheet.helpers.ArraySheet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Mats Maggi
 */
public class RestUtils {

    private static final TsImportOptions OPTIONS = TsImportOptions.create(DataFormat.create(null, "yyyy-MM-dd", null), ObsGathering.DEFAULT);
    private static final TsExportOptions EXPORT = TsExportOptions.create(true, true, true, true);
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static TsDomain createTsDomain(int start, int end, TsFrequency freq) {
        TsPeriod s = toPeriod(start, freq);
        TsPeriod e = toPeriod(end, freq);
        return new TsDomain(s, e.minus(s));
    }

    public static TsPeriod toPeriod(int period, TsFrequency freq) {
        if (freq.equals(TsFrequency.Undefined)) {
            freq = TsFrequency.Monthly;
        }
        TsPeriod p = new TsPeriod(freq);
        p.set(period / freq.intValue(), period % freq.intValue());
        return p;
    }

    public static int fromTsPeriod(TsPeriod p) {
        int y = p.getYear();
        int pos = p.getPosition();
        int id = y * p.getFrequency().intValue() + pos;
        return id;
    }

    public static TsCollectionInformation readExcelSeries(ExcelSeries series) {
        List<List<Double>> data = series.getData();
        List<String> periods;
        SpreadSheetFactory gridFactory = SpreadSheetFactory.getDefault();
        if (series.getPeriods() == null || series.getPeriods().isEmpty()) {
            int size = 0;
            if (data != null && !data.isEmpty() && data.get(0) != null) {
                size = data.get(0).size();
            }
            TsDomain dom = new TsDomain(new TsPeriod(TsFrequency.valueOf(
                    series.getFreq()),
                    series.getFirstYear(),
                    series.getFirstPeriod()), size);
            periods = dom
                    .stream()
                    .map(x -> ((TsPeriod) x).firstday().toString())
                    .collect(Collectors.toList());
        } else {
            periods = series.getPeriods();
        }

        ArraySheet.Builder asb = ArraySheet.builder();
        if (series.getNames() != null && !series.getNames().isEmpty()) {
            asb.row(0, 1, series.getNames());
        } else {
            asb.row(0, 1, IntStream.rangeClosed(1, series.getData().size())
                    .mapToObj(i -> "S" + i)
                    .collect(Collectors.toList()));
        }

        asb.column(1, 0, periods)
                .name("Sheet");

        for (int i = 0; i < series.getData().size(); i++) {
            asb.column(1, i + 1, series.getData().get(i));
        }
        return gridFactory.toTsCollectionInfo(asb.build(), OPTIONS);
    }

    public static ExcelSeries toExcelSeries(TsCollectionInformation coll) {
        ExcelSeries s = new ExcelSeries();
        SpreadSheetFactory gridFactory = SpreadSheetFactory.getDefault();
        ArraySheet sheet = gridFactory.fromTsCollectionInfo(coll, EXPORT);
        Table t = gridFactory.toTable(sheet);
        
        // periods
        SubArray<Date> p = t.column(0);
        List<String> periods = new ArrayList<>();
        for (int i = 1; i < p.getLength(); i++) {
            periods.add(FORMATTER.format(p.get(i)));
        }
        s.setPeriods(periods);
        
        // data
        List<List<Double>> data = new ArrayList<>();
        for (int i = 1; i < t.getColumnsCount(); i++) {
            SubArray<Double> col = t.column(i);
            List<Double> d = new ArrayList<>();
            for (int j = 1; j < t.getRowsCount(); j++) {
                d.add(col.get(j));
            }
            data.add(d);
        }
        s.setData(data);
        
        // names
        SubArray<String> n = t.row(0);
        List<String> names = new ArrayList<>();
        for (int i = 1; i < n.getLength(); i++) {
            names.add(n.get(i));
        }
        s.setNames(names);
        
        return s;
    }
    
    public static ExcelSeries toExcelSeries(TsData ts, String name) {
        ExcelSeries s = new ExcelSeries();
        TsDomain dom = ts.getDomain();
        // periods
        List<String> periods = dom.stream()
                .map(p -> FORMATTER.format(p.middle()))
                .collect(Collectors.toList());
        s.setPeriods(periods);

        // data
        List<List<Double>> data = new ArrayList<>();
        data.add(ts.stream().map(obs -> obs.getValue()).collect(Collectors.toList()));
        s.setData(data);
        
        // names
        List<String> names = new ArrayList<>();
        names.add(name);
        s.setNames(names);
        
        return s;
    }
}
