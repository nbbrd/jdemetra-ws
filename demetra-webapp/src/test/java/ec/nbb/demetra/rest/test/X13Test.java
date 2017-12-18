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

import ec.demetra.xml.XmlEmptyElement;
import ec.demetra.xml.calendars.XmlCalendars;
import ec.demetra.xml.calendars.XmlNationalCalendar;
import ec.demetra.xml.calendars.XmlSpecialCalendarDay;
import ec.demetra.xml.calendars.XmlSpecialDayEvent;
import ec.demetra.xml.core.XmlInformationSet;
import ec.demetra.xml.core.XmlTs;
import ec.demetra.xml.core.XmlTsData;
import ec.demetra.xml.processing.XmlProcessingContext;
import ec.demetra.xml.regression.XmlRegression;
import ec.demetra.xml.regression.XmlRegressionItem;
import ec.demetra.xml.regression.XmlStaticTsVariable;
import ec.demetra.xml.regression.XmlTsVariables;
import ec.demetra.xml.regression.XmlUserVariable;
import ec.demetra.xml.sa.x13.XmlCalendarSpec;
import ec.demetra.xml.sa.x13.XmlDefaultTradingDaysSpec;
import ec.demetra.xml.sa.x13.XmlOutliersSpec;
import ec.demetra.xml.sa.x13.XmlRegArimaSpecification;
import ec.demetra.xml.sa.x13.XmlRegressionSpec;
import ec.demetra.xml.sa.x13.XmlTradingDaysSpec;
import ec.demetra.xml.sa.x13.XmlTransformationSpec;
import ec.demetra.xml.sa.x13.XmlX11Spec;
import ec.demetra.xml.sa.x13.XmlX13Request;
import ec.demetra.xml.sa.x13.XmlX13Requests;
import ec.demetra.xml.sa.x13.XmlX13Specification;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.timeseries.calendars.DayEvent;
import java.net.URI;
import java.util.List;
import java.util.Random;
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
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Mats Maggi
 */
