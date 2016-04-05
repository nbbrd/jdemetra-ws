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
package ec.nbb.demetra.model.outlier;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

/**
 *
 * @author Mats Maggi
 */
@ApiModel
public class ShadowDomain {
    
    @JsonProperty(value = "Start")
    private int start;
    @JsonProperty(value = "End")
    private int end;

    @JsonProperty(value = "Start")
    public int getStart() {
        return start;
    }

    @JsonProperty(value = "Start")
    public void setStart(int start) {
        this.start = start;
    }
    
    @JsonProperty(value = "End")
    public int getEnd() {
        return end;
    }

    @JsonProperty(value = "End")
    public void setEnd(int end) {
        this.end = end;
    }
}
