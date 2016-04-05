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
package ec.nbb.demetra.json;

import ec.nbb.ws.json.IJsonConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsFactory;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mats Maggi
 */
public class JsonTsCollection implements IJsonConverter<TsCollectionInformation> {
    public String name;
    public String source;
    public String identifier;
    
    @JsonDeserialize(as=ArrayList.class, contentAs=JsonTs.class)
    public List<JsonTs> ts;
    
    public JsonMetaData metadata;

    @Override
    public void from(TsCollectionInformation t) {
        source = t.moniker.getSource();
        identifier = t.moniker.getId();
        name = t.name;
        
        if (t.metaData == null || t.metaData.isEmpty())
	    metadata = null;
	else {
	    metadata = new JsonMetaData();
	    metadata.from(t.metaData);
	}

        int n = t.items.size();
        if (n > 0) {
            ts = new ArrayList<>();
            for (int i = 0; i < n; ++i) {
                JsonTs s = new JsonTs();
                s.from(t.items.get(i));
                ts.add(s);
            }
        } else {
            ts = null;
        }
    }

    public TsCollectionInformation create() {
        TsMoniker moniker = TsMoniker.create(source, identifier);
        TsCollectionInformation cinfo = new TsCollectionInformation(moniker,
                TsInformationType.UserDefined);
        cinfo.name = name;
        
        if (metadata != null)
	    cinfo.metaData = metadata.create();
        
        if (ts != null) {
            for (int i = 0; i < ts.size(); ++i) {
                cinfo.items.add(ts.get(i).to());
            }
        }
        return cinfo;
    }

    public TsCollection createTSCollection() {
        TsCollectionInformation info = create();
        if (info == null) {
            return null;
        }
        ArrayList<Ts> ts = new ArrayList<>();
        if (info.items != null) {
            for (TsInformation tsinfo : info.items) {
                ts.add(TsFactory.instance.createTs(tsinfo.name, tsinfo.moniker,
                        tsinfo.metaData, tsinfo.data));
            }
        }
        return TsFactory.instance.createTsCollection(info.name, info.moniker,
                info.metaData, ts);
    }

}
