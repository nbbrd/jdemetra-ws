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
package ec.nbb.demetra.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ec.tstoolkit.MetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Mats Maggi
 */
public class JsonMetaData implements IJsonConverter<MetaData> {

    @JsonDeserialize(as = ArrayList.class, contentAs = JsonProperty.class)
    public List<JsonProperty> properties;

    @Override
    public void from(MetaData t) {
        if (t.isEmpty()) {
            properties = null;
            return;
        }

        Set<String> keys = t.keySet();
        properties = new ArrayList<>();
        for (String key : keys) {
            JsonProperty p = new JsonProperty();
            p.name = key;
            p.value = t.get(key);
        }
    }

    public MetaData create() {
        MetaData rslt = new MetaData();
        rslt.clear();
        if (properties != null) {
            for (int i = 0; i < properties.size(); ++i) {
                JsonProperty p = properties.get(i);
                rslt.put(p.name, p.value);
            }
        }
        return rslt;
    }

}
