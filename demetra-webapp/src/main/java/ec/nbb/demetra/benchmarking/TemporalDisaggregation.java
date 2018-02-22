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
package ec.nbb.demetra.benchmarking;

import ec.benchmarking.DisaggregationModel;
import ec.benchmarking.simplets.TsDisaggregation;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.arima.ArimaModel;
import ec.tstoolkit.maths.linearfilters.BackFilter;
import ec.tstoolkit.maths.polynomials.UnitRoots;
import ec.tstoolkit.ssf.ISsf;
import ec.tstoolkit.ssf.arima.SsfAr1;
import ec.tstoolkit.ssf.arima.SsfArima;
import ec.tstoolkit.ssf.arima.SsfRw;
import ec.tstoolkit.ssf.arima.SsfRwAr1;
import ec.tstoolkit.timeseries.regression.Constant;
import ec.tstoolkit.timeseries.regression.LinearTrend;
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariableList;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;

/**
 *
 * @author Mats Maggi
 */
public class TemporalDisaggregation {

    public static TempDisaggOutput process(TsData y, TsData[] x, TempDisaggSpec spec) {
        DisaggregationModel model = prepare(y, x, spec);
        if (model == null) {
            return null;
        }
        TsDisaggregation<? extends ISsf> disagg;

        switch (spec.getModel()) {
            case Ar1:
                disagg = initChowLin(spec);
                break;
            case Wn:
                disagg = initOLS();
                break;
            case RwAr1:
                disagg = initLitterman(spec);
                break;
            case Rw:
                disagg = initFernandez();
                break;
            default:
                disagg = initI(spec.getModel().getDifferencingOrder());
                break;
        }

        disagg.calculateVariance(true);

        if (!disagg.process(model, null)) {
            return null;
        } else {
            return new TempDisaggOutput(disagg, spec);
        }
    }

    private static TsDisaggregation<SsfAr1> initChowLin(TempDisaggSpec spec) {
        TsDisaggregation<SsfAr1> disagg = new TsDisaggregation<>();
        SsfAr1 ssf = new SsfAr1();
        Parameter p = spec.getParameter();
        if (p != null && p.isFixed()) {
            ssf.setRho(p.getValue());
        } else {
            disagg.setMapping(new SsfAr1.Mapping(false, spec.getTruncatedRho(), 1));
        }
        disagg.setSsf(ssf);
        return disagg;
    }

    private static TsDisaggregation<SsfAr1> initOLS() {
        TsDisaggregation<SsfAr1> disagg = new TsDisaggregation<>();
        SsfAr1 ssf = new SsfAr1();
        ssf.setRho(0);
        disagg.setSsf(ssf);
        return disagg;
    }

    private static TsDisaggregation<SsfRwAr1> initLitterman(TempDisaggSpec spec) {
        TsDisaggregation<SsfRwAr1> disagg = new TsDisaggregation<>();
        SsfRwAr1 ssf = new SsfRwAr1();
        Parameter p = spec.getParameter();
        if (p != null && p.isFixed()) {
            ssf.setRho(p.getValue());
        } else {
            disagg.setMapping(new SsfRwAr1.Mapping(false, spec.getTruncatedRho(), 1));
        }
        disagg.setSsf(ssf);
        return disagg;
    }

    private static TsDisaggregation<SsfRw> initFernandez() {
        TsDisaggregation<SsfRw> disagg = new TsDisaggregation<>();
        SsfRw ssf = new SsfRw();
        disagg.setSsf(ssf);
        return disagg;
    }

    private static TsDisaggregation<SsfArima> initI(int diff) {
        TsDisaggregation<SsfArima> disagg = new TsDisaggregation<>();
        ArimaModel sarima = new ArimaModel(null, new BackFilter(UnitRoots.D(1, diff)), null, 1);
        SsfArima ssf = new SsfArima(sarima);
        disagg.setSsf(ssf);
        return disagg;
    }

    private static DisaggregationModel prepare(TsData y, TsData[] x, TempDisaggSpec spec) {
        if (y == null) {
            return null;
        }
        DisaggregationModel model = new DisaggregationModel(spec.getDefaultFrequency());
        model.setY(y);
        if (x == null || x.length == 0) {
            if (spec.getDefaultFrequency() == TsFrequency.Undefined || !y.getFrequency().contains(spec.getDefaultFrequency())) {
                return null;
            } else {
                model.setDefaultForecastCount(spec.getDefaultFrequency().intValue());
            }
        }

        TsVariableList vars = new TsVariableList();
        if (spec.isConstant() && (spec.getModel().isStationary())) {
            vars.add(new Constant());
        }
        if (spec.isTrend()) {
            vars.add(new LinearTrend(y.getStart().firstday()));
        }
        if (x != null) {
            for (int i = 0; i < x.length; ++i) {
                vars.add(new TsVariable("var-" + i, x[i]));
            }
        }
        if (!vars.isEmpty()) {
            model.setX(vars);
        }
        model.setAggregationType(spec.getType());
        return model;
    }
}
