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
import ec.tstoolkit.timeseries.regression.OutlierType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Mats Maggi
 */
@ApiModel(description = "Json definition of an outlier")
public class ShadowOutlier {

    @ApiModelProperty(notes = "OutlierType", allowableValues = "AO, SO, LS, TC")
    @JsonProperty(value = "OutlierType")
    private OutlierType outlierType;
    @JsonProperty(value = "Period")
    private int period;
    @JsonProperty(value = "Value")
    public double value;
    @JsonProperty(value = "StDev")
    public double stdev;

    @JsonProperty(value = "OutlierType")
    public OutlierType getOutlierType() {
        return outlierType;
    }

    @JsonProperty(value = "OutlierType")
    public void setOutlierType(OutlierType outlierType) {
        this.outlierType = outlierType;
    }

    @JsonProperty(value = "Period")
    public int getPeriod() {
        return period;
    }

    @JsonProperty(value = "Period")
    public void setPeriod(int period) {
        this.period = period;
    }

    @JsonProperty(value = "Value")
    public double getValue() {
        return value;
    }

    @JsonProperty(value = "Value")
    public void setValue(double value) {
        this.value = value;
    }

    @JsonProperty(value = "StDev")
    public double getStdev() {
        return stdev;
    }

    @JsonProperty(value = "StDev")
    public void setStdev(double stdev) {
        this.stdev = stdev;
    }
}
