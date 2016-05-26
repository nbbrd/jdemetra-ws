/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbb.demetra.rest.test;

import ec.tss.xml.XmlTsData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import javax.ws.rs.client.Entity;
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
public class CheckLastTest {

    @Test
    public void checkLast() {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTsData xml = new XmlTsData();
        xml.copy(d);

        Response resp = callWS(xml, MediaType.APPLICATION_JSON);
        Assert.assertEquals(200, resp.getStatus());
        
        resp = callWS(xml, MediaType.APPLICATION_XML);
        Assert.assertEquals(200, resp.getStatus());
        
        resp = callWS(xml, MediaType.APPLICATION_JSON, 1, "tramoseats", "RSA4");
        Assert.assertEquals(200, resp.getStatus());
        
        resp = callWS(xml, MediaType.APPLICATION_JSON, 1, "tramoseats", "MY_SPEC");
        Assert.assertEquals(500, resp.getStatus());
        
        resp = callWS(xml, MediaType.APPLICATION_JSON, 1, "x13", "RG3");
        Assert.assertEquals(200, resp.getStatus());
        
        resp = callWS(xml, MediaType.APPLICATION_JSON, 1, "tramoseats", "RG3");
        Assert.assertEquals(500, resp.getStatus());
    }
    
    public Response callWS(XmlTsData ts, String type) {
        return callWS(ts, type, 1, "tramoseats", "TRfull");
    }

    public Response callWS(XmlTsData ts, String type, int nbLast, String algo, String spec) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("checklast")
                .queryParam("spec", spec)
                .queryParam("algorithm", algo)
                .queryParam("nbLast", nbLast)
                .request(type)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, type));

        return resp;
    }
}
