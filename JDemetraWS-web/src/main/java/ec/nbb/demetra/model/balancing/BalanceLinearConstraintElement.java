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
public class BalanceLinearConstraintElement {

    @JsonProperty(value = "Per")
    private int period;
    @JsonProperty(value = "Br")
    private String branch;
    @JsonProperty(value = "Prd")
    private String product;
    @JsonProperty(value = "F")
    private char flow;
    @JsonProperty(value = "Var")
    private char variable;
    @JsonProperty(value = "Fct")
    private double factor;

    @JsonProperty(value = "Per")
    public int getPeriod() {
        return period;
    }

    @JsonProperty(value = "Per")
    public void setPeriod(int period) {
        this.period = period;
    }

    @JsonProperty(value = "Br")
    public String getBranch() {
        return branch;
    }

    @JsonProperty(value = "Br")
    public void setBranch(String branch) {
        this.branch = branch;
    }

    @JsonProperty(value = "Prd")
    public String getProduct() {
        return product;
    }

    @JsonProperty(value = "Prd")
    public void setProduct(String product) {
        this.product = product;
    }

    @JsonProperty(value = "F")
    public char getFlow() {
        return flow;
    }

    @JsonProperty(value = "F")
    public void setFlow(char flow) {
        this.flow = flow;
    }

    @JsonProperty(value = "Var")
    public char getVariable() {
        return variable;
    }

    @JsonProperty(value = "Var")
    public void setVariable(char variable) {
        this.variable = variable;
    }

    @JsonProperty(value = "Fct")
    public double getFactor() {
        return factor;
    }

    @JsonProperty(value = "Fct")
    public void setFactor(double factor) {
        this.factor = factor;
    }
}
