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
package ec.nbb.demetra.model.balancing;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Mats Maggi
 */
public class Summary {

    @JsonProperty(value = "DimensionsCount")
    private int dimensionsCount;

    @JsonProperty(value = "Dimensions")
    private DimensionSummary[] dimensions;

    @JsonProperty(value = "Data")
    private CoreDefinitionSummary[] data;

    @JsonProperty(value = "Constraints")
    private BalanceLinearConstraint[] constraints;

    @JsonProperty(value = "Constraints")
    public BalanceLinearConstraint[] getConstraints() {
        return constraints;
    }

    @JsonProperty(value = "Data")
    public CoreDefinitionSummary[] getData() {
        return data;
    }

    @JsonProperty(value = "DimensionsCount")
    public int getDimensionsCount() {
        return dimensionsCount;
    }

    @JsonProperty(value = "Dimensions")
    public DimensionSummary[] getDimensions() {
        return dimensions;
    }

    @JsonProperty(value = "Constraints")
    public void setConstraints(BalanceLinearConstraint[] constraints) {
        this.constraints = constraints;
    }

    @JsonProperty(value = "Data")
    public void setData(CoreDefinitionSummary[] data) {
        this.data = data;
    }

    @JsonProperty(value = "DimensionsCount")
    public void setDimensionsCount(int dimensionCount) {
        this.dimensionsCount = dimensionCount;
    }

    @JsonProperty(value = "Dimensions")
    public void setDimensions(DimensionSummary[] dimensions) {
        this.dimensions = dimensions;
    }
}
