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

import com.fasterxml.jackson.core.JsonProcessingException;
import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.xml.XmlTs;
import ec.tss.xml.XmlTsCollection;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.message.GZipEncoder;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Mats Maggi
 */
public class SeasonalAdjustmentTest {

    @Test
    public void seasonalAdjustment() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Monthly);
        ShadowTs ts = RestUtils.toShadowTs("Blabla", d);
        ts.setAggregationMethod(TsAggregationType.None);
        ts.setFreq(12);

        Response resp = callWS(ts);
        
        Assert.assertEquals(200, resp.getStatus());
        
        List<ShadowTs> list = resp.readEntity(new GenericType<List<ShadowTs>>(){});
        Assert.assertNotNull(list);
    }
    
    @Test
    public void seasonalAdjustmentWithXml() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTs ts = new XmlTs();
        TsInformation info = new TsInformation("Blabla", new TsMoniker(), TsInformationType.All);
        info.data = d;
        ts.copy(info);

        Response resp = callWSWithXml(ts);
        
        Assert.assertEquals(200, resp.getStatus());
        
        XmlTsCollection list = resp.readEntity(XmlTsCollection.class);
        Assert.assertNotNull(list);
        TsCollectionInformation collInfo = list.create();
        System.out.println(collInfo.items.size());
    }
    
    public Response callWS(ShadowTs ts) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("sa/x13")
                .queryParam("spec", "RSA4c")
                .request(MediaType.APPLICATION_JSON)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_JSON));
        
        return resp;
    }
    
    public Response callWSWithXml(XmlTs ts) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("sa/xml/x13")
                .queryParam("spec", "RSA4c")
                .request(MediaType.APPLICATION_XML)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_XML));
        
        return resp;
    }
}
