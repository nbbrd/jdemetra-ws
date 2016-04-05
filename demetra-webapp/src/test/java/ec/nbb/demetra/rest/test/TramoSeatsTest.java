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
package ec.nbb.demetra.rest.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.util.Map;
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
public class TramoSeatsTest {

    @Test
    public void tramoseats() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTsData xmlTsData = new XmlTsData();
        xmlTsData.copy(d);

        Response resp = callWSTramoSeats(xmlTsData);
        
        Assert.assertEquals(200, resp.getStatus());
        
        Map<String, XmlTsData> map = resp.readEntity(new GenericType<Map<String, XmlTsData>>(){});
        Assert.assertNotNull(map);
    }
    
    public Response callWSTramoSeats(XmlTsData ts) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("tramoseats")
                .queryParam("spec", "RSA4")
                .request(MediaType.APPLICATION_JSON)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_JSON));
        
        return resp;
    }
}
