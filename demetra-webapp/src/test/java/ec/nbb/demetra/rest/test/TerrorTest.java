/*
 * Copyright 2015 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they will be approved 
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
package ec.nbb.demetra.rest.test;

import com.google.common.base.Stopwatch;
import ec.nbb.demetra.model.terror.TerrorRequest;
import ec.nbb.demetra.model.terror.TerrorResults;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.xml.XmlTsCollection;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Mats Maggi
 */
public class TerrorTest {

    @Test
    public void testTerror() {
        TerrorRequest input = new TerrorRequest();
        TsCollection coll = TsFactory.instance.createTsCollection();
        XmlTsCollection xmlCollection = new XmlTsCollection();
        for (int i = 0; i < 10; i++) {
            TsData tsdata = TsData.random(TsFrequency.Monthly);
            coll.add(TsFactory.instance.createTs("Series" + i, null, tsdata));
        }

        TsCollectionInformation infos = new TsCollectionInformation(coll, TsInformationType.All);
        xmlCollection.copy(infos);

        input.setNbLast(3);
        input.setSeries(xmlCollection);
        input.setSpecification("TR4");

        Stopwatch stopwatch = Stopwatch.createStarted();
       
        Client client = ClientBuilder.newClient();
        
        WebTarget service = client.target(TestConfig.getUrl());
        TerrorResults resp = service.path("terror")
                .request(MediaType.APPLICATION_XML)
                .post(Entity.entity(input, MediaType.APPLICATION_XML), TerrorResults.class);
        
        Assert.assertNotNull(resp);
        
        System.out.println(resp.getCount());
        System.out.println(stopwatch.stop().toString());
        
        
    }
}