public class X13Test extends JerseyTest {

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
    public void x13XML() {
        XmlX13Request request = new XmlX13Request();
        request.setDefaultSpecification("RSA5c");
        XmlTs s = new XmlTs();
        XmlTsData.MARSHALLER.marshal(Data.P, s);
        request.setSeries(s);
        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");

        Response resp = callWSX13(request, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    @Ignore
    public void x13JSON() {
        XmlX13Request request = new XmlX13Request();
        request.setDefaultSpecification("RSA5c");
        XmlTs s = new XmlTs();
        XmlTsData.MARSHALLER.marshal(Data.P, s);
        request.setSeries(s);
        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");

        Response resp = callWSX13(request, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    @Test
    public void x13CustomXML() {
        XmlX13Request request = new XmlX13Request();
        XmlX13Specification spec = new XmlX13Specification();
        XmlRegArimaSpecification preprocessing = new XmlRegArimaSpecification();
        XmlTransformationSpec transformation = new XmlTransformationSpec();
        transformation.setLog(new XmlEmptyElement());
        preprocessing.setTransformation(transformation);
        XmlOutliersSpec outliers = new XmlOutliersSpec();
        outliers.setCriticalValue(3.5);
        outliers.getTypes().add("AO");
        preprocessing.setOutliers(outliers);
        XmlX11Spec decomposition = new XmlX11Spec();
        decomposition.setTrendMA(23);
        spec.setPreprocessing(preprocessing);
        spec.setDecomposition(decomposition);
        request.setSpecification(spec);
        XmlTs s = new XmlTs();
        XmlTsData.MARSHALLER.marshal(Data.P, s);
        request.setSeries(s);
        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");

        Response resp = callWSX13(request, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    @Ignore
    public void x13CustomJSON() {
        XmlX13Request request = new XmlX13Request();
        XmlX13Specification spec = new XmlX13Specification();
        XmlRegArimaSpecification preprocessing = new XmlRegArimaSpecification();
        XmlTransformationSpec transformation = new XmlTransformationSpec();
        transformation.setLog(new XmlEmptyElement());
        preprocessing.setTransformation(transformation);
        XmlOutliersSpec outliers = new XmlOutliersSpec();
        outliers.setCriticalValue(3.5);
        outliers.getTypes().add("AO");
        preprocessing.setOutliers(outliers);
        XmlX11Spec decomposition = new XmlX11Spec();
        decomposition.setTrendMA(23);
        spec.setPreprocessing(preprocessing);
        spec.setDecomposition(decomposition);
        request.setSpecification(spec);
        XmlTs s = new XmlTs();
        XmlTsData.MARSHALLER.marshal(Data.P, s);
        request.setSeries(s);
        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");

        Response resp = callWSX13(request, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    @Test
    public void x13CustomWithContextXML() {
        XmlX13Request request = new XmlX13Request();
        XmlX13Specification spec = new XmlX13Specification();
        // Preprocessing
        XmlRegArimaSpecification preprocessing = new XmlRegArimaSpecification();
        // transform
        XmlTransformationSpec transformation = new XmlTransformationSpec();
        transformation.setLog(new XmlEmptyElement());
        preprocessing.setTransformation(transformation);
        // calendar
        XmlCalendarSpec calendar = new XmlCalendarSpec();
        XmlTradingDaysSpec tradingDays = new XmlTradingDaysSpec();
        XmlDefaultTradingDaysSpec deftd = new XmlDefaultTradingDaysSpec();
        deftd.setCalendar("test");
        tradingDays.setDefault(deftd);
        calendar.setTradingDays(tradingDays);
        preprocessing.setCalendar(calendar);
        // outlier
        XmlOutliersSpec outliers = new XmlOutliersSpec();
        outliers.setCriticalValue(3.5);
        outliers.getTypes().add("AO");
        preprocessing.setOutliers(outliers);
        // user-defined regressors
        XmlRegressionSpec regression = new XmlRegressionSpec();
        XmlRegression variables = new XmlRegression();
        List<XmlRegressionItem> items = variables.getItems();
        XmlRegressionItem xvar = new XmlRegressionItem();
        XmlUserVariable xuser = new XmlUserVariable();
        xuser.setVariable("vars.reg1");
        xuser.setEffect(TsVariableDescriptor.UserComponentType.Irregular);
        xvar.setVariable(xuser);
        items.add(xvar);
        regression.setVariables(variables);
        preprocessing.setRegression(regression);
        // x11
        XmlX11Spec decomposition = new XmlX11Spec();
        decomposition.setTrendMA(23);
        spec.setPreprocessing(preprocessing);
        spec.setDecomposition(decomposition);
        request.setSpecification(spec);
        XmlProcessingContext context = generateContext();
        request.setContext(context);
        XmlTs s = new XmlTs();
        XmlTsData.MARSHALLER.marshal(Data.P, s);
        request.setSeries(s);
        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");
        request.getOutputFilter().add("regression.*");

        Response resp = callWSX13(request, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    @Ignore
    public void x13CustomWithContextJSON() {
        XmlX13Request request = new XmlX13Request();
        XmlX13Specification spec = new XmlX13Specification();
        // Preprocessing
        XmlRegArimaSpecification preprocessing = new XmlRegArimaSpecification();
        // transform
        XmlTransformationSpec transformation = new XmlTransformationSpec();
        transformation.setLog(new XmlEmptyElement());
        preprocessing.setTransformation(transformation);
        // calendar
        XmlCalendarSpec calendar = new XmlCalendarSpec();
        XmlTradingDaysSpec tradingDays = new XmlTradingDaysSpec();
        XmlDefaultTradingDaysSpec deftd = new XmlDefaultTradingDaysSpec();
        deftd.setCalendar("test");
        tradingDays.setDefault(deftd);
        calendar.setTradingDays(tradingDays);
        preprocessing.setCalendar(calendar);
        // outlier
        XmlOutliersSpec outliers = new XmlOutliersSpec();
        outliers.setCriticalValue(3.5);
        outliers.getTypes().add("AO");
        preprocessing.setOutliers(outliers);
        // user-defined regressors
        XmlRegressionSpec regression = new XmlRegressionSpec();
        XmlRegression variables = new XmlRegression();
        List<XmlRegressionItem> items = variables.getItems();
        XmlRegressionItem xvar = new XmlRegressionItem();
        XmlUserVariable xuser = new XmlUserVariable();
        xuser.setVariable("vars.reg1");
        xuser.setEffect(TsVariableDescriptor.UserComponentType.Irregular);
        xvar.setVariable(xuser);
        items.add(xvar);
        regression.setVariables(variables);
        preprocessing.setRegression(regression);
        // x11
        XmlX11Spec decomposition = new XmlX11Spec();
        decomposition.setTrendMA(23);
        spec.setPreprocessing(preprocessing);
        spec.setDecomposition(decomposition);
        request.setSpecification(spec);
        XmlProcessingContext context = generateContext();
        request.setContext(context);
        XmlTs s = new XmlTs();
        XmlTsData.MARSHALLER.marshal(Data.P, s);
        request.setSeries(s);
        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");
        request.getOutputFilter().add("regression.*");

        Response resp = callWSX13(request, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    @Test
    public void x13RequestsXML() {
        XmlX13Requests requests = new XmlX13Requests();
        requests.setFlat(true);
        for (int i = 0; i < 5; ++i) {
            XmlX13Request cur = new XmlX13Request();
            cur.setDefaultSpecification("RSA5c");
            XmlTs s = new XmlTs();
            XmlTsData.MARSHALLER.marshal(Data.P, s);
            cur.setSeries(s);
            requests.getItems().add(cur);
        }

        requests.getOutputFilter().add("arima.*");
        requests.getOutputFilter().add("likelihood.*");
        requests.getOutputFilter().add("residuals.*");
        requests.getOutputFilter().add("*_f");

        Response resp = callWSX13(requests, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    @Ignore
    public void x13RequestsJSON() {
        XmlX13Requests requests = new XmlX13Requests();
        requests.setFlat(true);
        for (int i = 0; i < 5; ++i) {
            XmlX13Request cur = new XmlX13Request();
            cur.setDefaultSpecification("RSA5c");
            XmlTs s = new XmlTs();
            XmlTsData.MARSHALLER.marshal(Data.P, s);
            cur.setSeries(s);
            requests.getItems().add(cur);
        }

        requests.getOutputFilter().add("arima.*");
        requests.getOutputFilter().add("likelihood.*");
        requests.getOutputFilter().add("residuals.*");
        requests.getOutputFilter().add("*_f");

        Response resp = callWSX13(requests, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            Assert.assertEquals(200, resp.getStatus());
            XmlInformationSet set = resp.readEntity(XmlInformationSet.class);
            Assert.assertNotNull(set);
        }
    }

    private Response callWSX13(XmlX13Request request, String inputType, String outputType) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
        Response resp = jwt.path("x13/request")
                .request(outputType)
                .acceptEncoding("gzip")
                .post(Entity.entity(request, inputType));

        return resp;
    }

    private Response callWSX13(XmlX13Requests requests, String inputType, String outputType) {
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        jcb.register(GZipEncoder.class);
        JerseyClient jc = jcb.build();

        JerseyWebTarget jwt = jc.target(getBaseUri());
        Response resp = jwt.path("x13/requests")
                .request(outputType)
                .acceptEncoding("gzip")
                .post(Entity.entity(requests, inputType));

        return resp;
    }

    private static XmlNationalCalendar generateCalendar() {
        XmlNationalCalendar nc = new XmlNationalCalendar();
        nc.setName("test");
        XmlSpecialCalendarDay christmas = new XmlSpecialCalendarDay();
        christmas.setEvent(DayEvent.Christmas);
        XmlSpecialDayEvent ev1 = new XmlSpecialDayEvent();
        ev1.setDay(christmas);
        nc.getSpecialDayEvent().add(ev1);
        XmlSpecialCalendarDay ny = new XmlSpecialCalendarDay();
        ny.setEvent(DayEvent.NewYear);
        XmlSpecialDayEvent ev2 = new XmlSpecialDayEvent();
        ev1.setDay(christmas);
        nc.getSpecialDayEvent().add(ev2);
        return nc;
    }

    private static XmlProcessingContext generateContext() {
        XmlProcessingContext context = new XmlProcessingContext();
        XmlCalendars cal = new XmlCalendars();
        cal.getCalendars().add(generateCalendar());
        context.setCalendars(cal);
        XmlStaticTsVariable var = new XmlStaticTsVariable();
        var.setName("reg1");
        XmlTsData data = new XmlTsData();
        double[] r = new double[600];
        Random rnd = new Random();
        for (int i = 0; i < r.length; ++i) {
            r[i] = rnd.nextDouble();
        }
        data.setValues(r);
        data.setFrequency(12);
        data.setFirstPeriod(1);
        data.setFirstYear(1960);
        var.setTsData(data);
        XmlTsVariables xvars = new XmlTsVariables();
        xvars.setName("vars");
        xvars.getVariables().add(var);
        XmlProcessingContext.Variables vars = new XmlProcessingContext.Variables();
        vars.getGroup().add(xvars);
        context.setVariables(vars);
        return context;
    }

}
