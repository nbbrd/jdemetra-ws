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
package ec.nbb.demetra.rest.model;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mats Maggi
 */
@ApiModel
@XmlRootElement(name = "TerrorResult")
@XmlAccessorType(XmlAccessType.NONE)
public class TerrorResult {

    private String name;
    private double[] value;    
    private double[] forecast;
    private double[] score;

    public TerrorResult(String name, double[] value, double[] forecast, double[] score) {
        this.name = name;
        this.value = value;
        this.forecast = forecast;
        this.score = score;
    }
    
    public TerrorResult(String name) {
        this.name = name;
    }

    public TerrorResult() {
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "forecast", required = true)
    public double[] getForecast() {
        return forecast;
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "score", required = true)
    public double[] getScore() {
        return score;
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "value", required = true)
    public double[] getValue() {
        return value;
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "name", nillable = false, required = true)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setForecast(double[] forecast) {
        this.forecast = forecast;
    }

    public void setScore(double[] score) {
        this.score = score;
    }

    public void setValue(double[] value) {
        this.value = value;
    }
}
