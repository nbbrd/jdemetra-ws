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

    @JsonProperty(value = "Identifier")
    private String identifier;
    @JsonProperty(value = "Target")
    private double target;
    @JsonProperty(value = "Mode")
    private String mode;
    @JsonProperty(value = "Elements")
    private BalanceLinearConstraintElement[] elements;

    @JsonProperty(value = "Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty(value = "Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty(value = "Target")
    public double getTarget() {
        return target;
    }

    @JsonProperty(value = "Target")
    public void setTarget(double target) {
        this.target = target;
    }

    @JsonProperty(value = "Mode")
    public String getMode() {
        return mode;
    }

    @JsonProperty(value = "Mode")
    public void setMode(String mode) {
        this.mode = mode;
    }

    @JsonProperty(value = "Elements")
    public BalanceLinearConstraintElement[] getElements() {
        return elements;
    }

    @JsonProperty(value = "Elements")
    public void setElements(BalanceLinearConstraintElement[] elements) {
        this.elements = elements;
    }

}
