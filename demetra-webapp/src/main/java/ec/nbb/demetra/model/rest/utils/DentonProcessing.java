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
package ec.nbb.demetra.model.rest.utils;

import ec.benchmarking.denton.DentonMethod;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

/**
 * New Denton processing methods
 *
 * @author Mats Maggi
 */
public class DentonProcessing {

    public static TsData process(TsData q, TsData Y, DentonSpecification spec) {
        DentonMethod denton = new DentonMethod();
        denton.setAggregationType(spec.getType());
        denton.setDifferencingOrder(spec.getDifferencing());
        denton.setMultiplicative(spec.isMultiplicative());
        denton.setModifiedDenton(spec.isModified());
        int yfreq = Y.getFrequency().intValue();
        int qfreq = q.getFrequency().intValue();
        if (qfreq % yfreq != 0) {
            return null;
        }
        denton.setConversionFactor(qfreq / yfreq);
        // Y is limited to q !
        TsPeriodSelector qsel = new TsPeriodSelector();
        qsel.between(q.getStart().firstday(), q.getLastPeriod().lastday());
        Y = Y.select(qsel);
        TsPeriod q0 = q.getStart(), yq0 = new TsPeriod(q0.getFrequency());
        yq0.set(Y.getStart().firstday());
        denton.setOffset(yq0.minus(q0));
        double[] r = denton.process(q, Y);
        return new TsData(q.getStart(), r, false);
    }

    public static TsData process(TsData Y, TsFrequency f, DentonSpecification spec) {
        DentonMethod denton = new DentonMethod();
        denton.setAggregationType(spec.getType());
        denton.setDifferencingOrder(spec.getDifferencing());
        denton.setMultiplicative(spec.isMultiplicative());
        denton.setModifiedDenton(spec.isModified());
        int yfreq = Y.getFrequency().intValue();
        int qfreq = f.intValue();
        if (qfreq % yfreq != 0) {
            return null;
        }
        denton.setConversionFactor(qfreq / yfreq);
        TsPeriod qstart = Y.getStart().firstPeriod(f);
        double[] r = denton.process(Y);
        return new TsData(qstart, r, false);
    }
}
