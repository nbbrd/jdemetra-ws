/*
 * Copyright 2017 National Bank of Belgium
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
import java.net.URI;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Mats Maggi
 */
public class CheckLastTest extends JerseyTest {

    private static final XmlTsData data = new XmlTsData();

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .packages("ec.nbb.demetra.rest")
                .register(ec.nbb.demetra.exception.DemetraExceptionMapper.class)
                .register(ec.nbb.ws.filters.GZipWriterInterceptor.class)
                .register(ec.nbb.ws.filters.GZipReaderInterceptor.class)
                .register(io.swagger.jersey.listing.ApiListingResourceJSON.class)
                .register(io.swagger.jaxrs.listing.SwaggerSerializers.class)
                .register(ec.nbb.ws.json.JacksonJsonProvider.class)
                .register(org.glassfish.jersey.jackson.JacksonFeature.class)
                .register(ec.nbb.ws.filters.CORSFilter.class);
    }

    @Override
    protected URI getBaseUri() {
        return TestConfig.getURI();
    }

    @BeforeClass
    public static void setupTsData() {
        data.copy(Data.X);
    }

    @Test
    public void checkLast1() {
        Response resp = callWS(data, MediaType.APPLICATION_JSON);
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void checkLast2() {
        Response resp = callWS(data, MediaType.APPLICATION_XML);
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void checkLast3() {
        Response resp = callWS(data, MediaType.APPLICATION_JSON, 1, "tramoseats", "RSA4");
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void checkLast4() {
        Response resp = callWS(data, MediaType.APPLICATION_XML, 1, "tramoseats", "RSA4");
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void checkLast5() {
        Response resp = callWS(data, MediaType.APPLICATION_JSON, 1, "tramoseats", "MY_SPEC");
        Assert.assertEquals(500, resp.getStatus());
    }

    @Test
    public void checkLast6() {
        Response resp = callWS(data, MediaType.APPLICATION_XML, 1, "tramoseats", "MY_SPEC");
        Assert.assertEquals(500, resp.getStatus());
    }

    @Test
    public void checkLast7() {
        Response resp = callWS(data, MediaType.APPLICATION_JSON, 1, "x13", "RG3");
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void checkLast8() {
        Response resp = callWS(data, MediaType.APPLICATION_XML, 1, "x13", "RG3");
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void checkLast9() {
        Response resp = callWS(data, MediaType.APPLICATION_JSON, 1, "tramoseats", "RG3");
        Assert.assertEquals(500, resp.getStatus());
    }

    @Test
    public void checkLast10() {
        Response resp = callWS(data, MediaType.APPLICATION_XML, 1, "tramoseats", "RG3");
        Assert.assertEquals(500, resp.getStatus());
    }

    private Response callWS(XmlTsData ts, String type) {
        return callWS(ts, type, 1, "tramoseats", "TRfull");
    }

    private Response callWS(XmlTsData ts, String type, int nbLast, String algo, String spec) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
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
