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
package ec.nbb.demetra.json.excel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 *
 * @author Mats Maggi
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "periods",
    "data",
    "firstYear",
    "firstPeriod",
    "freq"
})
public class ExcelSeries {

    @JsonProperty("periods")
    private List<String> periods = null;
    @JsonProperty("data")
    private List<List<Double>> data = null;
    @JsonProperty("names")
    private List<String> names = null;
    @JsonProperty("firstYear")
    private Integer firstYear;
    @JsonProperty("firstPeriod")
    private Integer firstPeriod;
    @JsonProperty("freq")
    private Integer freq;

    @JsonProperty("periods")
    public List<String> getPeriods() {
        return periods;
    }

    @JsonProperty("periods")
    public void setPeriods(List<String> periods) {
        this.periods = periods;
    }

    @JsonProperty("data")
    public List<List<Double>> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<List<Double>> data) {
        this.data = data;
    }

    @JsonProperty("names")
    public List<String> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<String> names) {
        this.names = names;
    }

    @JsonProperty("firstYear")
    public Integer getFirstYear() {
        return firstYear;
    }

    @JsonProperty("firstYear")
    public void setFirstYear(Integer firstYear) {
        this.firstYear = firstYear;
    }

    @JsonProperty("firstPeriod")
    public Integer getFirstPeriod() {
        return firstPeriod;
    }

    @JsonProperty("firstPeriod")
    public void setFirstPeriod(Integer firstPeriod) {
        this.firstPeriod = firstPeriod;
    }

    @JsonProperty("freq")
    public Integer getFreq() {
        return freq;
    }

    @JsonProperty("freq")
    public void setFreq(Integer freq) {
        this.freq = freq;
    }
}
