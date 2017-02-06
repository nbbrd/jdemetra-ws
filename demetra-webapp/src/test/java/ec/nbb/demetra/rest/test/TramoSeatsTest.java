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
import ec.demetra.xml.core.XmlInformationSet;
import ec.demetra.xml.processing.XmlProcessingContext;
import ec.demetra.xml.sa.tramoseats.XmlTramoSeatsRequest;
import ec.demetra.xml.sa.tramoseats.XmlTramoSeatsRequests;
import ec.demetra.xml.sa.tramoseats.XmlTramoSeatsSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.timeseries.calendars.DayEvent;
import ec.tstoolkit.timeseries.calendars.FixedDay;
import ec.tstoolkit.timeseries.calendars.NationalCalendar;
import ec.tstoolkit.timeseries.calendars.NationalCalendarProvider;
import ec.tstoolkit.timeseries.calendars.SpecialCalendarDay;
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

    private static final String TRAMOSEATS_REQUEST_PATH = "tramoseats/request";

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
    
    @Test
    public void tramoseatsRequest() throws Exception {
        XmlTramoSeatsRequest request = new XmlTramoSeatsRequest();
        request.setDefaultSpecification("RSAfull");
        request.setSeries(new ec.demetra.xml.core.XmlTs());
        ec.demetra.xml.core.XmlTsData.MARSHALLER.marshal(Data.X, request.getSeries());
//        request.getOutputFilter().add("arima.*");
//        request.getOutputFilter().add("likelihood.*");
//        request.getOutputFilter().add("residuals.*");
//        request.getOutputFilter().add("*_f");

        Response resp = callWSTramoSeats(request);        
        Response respJSON = callWSTramoSeatsJSON(request);
        
        Assert.assertEquals(200, resp.getStatus());
        Assert.assertEquals(200, respJSON.getStatus());
        
        XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
        XmlInformationSet setJSON = respJSON.readEntity(XmlInformationSet.class);
        
        Assert.assertNotNull(set);
        Assert.assertNotNull(setJSON);
        Assert.assertEquals(set.item.length, setJSON.item.length);
    }
    
    @Test
    public void tramoseatsRequests() {
        int N = 10;
        XmlTramoSeatsRequests requests = new XmlTramoSeatsRequests();
        requests.setFlat(true);
        for (int i = 0; i < N; ++i) {
            XmlTramoSeatsRequest cur = new XmlTramoSeatsRequest();
            cur.setSpecification(advanced());
            cur.setSeries(new ec.demetra.xml.core.XmlTs());
            ec.demetra.xml.core.XmlTsData.MARSHALLER.marshal(Data.P, cur.getSeries());
            requests.getItems().add(cur);
        }
        requests.setContext(context());        
        
        requests.getOutputFilter().add("arima.*");
        requests.getOutputFilter().add("likelihood.*");
        requests.getOutputFilter().add("residuals.*");
        requests.getOutputFilter().add("*_f");

        Response resp = callWSTramoSeats(requests);
        
        Assert.assertEquals(200, resp.getStatus());
        
        XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(set);
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
    
    public Response callWSTramoSeats(XmlTramoSeatsRequest request) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path(TRAMOSEATS_REQUEST_PATH)
                .request(MediaType.APPLICATION_XML)
                .acceptEncoding("gzip")
                .post(Entity.entity(request, MediaType.APPLICATION_XML));
        
        return resp;
    }
    
    public Response callWSTramoSeats(XmlTramoSeatsRequests requests) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path("tramoseats/requests")
                .request(MediaType.APPLICATION_XML)
                .acceptEncoding("gzip")
                .post(Entity.entity(requests, MediaType.APPLICATION_XML));
        
        return resp;
    }    
    // RT 26012017
    public Response callWSTramoSeatsJSON(XmlTramoSeatsRequest request) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(TestConfig.getUrl());
        Response resp = jwt.path(TRAMOSEATS_REQUEST_PATH)
                .request(MediaType.APPLICATION_JSON)
                .acceptEncoding("gzip")
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
        
        return resp;
    }
    
    private XmlTramoSeatsSpecification advanced() {
        TramoSeatsSpecification spec=TramoSeatsSpecification.RSAfull.clone();
        spec.getTramoSpecification().getRegression().getCalendar().getTradingDays().setHolidays("Belgium");
        XmlTramoSeatsSpecification xml=new XmlTramoSeatsSpecification();
        XmlTramoSeatsSpecification.MARSHALLER.marshal(spec, xml);
        return xml;
    }
    
    private XmlProcessingContext context(){
        ProcessingContext context=new ProcessingContext();
        NationalCalendar calendar=new NationalCalendar();
        calendar.add(new FixedDay(20, Month.July));
        calendar.add(new FixedDay(10, Month.October));
        calendar.add(new SpecialCalendarDay(DayEvent.NewYear, 0));
        calendar.add(new SpecialCalendarDay(DayEvent.EasterMonday, 0));
        calendar.add(new SpecialCalendarDay(DayEvent.Ascension, 0));
        calendar.add(new SpecialCalendarDay(DayEvent.WhitMonday, 0));
        calendar.add(new SpecialCalendarDay(DayEvent.MayDay, 0));
        calendar.add(new SpecialCalendarDay(DayEvent.Assumption, 0));
        calendar.add(new SpecialCalendarDay(DayEvent.AllSaintsDay, 0));
        calendar.add(new SpecialCalendarDay(DayEvent.Christmas, 0));
        context.getGregorianCalendars().set("Belgium", new NationalCalendarProvider(calendar));
        XmlProcessingContext xc=new XmlProcessingContext();
        XmlProcessingContext.MARSHALLER.marshal(context, xc);
        return xc;
    }
}
