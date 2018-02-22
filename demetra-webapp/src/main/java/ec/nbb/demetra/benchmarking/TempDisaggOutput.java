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

import ec.benchmarking.simplets.TsDisaggregation;
import ec.tstoolkit.data.AutoCorrelations;
import ec.tstoolkit.ssf.ISsf;
import ec.tstoolkit.timeseries.simplets.TsData;

/**
 *
 * @author Mats Maggi
 */
public class TempDisaggOutput {

    private TsData pred;
    private TsData sePred;
    private double[] coeff;
    private double[] seCoeff;
    private double rho, dw;

    TempDisaggOutput(TsDisaggregation<? extends ISsf> disagg, TempDisaggSpec spec) {
        pred = disagg.getSmoothedSeries();
        sePred = disagg.getSmoothedSeriesVariance().sqrt();
        coeff = disagg.getLikelihood().getB();
        if (coeff != null) {
            seCoeff = new double[coeff.length];
            for (int i = 0; i < seCoeff.length; ++i) {
                seCoeff[i] = disagg.getLikelihood().bser(i, true, 0);
            }
        }
        if (spec.getModel().hasParameter()) {
            if (spec.getParameter().isFixed()) {
                rho = spec.getParameter().getValue();
            } else {
                rho = disagg.getMin().getParameters().get(0);
            }
        }
        
        try {
            TsData res = disagg.getFullResiduals();
            AutoCorrelations stats = new AutoCorrelations(res);
            dw = stats.getDurbinWatson();
        } catch (Exception e) {

        }
    }

    /**
     * @return the result
     */
    public TsData getPred() {
        return pred;
    }

    /**
     * @return the stdresult
     */
    public TsData getSePred() {
        return sePred;
    }

    /**
     * @return the coeff
     */
    public double[] getCoeff() {
        return coeff;
    }

    /**
     * @return the ecoeff
     */
    public double[] getSeCoeff() {
        return seCoeff;
    }

    public double[] getTStats() {
        if (coeff == null) {
            return null;
        }
        double[] t = coeff.clone();
        for (int i = 0; i < t.length; ++i) {
            t[i] /= seCoeff[i];
        }
        return t;
    }

    /**
     * @return the rho
     */
    public double getRho() {
        return rho;
    }

    /**
     * @return the dw
     */
    public double getDw() {
        return dw;
    }
}
