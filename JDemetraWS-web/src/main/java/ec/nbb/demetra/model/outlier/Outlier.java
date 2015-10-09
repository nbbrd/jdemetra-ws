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

import ec.nbb.demetra.json.JsonTsPeriod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mats Maggi
 */
@ApiModel
@XmlRootElement(name = "Outlier")
@XmlAccessorType(XmlAccessType.NONE)
public class Outlier {

    private JsonTsPeriod position;
    private String type;
    private double value;
    private double stdev;
    private double tstat;

    @ApiModelProperty
    @XmlElement(name = "position")
    public JsonTsPeriod getPosition() {
        return position;
    }

    @ApiModelProperty
    @XmlElement(name = "stdev")
    public double getStdev() {
        return stdev;
    }

    @ApiModelProperty
    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    @ApiModelProperty
    @XmlElement(name = "value")
    public double getValue() {
        return value;
    }

    @ApiModelProperty
    @XmlElement(name = "tstat")
    public double getTStat() {
        return tstat;
    }

    public void setPosition(JsonTsPeriod period) {
        this.position = period;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStdev(double stdev) {
        this.stdev = stdev;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setTStat(double tStat) {
        this.tstat = tStat;
    }
}
