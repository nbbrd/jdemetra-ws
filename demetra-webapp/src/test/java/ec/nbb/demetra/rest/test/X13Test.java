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

import ec.demetra.xml.core.XmlInformationSet;
import ec.demetra.xml.core.XmlTs;
import ec.demetra.xml.core.XmlTsData;
import ec.demetra.xml.sa.x13.XmlX13Request;
import ec.demetra.xml.sa.x13.XmlX13Requests;
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
public class X13Test {

    @Test
    public void x13() {
        XmlX13Request request = new XmlX13Request();
        request.setDefaultSpecification("RSA5c");
        request.setSeries(new XmlTs());
        XmlTsData.MARSHALLER.marshal(Data.X, request.getSeries());
        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");

        Response resp = callWSX13(request);
        
        Assert.assertEquals(200, resp.getStatus());
        
        XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(set);
    }
    
    @Test
    public void x13Requests() {
        XmlX13Requests requests = new XmlX13Requests();
        requests.setFlat(true);
        for (int i = 0; i < 5; ++i) {
            XmlX13Request cur = new XmlX13Request();
            cur.setDefaultSpecification("RSA5c");
            cur.setSeries(new XmlTs());
            XmlTsData.MARSHALLER.marshal(Data.P, cur.getSeries());
            requests.getItems().add(cur);
        }

        requests.getOutputFilter().add("arima.*");
        requests.getOutputFilter().add("likelihood.*");
        requests.getOutputFilter().add("residuals.*");
        requests.getOutputFilter().add("*_f");

        Response resp = callWSX13(requests);
        
        Assert.assertEquals(200, resp.getStatus());
        
        XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(set);
    }
    
    public Response callWSX13(XmlX13Request request) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("x13/request")
                .request(MediaType.APPLICATION_XML)
                .acceptEncoding("gzip")
                .post(Entity.entity(request, MediaType.APPLICATION_XML));
        
        return resp;
    }
    
    public Response callWSX13(XmlX13Requests requests) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("x13/requests")
                .request(MediaType.APPLICATION_XML)
                .acceptEncoding("gzip")
                .post(Entity.entity(requests, MediaType.APPLICATION_XML));
        
        return resp;
    }
}
