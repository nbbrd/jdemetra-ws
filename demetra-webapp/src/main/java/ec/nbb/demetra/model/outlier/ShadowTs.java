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
package ec.nbb.demetra.model.outlier;

import com.fasterxml.jackson.annotation.JsonProperty;
import ec.tstoolkit.timeseries.TsAggregationType;
import io.swagger.annotations.ApiModel;

/**
 *
 * @author Mats Maggi
 */
@ApiModel(description = "Json definition of Time Series. It contains a name, a frequency, an array of periods and the corresponding array of values, and the aggregation type for the ts creation")
public class ShadowTs {

    @JsonProperty(value = "Name")
    private String name;
    @JsonProperty(value = "Freq")
    private int freq;
    @JsonProperty(value = "Periods")
    private int[] periods;
    @JsonProperty(value = "Values")
    private double[] values;
    @JsonProperty(value = "AggregationMethod")
    private TsAggregationType aggregationMethod;

    @JsonProperty(value = "Name")
    public String getName() {
        return name;
    }

    @JsonProperty(value = "Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value = "Freq")
    public int getFreq() {
        return freq;
    }

    @JsonProperty(value = "Freq")
    public void setFreq(int freq) {
        this.freq = freq;
    }

    @JsonProperty(value = "Periods")
    public int[] getPeriods() {
        return periods;
    }

    @JsonProperty(value = "Periods")
    public void setPeriods(int[] periods) {
        this.periods = periods;
    }

    @JsonProperty(value = "Values")
    public double[] getValues() {
        return values;
    }

    @JsonProperty(value = "Values")
    public void setValues(double[] values) {
        this.values = values;
    }

    @JsonProperty(value = "AggregationMethod")
    public TsAggregationType getAggregationMethod() {
        return aggregationMethod;
    }

    @JsonProperty(value = "AggregationMethod")
    public void setAggregationMethod(TsAggregationType aggregationMethod) {
        this.aggregationMethod = aggregationMethod;
    }
}
