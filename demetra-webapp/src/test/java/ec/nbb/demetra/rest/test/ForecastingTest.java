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
import ec.nbb.demetra.model.rest.utils.RestUtils;
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
public class ForecastingTest {

    @Test
    public void forecastingSameDomain() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTsData ts = new XmlTsData();
        ts.name = "Blabla";
        ts.copy(d);
        int start = RestUtils.fromTsPeriod(d.getStart());
        int end = RestUtils.fromTsPeriod(d.getEnd());

        Response resp = callWS(ts, start, end);
        Assert.assertNotNull(resp);
        Assert.assertEquals(200, resp.getStatus());

        XmlTsData responseTs = resp.readEntity(XmlTsData.class);
        Assert.assertNotNull(responseTs);
        Assert.assertEquals("Blabla", responseTs.name);

        TsData resultTsData = responseTs.create();
        Assert.assertEquals(d.getValues().getLength(), resultTsData.getValues().getLength());
        Assert.assertTrue(d.getDomain().equals(resultTsData.getDomain()));
    }

    @Test
    public void backcasting() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Quarterly);
        XmlTsData ts = new XmlTsData();
        ts.name = "Blabla";
        ts.copy(d);

        int start = RestUtils.fromTsPeriod(d.getStart().minus(10));
        int end = RestUtils.fromTsPeriod(d.getEnd());

        Response resp = callWS(ts, start, end);

        Assert.assertNotNull(resp);
        Assert.assertEquals(200, resp.getStatus());
        XmlTsData responseTs = resp.readEntity(XmlTsData.class);
        Assert.assertNotNull(responseTs);
        Assert.assertEquals("Blabla", responseTs.name);

        TsData resultTsData = responseTs.create();
        Assert.assertEquals(d.getObsCount() + 10, resultTsData.getObsCount());
        Assert.assertTrue(d.getEnd().equals(resultTsData.getEnd())); // Backcast so end unchanged
    }

    private Response callWS(XmlTsData ts, int start, int end) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        //JerseyWebTarget jwt = jc.target("https://pc0021770.nbb.local:8181/demetra/api"); // Needs installation of certificate
        Response resp = jwt.path("forecast")
                .queryParam("start", start)
                .queryParam("end", end)
                .request(MediaType.APPLICATION_JSON)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_XML));

        return resp;
    }
}
