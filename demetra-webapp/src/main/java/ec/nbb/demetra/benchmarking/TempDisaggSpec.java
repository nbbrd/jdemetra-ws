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

import ec.tstoolkit.Parameter;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;

/**
 *
 * @author Mats Maggi
 */
public class TempDisaggSpec {

    public static enum Model {
        Wn,
        Ar1,
        Rw,
        RwAr1,
        I2, I3;

        public boolean hasParameter() {
            return this == Ar1 || this == RwAr1;
        }

        public boolean isStationary() {
            return this == Ar1 || this == Wn;
        }

        public int getParametersCount() {
            return (this == Ar1 || this == RwAr1) ? 1 : 0;
        }

        public int getDifferencingOrder() {
            switch (this) {
                case Rw:
                case RwAr1:
                    return 1;
                case I2:
                    return 2;
                case I3:
                    return 3;
                default:
                    return 0;
            }
        }
    }

    public static final double DEF_EPS = 1e-5;
    private Model model = Model.Ar1;
    private boolean constant = true, trend = false;
    private Parameter p = new Parameter();
    private double truncated = 0;
    private TsAggregationType type = TsAggregationType.Sum;
    private double eps = DEF_EPS;
    private TsFrequency defaultFrequency = TsFrequency.Quarterly;

    /**
     * @return the type
     */
    public TsAggregationType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TsAggregationType type) {
        this.type = type;
    }

    /**
     * @return the eps
     */
    public double getEpsilon() {
        return eps;
    }

    /**
     * @param eps the eps to set
     */
    public void setEpsilon(double eps) {
        this.eps = eps;
    }

    /**
     * @return the p
     */
    public Parameter getParameter() {
        return p;
    }

    /**
     * @param p the p to set
     */
    public void setParameter(Parameter p) {
        this.p = p;
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * @param aModel the model to set
     */
    public void setModel(Model aModel) {
        model = aModel;
    }

    /**
     * @return the constant
     */
    public boolean isConstant() {
        return constant;
    }

    /**
     * @param constant the constant to set
     */
    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    /**
     * @return the trend
     */
    public boolean isTrend() {
        return trend;
    }

    /**
     * @param trend the trend to set
     */
    public void setTrend(boolean trend) {
        this.trend = trend;
    }

    public double getTruncatedRho() {
        return truncated;
    }

    public void setTruncatedRho(double lrho) {
        if (lrho > 0 || lrho < -1) {
            throw new IllegalArgumentException("Truncated value should be in [-1,0]");
        }
        truncated = lrho;
    }

    public TsFrequency getDefaultFrequency() {
        return defaultFrequency;
    }

    /**
     * @param freq
     */
    public void setDefaultFrequency(TsFrequency freq) {
        this.defaultFrequency = freq;
    }

}
