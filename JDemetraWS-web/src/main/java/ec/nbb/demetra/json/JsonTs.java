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

import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Mats Maggi
 */
@XmlRootElement(name = "ts")
@XmlType(name = "tsType")
public class JsonTs implements IJsonConverter<TsInformation> {

    @XmlElement
    public Integer freq;
    
    @XmlElement
    public Integer firstYear;
    
    @XmlElement
    public Integer firstPeriod;
    
    @XmlElement(name = "data")
    @XmlList
    public double[] data;
    
    @XmlAttribute
    public String name;
    
    @XmlAttribute
    public String source;
    
    @XmlAttribute
    public String identifier;

    @Override
    public void from(TsInformation t) {
        TsData tsdata = t.data;
        if (tsdata != null) {
            TsPeriod start = tsdata.getStart();
            freq = start.getFrequency().intValue();
            firstYear = start.getYear();
            firstPeriod = start.getPosition() + 1;
            data = tsdata.getValues().internalStorage();
        }
        source = t.moniker.getSource();
        identifier = t.moniker.getId();
        name = t.name;
    }

    public TsInformation to() {
        TsMoniker moniker = TsMoniker.create(source, identifier);
        TsInformation info = new TsInformation(name, moniker, data != null
                ? TsInformationType.UserDefined : TsInformationType.None);
        if (data != null) {
            info.data = new TsData(TsFrequency.valueOf(freq), firstYear,
                    firstPeriod - 1, data, false);
        }
        return info;
    }
}
