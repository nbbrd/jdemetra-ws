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
package ec.nbb.demetra.model.terror;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Object used to hold the results of a CheckLast
 *
 * @author Mats Maggi
 */
@ApiModel
@XmlRootElement(name = "TerrorResult")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TerrorResultType")
public class TerrorResult {

    @ApiModelProperty(required = true)
    @JsonProperty(value = "Name")
    @XmlElement
    private String name;

    @ApiModelProperty(required = true)
    @JsonProperty(value = "Value")
    @XmlElement
    @XmlList
    private double[] value;

    @ApiModelProperty(required = true)
    @JsonProperty(value = "Forecast")
    @XmlElement
    @XmlList
    private double[] forecast;

    @ApiModelProperty(required = true)
    @JsonProperty(value = "Score")
    @XmlElement
    @XmlList
    private double[] score;

    public TerrorResult(String name, double[] value, double[] forecast, double[] score) {
        this.name = name;
        this.value = value;
        this.forecast = forecast;
        this.score = score;
    }

    public TerrorResult() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getValue() {
        return value;
    }

    public void setValue(double[] value) {
        this.value = value;
    }

    public double[] getForecast() {
        return forecast;
    }

    public void setForecast(double[] forecast) {
        this.forecast = forecast;
    }

    public double[] getScore() {
        return score;
    }

    public void setScore(double[] score) {
        this.score = score;
    }
    
    
}
