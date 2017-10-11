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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import javax.imageio.ImageIO;
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
 * Test class for the Chart REST resource
 *
 * @author Mats Maggi
 */
public class ChartTest extends JerseyTest {

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
                .register(ec.nbb.demetra.filters.ChartBodyWriter.class)
                .register(ec.nbb.ws.filters.CORSFilter.class);
    }

    @Override
    protected URI getBaseUri() {
        return TestConfig.getURI();
    }

    @Test
    /**
     * Test of the chart creation by the web service. Currently, 3 formats are
     * supported :
     * <li>image/png</li>
     * <li>image/jpeg</li>
     * <li>image/svg+xml</li>
     */
    public void chartFromTs() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTsData xmlTsData = new XmlTsData();
        xmlTsData.name = "RandomTs";
        xmlTsData.copy(d);

        String format = "image/png";

        Response resp = callWS(xmlTsData, format);

        Assert.assertEquals(200, resp.getStatus());

        switch (format) {
            case "image/svg+xml":
                String svgImage = resp.readEntity(String.class);
                Assert.assertNotNull(svgImage);

                try (PrintWriter out = new PrintWriter(String.format("C:\\Temp\\%s.svg", xmlTsData.name))) {
                    out.println(svgImage);
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                }
                break;
            case "image/png":
            case "image/jpeg":
                BufferedImage image = resp.readEntity(BufferedImage.class);
                Assert.assertNotNull(image);
                String subType = resp.getMediaType().getSubtype();
                try {
                    String path = System.getProperty("java.io.tmpdir");
                    ImageIO.write(image, subType, new File(String.format(path + "%s.%s", xmlTsData.name, subType)));
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                }
                break;
        }
    }

    private Response callWS(XmlTsData ts, String format) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
        Response resp = jwt.path("chart")
                .queryParam("scheme", "Lollipop")
                .queryParam("w", 800)
                .queryParam("h", 400)
                .queryParam("title", "My Awesome Web Service Chart")
                .request(format)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_JSON));

        return resp;
    }
}
