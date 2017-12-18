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

import ec.tstoolkit.timeseries.TsAggregationType;

/**
 *
 * @author Mats Maggi
 */
public class DentonSpecification {

    private boolean mul = true, modified = true;
    private int differencing = 1;
    private TsAggregationType type = TsAggregationType.Average;

    /**
     * @return the mul
     */
    public boolean isMultiplicative() {
        return mul;
    }

    /**
     * @param mul the mul to set
     */
    public void setMultiplicative(boolean mul) {
        this.mul = mul;
    }

    /**
     * @return the modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * @return the differencing
     */
    public int getDifferencing() {
        return differencing;
    }

    /**
     * @param differencing the differencing to set
     */
    public void setDifferencing(int differencing) {
        this.differencing = differencing;
    }

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
}
