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

import ec.tss.xml.XmlTsCollection;
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
@XmlRootElement(name = "TerrorRequest")
@XmlAccessorType(XmlAccessType.NONE)
public class TerrorRequest {

    private XmlTsCollection series;
    private String specification;
    private int nbLast;

    @ApiModelProperty(required = true)
    @XmlElement(name = "series", nillable = false, required = true)
    public XmlTsCollection getSeries() {
        return series;
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "specification", nillable = false, required = true)
    public String getSpecification() {
        return specification;
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "nbLast", nillable = false, required = true)
    public int getNbLast() {
        return nbLast;
    }

    public void setNbLast(int nbLast) {
        this.nbLast = nbLast;
    }

    public void setSeries(XmlTsCollection series) {
        this.series = series;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

}
