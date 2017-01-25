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

import ec.nbb.demetra.model.outlier.ShadowOutlier;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
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
import org.junit.Test;

/**
 * Tests for the outlier detection service
 *
 * @author Mats Maggi
 */
public class OutlierTest extends JerseyTest {
    
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

    @Test
    public void outlierNewTest() {
        TsData d = TsData.random(TsFrequency.Monthly);
        d.set(d.getObsCount() / 2, 1000); // Creating an outlier
        XmlTsData ts = new XmlTsData();
        ts.name = "ts";
        ts.copy(d);

        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
        Response resp = jwt.path("outlier")
                .request(MediaType.APPLICATION_JSON)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_JSON));
        Assert.assertEquals(200, resp.getStatus());
        ShadowOutlier[] outliers = resp.readEntity(ShadowOutlier[].class);
        Assert.assertEquals(1, outliers.length);
    }
}
