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
package ec.nbb.demetra.rest.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.GZipEncoder;
import org.junit.Test;

/**
 *
 * @author Mats Maggi
 */
public class HelloTest {

    @Test
    public void hello() {
        Client client = ClientBuilder.newClient();
        client.register(GZipEncoder.class);
        client.register(EncodingFilter.class);
        //Client client = Client.create(new DefaultClientConfig());
        //WebResource service = client.resource("http://srvdqrdd2.nbb.local:9998/demetra/api");
        WebTarget service = client.target(TestConfig.getUrl());
        String resp = service.path("hello")
                .queryParam("firstName", "Mats")
                .request(MediaType.TEXT_PLAIN)
                .get(String.class);

        System.out.println(resp);
    }

    @Test
    public void helloAsync() {
        try {
            JerseyClientBuilder jcb = new JerseyClientBuilder();
            jcb.register(GZipEncoder.class);
            JerseyClient jc = jcb.build();
            
            JerseyWebTarget service = jc.target(TestConfig.getUrl());
            Future<String> resp = service.path("hello/async")
                    .request(MediaType.TEXT_PLAIN)
                    .async()
                    .get(String.class);

            System.out.println(resp.get());
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(HelloTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
