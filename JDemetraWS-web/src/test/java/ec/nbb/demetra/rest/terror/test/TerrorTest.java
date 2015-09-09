package ec.nbb.demetra.rest.terror.test;

import com.google.common.base.Stopwatch;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import ec.nbb.demetra.rest.model.TerrorRequest;
import ec.nbb.demetra.rest.model.TerrorResults;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.xml.XmlTsCollection;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/*
 * Copyright 2014 National Bank of Belgium
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
/**
 *
 * @author Mats Maggi
 */
public class TerrorTest {

    private Set<Class<?>> configure() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(ec.nbb.demetra.rest.TerrorResource.class);
        return resources;
    }

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
       
        Client client = Client.create(new DefaultClientConfig());
        WebResource service = client.resource("http://srvdqrdd2.nbb.local:9998/demetra/api");
        TerrorResults resp = service.path("terror")
                .accept(MediaType.APPLICATION_XML)
                .entity(input, MediaType.APPLICATION_XML)
                .post(TerrorResults.class, input);
        System.out.println(resp.getCount());
        System.out.println(stopwatch.stop().toString());
    }
}
