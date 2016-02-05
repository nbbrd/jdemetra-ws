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

import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.xml.IXmlConverter;
import ec.tss.xml.XmlPeriodSelection;
import ec.tss.xml.XmlTs;
import ec.tss.xml.XmlTsCollection;
import ec.tss.xml.XmlTsData;
import ec.tss.xml.XmlTsMoniker;
import ec.tss.xml.XmlTsPeriod;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.timeseries.PeriodSelectorType;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
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
public class XmlTest {
    
    private final String mediaType = MediaType.APPLICATION_XML;

    // <editor-fold desc="XmlTs">
    @Test
    public void ts() {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTs ts = new XmlTs();
        TsInformation info = new TsInformation("Test Ts", new TsMoniker(), TsInformationType.All);
        info.data = d;
        ts.copy(info);

        Response resp = callWS(ts, "ts", mediaType);
        Assert.assertEquals(200, resp.getStatus());
        XmlTs responseTs = resp.readEntity(XmlTs.class);
        Assert.assertNotNull(responseTs);
        Assert.assertArrayEquals(responseTs.data, ts.data, 0);
        Assert.assertEquals(responseTs.firstPeriod, ts.firstPeriod);
        Assert.assertEquals(responseTs.firstYear, ts.firstYear);
        Assert.assertEquals(responseTs.freq, ts.freq);
    }
    // </editor-fold>

    // <editor-fold desc="XmlTsData">
    @Test
    public void tsData() {
        TsData tsData = TsData.random(TsFrequency.Monthly);
        XmlTsData xmlTsData = new XmlTsData();
        xmlTsData.copy(tsData);

        Response resp = callWS(xmlTsData, "tsdata", mediaType);
        Assert.assertEquals(200, resp.getStatus());
        XmlTsData responseXml = resp.readEntity(XmlTsData.class);
        Assert.assertNotNull(responseXml);
        Assert.assertArrayEquals(responseXml.data, xmlTsData.data, 0);
        Assert.assertEquals(responseXml.firstPeriod, xmlTsData.firstPeriod);
        Assert.assertEquals(responseXml.firstYear, xmlTsData.firstYear);
        Assert.assertEquals(responseXml.freq, xmlTsData.freq);
        TsData responseTsData = responseXml.create();
        Assert.assertTrue(responseTsData.equals(tsData));
    }
    // </editor-fold>

    // <editor-fold desc="XmlTsCollection">
    @Test
    public void tsCollection() {
        TsCollectionInformation collection = new TsCollectionInformation(new TsMoniker(), TsInformationType.All);
        for (int i = 0; i < 10; i++) {
            TsData d = TsData.random(TsFrequency.Monthly);
            TsInformation info = new TsInformation("[" + i + "] Test Ts", new TsMoniker(), TsInformationType.All);
            info.data = d;
            collection.items.add(info);
        }
        XmlTsCollection xmlTsCollection = new XmlTsCollection();
        xmlTsCollection.copy(collection);

        Response resp = callWS(xmlTsCollection, "tscollection", mediaType);
        Assert.assertEquals(200, resp.getStatus());
        XmlTsCollection responseXml = resp.readEntity(XmlTsCollection.class);
        Assert.assertNotNull(responseXml);
        Assert.assertEquals(responseXml.tslist.length, xmlTsCollection.tslist.length);
    }
    // </editor-fold>
    
    // <editor-fold desc="XmlTsMoniker">
    @Test
    public void tsMoniker() {
        TsMoniker tsMoniker = new TsMoniker("MySource", "MyId");
        XmlTsMoniker xmlTsMoniker = new XmlTsMoniker();
        xmlTsMoniker.copy(tsMoniker);

        Response resp = callWS(xmlTsMoniker, "tsmoniker", mediaType);
        Assert.assertEquals(200, resp.getStatus());
        XmlTsMoniker responseXml = resp.readEntity(XmlTsMoniker.class);
        Assert.assertNotNull(responseXml);
        TsMoniker responseTsMoniker = responseXml.create();
        Assert.assertEquals(responseTsMoniker, tsMoniker);
    }
    // </editor-fold>
    
    // <editor-fold desc="XmlTsPeriod">
    @Test
    public void tsPeriod() {
        TsPeriod tsPeriod = new TsPeriod(TsFrequency.Monthly, 2010, 3);
        XmlTsPeriod xmlTsPeriod = new XmlTsPeriod();
        xmlTsPeriod.copy(tsPeriod);

        Response resp = callWS(xmlTsPeriod, "tsperiod", mediaType);
        Assert.assertEquals(200, resp.getStatus());
        XmlTsPeriod responseXml = resp.readEntity(XmlTsPeriod.class);
        Assert.assertNotNull(responseXml);
        TsPeriod responseTsPeriod = responseXml.create();
        Assert.assertTrue(responseTsPeriod.equals(tsPeriod));
    }
    // </editor-fold>
    
    // <editor-fold desc="XmlPeriodSelection">
    @Test
    public void tsPeriodSelection() {
        TsPeriodSelector selector = new TsPeriodSelector();
        selector.setType(PeriodSelectorType.Between);
        selector.between(new Day(2010, Month.March, 14), new Day(2010, Month.July, 20));
        XmlPeriodSelection xmlPeriodSelection = new XmlPeriodSelection();
        xmlPeriodSelection.copy(selector);

        Response resp = callWS(xmlPeriodSelection, "periodselector", mediaType);
        Assert.assertEquals(200, resp.getStatus());
        XmlPeriodSelection responseXml = resp.readEntity(XmlPeriodSelection.class);
        Assert.assertNotNull(responseXml);
        TsPeriodSelector responsePeriodSelector = responseXml.create();
        Assert.assertTrue(responsePeriodSelector.equals(selector));
    }
    // </editor-fold>

    private Response callWS(IXmlConverter entity, String path, String type) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("test/xml/" + path)
                .request(type)
                .acceptEncoding("gzip")
                .post(Entity.entity(entity, type));

        return resp;
    }
}
