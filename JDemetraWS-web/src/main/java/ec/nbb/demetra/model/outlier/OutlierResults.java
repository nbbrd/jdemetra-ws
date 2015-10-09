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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mats Maggi
 */
@ApiModel
@XmlRootElement(name = "OutlierResults")
@XmlAccessorType(XmlAccessType.NONE)
public class OutlierResults {

    private List<OutlierResult> results;

    @ApiModelProperty
    @XmlElement(name = "results")
    public List<OutlierResult> getResults() {
        return results;
    }

    public void setResults(List<OutlierResult> results) {
        this.results = results;
    }
    
    @ApiModelProperty(access = "hidden")
    public void add(OutlierResult r) {
        if (results == null) {
            results = new ArrayList<>();
        }
        results.add(r);
    }
}
