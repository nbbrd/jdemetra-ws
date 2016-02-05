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
public class BalanceLinearConstraint {

    @JsonProperty(value = "Id")
    private String identifier;
    @JsonProperty(value = "Tgt")
    private double target;
    @JsonProperty(value = "M")
    private String mode;
    @JsonProperty(value = "Els")
    private BalanceLinearConstraintElement[] elements;
    @JsonProperty(value = "P")
    private int priority;
    @JsonProperty(value = "W")
    private double weigth;

    @JsonProperty(value = "Id")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty(value = "Id")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty(value = "Tgt")
    public double getTarget() {
        return target;
    }

    @JsonProperty(value = "Tgt")
    public void setTarget(double target) {
        this.target = target;
    }

    @JsonProperty(value = "M")
    public String getMode() {
        return mode;
    }

    @JsonProperty(value = "M")
    public void setMode(String mode) {
        this.mode = mode;
    }

    @JsonProperty(value = "Els")
    public BalanceLinearConstraintElement[] getElements() {
        return elements;
    }

    @JsonProperty(value = "Els")
    public void setElements(BalanceLinearConstraintElement[] elements) {
        this.elements = elements;
    }

    @JsonProperty(value = "P")
    public int getPriority() {
        return priority;
    }

    @JsonProperty(value = "P")
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @JsonProperty(value = "W")
    public double getWeigth() {
        return weigth;
    }

    @JsonProperty(value = "W")
    public void setWeigth(double weigth) {
        this.weigth = weigth;
    }
 
}
