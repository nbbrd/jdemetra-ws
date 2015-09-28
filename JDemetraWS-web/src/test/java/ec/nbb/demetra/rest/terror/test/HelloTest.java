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
package ec.nbb.demetra.rest.terror.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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
        WebTarget service = client.target("http://localhost:8080/demetra/api");
        String resp = service.path("hello")
                .request(MediaType.TEXT_PLAIN)
                .get(String.class);

        System.out.println(resp);
    }
}
