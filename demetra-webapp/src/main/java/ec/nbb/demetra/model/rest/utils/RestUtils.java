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

import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.TsException;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataCollector;
import ec.tstoolkit.timeseries.simplets.TsDataIterator;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Mats Maggi
 */
@Path("/test/xml")
@Api(value = "/test/xml")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class RestUtils {

    public static TsData createTsData(ShadowTs ts) {
        if (ts == null
                || ts.getPeriods() == null || ts.getPeriods().length == 0
                || ts.getValues() == null || ts.getValues().length == 0) {
            System.out.println("The provided Ts is empty !");
            throw new IllegalArgumentException("The Ts is empty !");
        }
        if (ts.getPeriods().length != ts.getValues().length) {
            System.out.println("Number of observations (" + ts.getValues().length 
                    + ") doesn't match number of periods (" + ts.getPeriods().length + ") !");
            throw new IllegalArgumentException("Number of observations (" + ts.getValues().length 
                    + ") doesn't match number of periods (" + ts.getPeriods().length + ") !");
        }

        TsAggregationType aggType = ts.getAggregationMethod();
        TsDataCollector coll = new TsDataCollector();
        TsPeriod p;

        for (int i = 0; i < ts.getPeriods().length; i++) {
            int id = ts.getPeriods()[i];
            try {
                p = toPeriod(id, TsFrequency.valueOf(ts.getFreq()));
            } catch (TsException ex) {
                System.out.println("==> Invalid period : " + id + " - freq : " + ts.getFreq());
                throw ex;
            }
            coll.addObservation(new Date(p.middle().getTime()), ts.getValues()[i]);
        }
        return coll.make(TsFrequency.Undefined, aggType);
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
            periods.add(fromTsPeriod(obs.getPeriod()));
            values.add(obs.getValue());
        }

        ts.setPeriods(ArrayUtils.toPrimitive(periods.toArray(new Integer[periods.size()])));
        ts.setValues(ArrayUtils.toPrimitive(values.toArray(new Double[values.size()])));

        return ts;
    }

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
}
