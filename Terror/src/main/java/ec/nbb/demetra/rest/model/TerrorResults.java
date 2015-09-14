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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mats Maggi
 */
@ApiModel
@XmlRootElement(name = "TerrorResults")
public class TerrorResults {

    private List<TerrorResult> results;

    public TerrorResults() {
    }

    public TerrorResults(final Collection<TerrorResult> results) {
        this.results = new ArrayList<>(results);
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "result", required = true)
    public List<TerrorResult> getResults() {
        return results;
    }

    public void setResults(List<TerrorResult> results) {
        this.results = results;
    }
    
    @ApiModelProperty(required = true)
    @XmlAttribute(name = "size", required = true)
    public int getCount() {
        return results == null ? 0 : results.size();
    }
    
    @ApiModelProperty(access = "hidden")
    public void add(TerrorResult r) {
        if (results == null) {
            results = new ArrayList<>();
        }
        results.add(r);
    }
}
