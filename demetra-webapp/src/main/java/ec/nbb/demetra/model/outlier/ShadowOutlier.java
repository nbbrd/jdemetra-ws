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
import ec.tss.xml.XmlTsPeriod;
import ec.tstoolkit.timeseries.regression.OutlierType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * POJO definition of an outlier used for serialization
 *
 * @author Mats Maggi
 */
@ApiModel(description = "POJO definition of an outlier used for serialization")
@XmlRootElement(name = "ShadowOutlier")
@XmlType(name = "ShadowOutlierType")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShadowOutlier {

    @ApiModelProperty(notes = "OutlierType", allowableValues = "AO, SO, LS, TC")
    @JsonProperty(value = "OutlierType")
    @XmlElement
    private OutlierType outlierType;

    @JsonProperty(value = "Period")
    @XmlElement
    private XmlTsPeriod period;

    @JsonProperty(value = "Value")
    @XmlElement
    public double value;

    @JsonProperty(value = "StDev")
    @XmlElement
    public double stdev;

    public OutlierType getOutlierType() {
        return outlierType;
    }

    public void setOutlierType(OutlierType outlierType) {
        this.outlierType = outlierType;
    }

    public XmlTsPeriod getPeriod() {
        return period;
    }

    public void setPeriod(XmlTsPeriod period) {
        this.period = period;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getStdev() {
        return stdev;
    }

    public void setStdev(double stdev) {
        this.stdev = stdev;
    }
}
