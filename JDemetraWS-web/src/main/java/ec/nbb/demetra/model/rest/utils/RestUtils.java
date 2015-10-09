/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbb.demetra.model.rest.utils;

import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.TsException;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataCollector;
import ec.tstoolkit.timeseries.simplets.TsDataIterator;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Mats Maggi
 */
public class RestUtils {

    public static TsData createTsData(ShadowTs ts) {
        if (ts == null
                || ts.getPeriods() == null || ts.getPeriods().length == 0
                || ts.getValues() == null || ts.getValues().length == 0) {
            throw new IllegalArgumentException("The Ts is empty !");
        }
        if (ts.getPeriods().length != ts.getValues().length) {
            throw new IllegalArgumentException("Number of observations doesn't match number of periods !");
        }

        TsAggregationType aggType = ts.getAggregationMethod();
        TsFrequency freq = TsFrequency.Monthly;
        TsDataCollector coll = new TsDataCollector();
        TsPeriod p = new TsPeriod(freq);
        for (int i = 0; i < ts.getPeriods().length; i++) {
            int id = ts.getPeriods()[i];
            int year = id / 12;
            int pos = id % 12;
            try {
                p.set(year, pos);
            } catch (TsException ex) {
                System.out.println("==> Invalid period : " + id + " - freq : " + ts.getFreq() + " - year : " + year + " - pos : " + pos);
                throw ex;
            }
            coll.addObservation(new Date(p.middle().getTime()), ts.getValues()[i]);
        }
        return coll.make(freq, aggType);
    }

    public static ShadowTs toShadowTs(String name, TsData tsData) {
        ShadowTs ts = new ShadowTs();
        if (name != null) {
            ts.setName(name);
        }
        int freq = tsData.getFrequency().intValue();
        ts.setFreq(freq);
        List<Integer> periods = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        TsDataIterator it = new TsDataIterator(tsData);
        it.setSkippingMissings(true);

        while (it.hasMoreElements()) {
            TsObservation obs = it.nextElement();
            int y = obs.getPeriod().getYear();
            int pos = obs.getPeriod().getPosition();
            int id = y * 12 + pos;
            periods.add(id);
            values.add(obs.getValue());
        }

        ts.setPeriods(ArrayUtils.toPrimitive(periods.toArray(new Integer[periods.size()])));
        ts.setValues(ArrayUtils.toPrimitive(values.toArray(new Double[values.size()])));

        return ts;
    }
}
