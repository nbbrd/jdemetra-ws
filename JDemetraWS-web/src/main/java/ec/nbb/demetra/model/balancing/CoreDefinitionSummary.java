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
public class CoreDefinitionSummary {

    @JsonProperty(value = "Period")
    private int period;
    @JsonProperty(value = "Branch")
    private String branch;
    @JsonProperty(value = "Product")
    private String product;
    @JsonProperty(value = "Flow")
    private char flow;
    @JsonProperty(value = "Variable")
    private char variable;
    @JsonProperty(value = "Value")
    private double value;
    @JsonProperty(value = "LowerBound")
    private double lowerBound;
    @JsonProperty(value = "UpperBound")
    private double upperBound;

    @JsonProperty(value = "Period")
    public int getPeriod() {
        return period;
    }

    @JsonProperty(value = "Period")
    public void setPeriod(int period) {
        this.period = period;
    }

    @JsonProperty(value = "Branch")
    public String getBranch() {
        return branch;
    }

    @JsonProperty(value = "Branch")
    public void setBranch(String branch) {
        this.branch = branch;
    }

    @JsonProperty(value = "Product")
    public String getProduct() {
        return product;
    }

    @JsonProperty(value = "Product")
    public void setProduct(String product) {
        this.product = product;
    }

    @JsonProperty(value = "Flow")
    public char getFlow() {
        return flow;
    }

    @JsonProperty(value = "Flow")
    public void setFlow(char flow) {
        this.flow = flow;
    }

    @JsonProperty(value = "Variable")
    public char getVariable() {
        return variable;
    }

    @JsonProperty(value = "Variable")
    public void setVariable(char variable) {
        this.variable = variable;
    }

    @JsonProperty(value = "Value")
    public double getValue() {
        return value;
    }

    @JsonProperty(value = "Value")
    public void setValue(double value) {
        this.value = value;
    }

    @JsonProperty(value = "LowerBound")
    public double getLowerBound() {
        return lowerBound;
    }

    @JsonProperty(value = "LowerBound")
    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    @JsonProperty(value = "UpperBound")
    public double getUpperBound() {
        return upperBound;
    }

    @JsonProperty(value = "UpperBound")
    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }
}
