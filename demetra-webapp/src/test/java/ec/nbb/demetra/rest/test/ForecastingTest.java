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
import com.google.common.base.Strings;
import ec.nbb.demetra.model.outlier.ShadowTs;
import ec.nbb.demetra.model.rest.utils.RestUtils;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import io.swagger.util.Json;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        ShadowTs ts = RestUtils.toShadowTs("Blabla", d);
        ts.setAggregationMethod(TsAggregationType.None);
        ts.setFreq(d.getFrequency().intValue());
        int start = RestUtils.fromTsPeriod(d.getStart());
        int end = RestUtils.fromTsPeriod(d.getEnd());

        Response resp = callWS(ts, start, end);
        Assert.assertNotNull(resp);
        Assert.assertEquals(200, resp.getStatus());

        ShadowTs responseTs = resp.readEntity(ShadowTs.class);
        Assert.assertNotNull(responseTs);
        Assert.assertEquals("Blabla", responseTs.getName());
        Assert.assertArrayEquals(ts.getValues(), responseTs.getValues(), 0);
        Assert.assertArrayEquals(ts.getPeriods(), responseTs.getPeriods());
    }

    @Test
    public void backcasting() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Quarterly);
        ShadowTs ts = RestUtils.toShadowTs("Blabla", d);
        ts.setAggregationMethod(TsAggregationType.None);
        ts.setFreq(d.getFrequency().intValue());
        int start = RestUtils.fromTsPeriod(d.getStart().minus(10));
        int end = RestUtils.fromTsPeriod(d.getEnd());
        
        Response resp = callWS(ts, start, end);

        Assert.assertNotNull(resp);
        Assert.assertEquals(200, resp.getStatus());
        ShadowTs responseTs = resp.readEntity(ShadowTs.class);
        Assert.assertNotNull(responseTs);
        Assert.assertEquals("Blabla", responseTs.getName());
        Assert.assertEquals(ts.getPeriods().length + 10, responseTs.getPeriods().length);
        Assert.assertEquals(responseTs.getPeriods().length, responseTs.getValues().length);
        TsPeriod responseStart = RestUtils.toPeriod(responseTs.getPeriods()[0], TsFrequency.Quarterly);
        TsPeriod requestStart = RestUtils.toPeriod(start, TsFrequency.Quarterly);
        Assert.assertEquals(responseStart, requestStart);
        
    }

    private Response callWS(ShadowTs ts, int start, int end) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        //JerseyWebTarget jwt = jc.target("https://pc0021770.nbb.local:8181/demetra/api"); // Needs installation of certificate
        Response resp = jwt.path("forecast/tramoseats")
                .queryParam("start", start)
                .queryParam("end", end)
                .request(MediaType.APPLICATION_JSON)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_JSON));

        return resp;
    }

    @Test
    public void createShadowTs() {
        try {
            ShadowTs[] ts = new ShadowTs[1000];
            for (int i = 0; i < ts.length; i++) {
                TsData d = TsData.random(TsFrequency.Monthly);
                
                ts[i] = RestUtils.toShadowTs("Blabla" + i, d);
                ts[i].setAggregationMethod(TsAggregationType.None);
                ts[i].setFreq(d.getFrequency().intValue());
            }
            
            String json = Json.mapper().writeValueAsString(ts);
            Assert.assertFalse(Strings.isNullOrEmpty(json));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ForecastingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
