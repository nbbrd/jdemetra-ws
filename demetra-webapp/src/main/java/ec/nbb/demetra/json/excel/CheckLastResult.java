/*
 * Copyright 2014 National Bank of Belgium
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
package ec.nbb.demetra.json.excel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Object used to hold the results of a CheckLast
 *
 * @author Mats Maggi
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "value",
    "forecast",
    "score"
})
public class CheckLastResult {

    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private double[] value;
    @JsonProperty("forecast")
    private double[] forecast;
    @JsonProperty("score")
    private double[] score;

    public CheckLastResult(String name, double[] value, double[] forecast, double[] score) {
        this.name = name;
        this.value = value;
        this.forecast = forecast;
        this.score = score;
    }
}
