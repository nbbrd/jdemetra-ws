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

import ec.nbb.demetra.json.JsonTsCollection;
import ec.tss.TsCollectionInformation;
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
@XmlRootElement(name = "OutlierRequest")
@XmlAccessorType(XmlAccessType.NONE)
public class OutlierRequest {

    private JsonTsCollection series;
    
    private double criticalValue;
    
    private boolean showAO;
    private boolean showLS;
    private boolean showTC;
    private boolean showSO;
    
    private String transformation;
    private String specification;

    public OutlierRequest() {
        
    }
    
    public OutlierRequest(TsCollectionInformation coll) {
        series = new JsonTsCollection();
        series.from(coll);
    }
    
    @ApiModelProperty(required = true)
    @XmlElement(name = "series", nillable = false, required = true)
    public JsonTsCollection getSeries() {
        return series;
    }

    @ApiModelProperty(required = false)
    @XmlElement(name = "criticalValue", defaultValue = "0.0")
    public double getCriticalValue() {
        return criticalValue;
    }    

    @ApiModelProperty(required = false)
    @XmlElement(name = "showAO", defaultValue = "true")
    public boolean isShowAO() {
        return showAO;
    }

    @ApiModelProperty(required = false)
    @XmlElement(name = "showLS", defaultValue = "true")
    public boolean isShowLS() {
        return showLS;
    }

    @ApiModelProperty(required = false)
    @XmlElement(name = "showSO", defaultValue = "true")
    public boolean isShowSO() {
        return showSO;
    }

    @ApiModelProperty(required = false)
    @XmlElement(name = "showTC", defaultValue = "true")
    public boolean isShowTC() {
        return showTC;
    }

    @ApiModelProperty(required = false, allowableValues = "None, Auto, Log")
    @XmlElement(name = "transformation", defaultValue = "None")
    public String getTransformation() {
        return transformation;
    }

    @ApiModelProperty(required = false)
    @XmlElement(name = "specification", defaultValue = "TRfull")
    public String getSpecification() {
        return specification;
    }

    public void setCriticalValue(double criticalValue) {
        this.criticalValue = criticalValue;
    }

    public void setSeries(JsonTsCollection series) {
        this.series = series;
    }

    public void setShowAO(boolean showAO) {
        this.showAO = showAO;
    }

    public void setShowLS(boolean showLS) {
        this.showLS = showLS;
    }

    public void setShowSO(boolean showSO) {
        this.showSO = showSO;
    }

    public void setShowTC(boolean showTC) {
        this.showTC = showTC;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }
}
