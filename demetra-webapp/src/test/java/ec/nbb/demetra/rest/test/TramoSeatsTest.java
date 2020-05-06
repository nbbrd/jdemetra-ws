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
import java.net.URI;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Mats Maggi
 */
public class TramoSeatsTest extends JerseyTest {

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
    public void tramoseats() throws JsonProcessingException {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTsData xmlTsData = new XmlTsData();
        xmlTsData.copy(d);

        Response resp = callWSTramoSeats(xmlTsData);

        Assert.assertEquals(200, resp.getStatus());

        Map<String, XmlTsData> map = resp.readEntity(new GenericType<Map<String, XmlTsData>>() {
        });
        Assert.assertNotNull(map);
    }

    @Test
    public void tramoseatsRequestXML() {
        XmlTramoSeatsRequest request = new XmlTramoSeatsRequest();
        request.setDefaultSpecification("RSAfull");
        request.setSeries(new ec.demetra.xml.core.XmlTs());
        ec.demetra.xml.core.XmlTsData.MARSHALLER.marshal(Data.X, request.getSeries());

        Response resp = callWSTramoSeats(request, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);
        Assert.assertEquals(200, resp.getStatus());
        XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(set);
    }

    @Test
    public void tramoseatsRequestJSON() {
        XmlTramoSeatsRequest request = new XmlTramoSeatsRequest();
        request.setDefaultSpecification("RSAfull");
        request.setSeries(new ec.demetra.xml.core.XmlTs());
        ec.demetra.xml.core.XmlTsData.MARSHALLER.marshal(Data.X, request.getSeries());

        Response resp = callWSTramoSeats(request, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
        Assert.assertEquals(200, resp.getStatus());
        XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(set);
    }

    @Test
    public void tramoseatsRequestsXML() {
        int N = 10;
        XmlTramoSeatsRequests requests = new XmlTramoSeatsRequests();
        requests.setFlat(false);
        for (int i = 0; i < N; ++i) {
            XmlTramoSeatsRequest cur = new XmlTramoSeatsRequest();
            cur.setSpecification(advanced());
            cur.setSeries(new ec.demetra.xml.core.XmlTs());            
            ec.demetra.xml.core.XmlTsData.MARSHALLER.marshal(Data.P, cur.getSeries());
            cur.getSeries().setName("MySeries" + i);
            requests.getItems().add(cur);
        }
        requests.setContext(context());

        requests.getOutputFilter().add("arima.*");
        requests.getOutputFilter().add("likelihood.*");
        requests.getOutputFilter().add("residuals.*");
        requests.getOutputFilter().add("*_f");

        Response resp = callWSTramoSeats(requests, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);

        Assert.assertEquals(200, resp.getStatus());

        XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(set);
    }

    @Ignore
    public void tramoseatsRequestsJSON() {
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

        try {
            String json = TestConfig.serializationJson(requests);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        Response resp = callWSTramoSeats(requests, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    private Response callWSTramoSeats(XmlTsData ts) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
        Response resp = jwt.path("tramoseats")
                .queryParam("spec", "RSA4")
                .request(MediaType.APPLICATION_JSON)
                .acceptEncoding("gzip")
                .post(Entity.entity(ts, MediaType.APPLICATION_JSON));

        return resp;
    }

    private Response callWSTramoSeats(XmlTramoSeatsRequest request, String inputType, String outputType) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
        Response resp = jwt.path("tramoseats/request")
                .request(outputType)
                .acceptEncoding("gzip")
                .post(Entity.entity(request, inputType));

        return resp;
    }

    private Response callWSTramoSeats(XmlTramoSeatsRequests requests, String inputType, String outputType) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
        Response resp = jwt.path("tramoseats/requests")
                .request(outputType)
                .acceptEncoding("gzip")
                .post(Entity.entity(requests, inputType));

        return resp;
    }

    private XmlTramoSeatsSpecification advanced() {
        TramoSeatsSpecification spec = TramoSeatsSpecification.RSAfull.clone();
        spec.getTramoSpecification().getRegression().getCalendar().getTradingDays().setHolidays("Belgium");
        XmlTramoSeatsSpecification xml = new XmlTramoSeatsSpecification();
        XmlTramoSeatsSpecification.MARSHALLER.marshal(spec, xml);
        return xml;
    }

    private XmlProcessingContext context() {
        ProcessingContext context = new ProcessingContext();
        NationalCalendar calendar = new NationalCalendar();
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
        XmlProcessingContext xc = new XmlProcessingContext();
        XmlProcessingContext.MARSHALLER.marshal(context, xc);
        return xc;
    }
}
