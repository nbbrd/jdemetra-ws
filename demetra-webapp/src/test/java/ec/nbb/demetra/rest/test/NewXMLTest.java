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
import ec.demetra.xml.XmlEmptyElement;
import ec.demetra.xml.calendars.XmlCalendars;
import ec.demetra.xml.calendars.XmlNationalCalendar;
import ec.demetra.xml.calendars.XmlSpecialCalendarDay;
import ec.demetra.xml.calendars.XmlSpecialDayEvent;
import ec.demetra.xml.core.XmlInformation;
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
import ec.demetra.xml.sa.x13.XmlX13Specification;
import ec.tstoolkit.information.Information;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.timeseries.calendars.DayEvent;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for the outlier detection service
 *
 * @author Mats Maggi
 */
public class NewXMLTest extends JerseyTest {

    private final static String INFOSET = "test/infoset", INFO = "test/info", TSDATA = "test/tsdata", X13_REQUEST = "test/x13/request";
    private static JerseyWebTarget jwt;

    @ClassRule
    public static TemporaryFolder temp = new TemporaryFolder();

    @Override
    protected Application configure() {
        Logger logger = Logger.getGlobal();
        logger.setLevel(Level.ALL);
        return new ResourceConfig()
                .packages("ec.nbb.demetra.rest")
                .register(ec.nbb.demetra.exception.DemetraExceptionMapper.class)
                .register(ec.nbb.ws.filters.GZipWriterInterceptor.class)
                .register(ec.nbb.ws.filters.GZipReaderInterceptor.class)
                .register(ec.nbb.ws.filters.CORSFilter.class)
                .register(new LoggingFilter(logger, true));
    }

    @Override
    protected URI getBaseUri() {
        return TestConfig.getURI();
    }

    @BeforeClass
    public static void setupJerseyLog() throws Exception {
        Handler fh = new FileHandler(temp.newFile("jersey_test.log").getAbsolutePath());
        Logger.getLogger("").addHandler(fh);
        Logger.getLogger("com.sun.jersey").setLevel(Level.FINEST);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        JerseyClientBuilder jcb = new JerseyClientBuilder();
        JerseyClient jc = jcb.build();
        jwt = jc.target(getBaseUri());
        jwt.register(GZipEncoder.class);
        //jwt.register(MyJacksonJsonProvider.class);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        jwt = null;
    }

    @Test
    public void xmlTsData() {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlTsData ts = new XmlTsData();
        XmlTsData.MARSHALLER.marshal(d, ts);

        Response jsonResp = callWS(TSDATA, ts, MediaType.APPLICATION_JSON);
        if (jsonResp.getStatus() == 400) {
            Assert.fail(jsonResp.readEntity(String.class));
        } else {
            XmlTsData json = jsonResp.readEntity(XmlTsData.class);
            System.out.println(Arrays.toString(json.getValues()));
        }
    }

    @Test
    public void xmlInformation() throws Exception {
        TsData d = TsData.random(TsFrequency.Monthly);
        Information info = new Information("y", d);
        XmlInformation xmlInfo = XmlInformation.create(info);

        Response jsonResp = callWS(INFO, xmlInfo, MediaType.APPLICATION_JSON);
        Assert.assertEquals(200, jsonResp.getStatus());
        XmlInformation json = jsonResp.readEntity(XmlInformation.class);
        Information<Object> obj = json.toInformation();
    }

    @Test
    public void xmlInformationSet() throws Exception {
        TsData d = TsData.random(TsFrequency.Monthly);
        XmlInformationSet xmlSet = new XmlInformationSet();
        InformationSet set = new InformationSet();
        set.add("y", d);
        xmlSet.copy(set);

        Response xmlResp = callWS(INFOSET, xmlSet, MediaType.APPLICATION_XML);
        Assert.assertEquals(200, xmlResp.getStatus());
        XmlInformationSet xml = xmlResp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(xml.item);
        Assert.assertEquals(1, xml.item.length);
        Information<Object> obj = xml.item[0].toInformation();
        Assert.assertTrue(obj.value instanceof TsData);
        Assert.assertEquals(d, obj.value);

        Response jsonResp = callWS(INFOSET, xmlSet, MediaType.APPLICATION_XML);
        Assert.assertEquals(200, jsonResp.getStatus());
        XmlInformationSet json = jsonResp.readEntity(XmlInformationSet.class);
        Assert.assertNotNull(json.item);
        Assert.assertEquals(1, json.item.length);
        Information<Object> result = (json.item[0].toInformation());
        Assert.assertTrue(result.value instanceof TsData);
        Assert.assertEquals(d, obj.value);
    }

    @Test
    public void xmlX13Request() {
        XmlX13Request request = new XmlX13Request();
        request.setDefaultSpecification("RSA5c");
        XmlTs s = new XmlTs();
        XmlTsData.MARSHALLER.marshal(Data.P, s);
        request.setSeries(s);

        request.getOutputFilter().add("arima.*");
        request.getOutputFilter().add("likelihood.*");
        request.getOutputFilter().add("residuals.*");
        request.getOutputFilter().add("*_f");

        Response xmlResp = callWS(X13_REQUEST, request, MediaType.APPLICATION_XML);
        Assert.assertEquals(200, xmlResp.getStatus());
        if (xmlResp.getStatus() != 200) {
            Assert.fail(xmlResp.readEntity(String.class));
        } else {
            XmlX13Request json = xmlResp.readEntity(XmlX13Request.class);
            Assert.assertEquals(s.getValues().length, json.getSeries().getValues().length);
        }
    }

    @Test
    public void x13CustomWithContext() throws JsonProcessingException {
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
        request.getOutputFilter().add("decomposition.d-tables.d10");
        request.getOutputFilter().add("decomposition.d-tables.d11");
        request.getOutputFilter().add("decomposition.a-tables.a1");
        request.getOutputFilter().add("cal");

        String in = TestConfig.serializationJson(request);
        Response resp = callWS(X13_REQUEST, request, MediaType.APPLICATION_XML);

        if (resp.getStatus() != 200) {
            Assert.fail(resp.readEntity(String.class));
        } else {
            XmlX13Request result = resp.readEntity(XmlX13Request.class);
            String out = TestConfig.serializationJson(result);
            Assert.assertEquals(in, out);
        }
    }

    public Response callWS(String path, Object set, String type) {
        return jwt.path(path)
                .request(type)
                .acceptEncoding("gzip")
                .post(Entity.entity(set, type));
    }

    static XmlNationalCalendar generateCalendar() {
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
        ev2.setDay(ny);
        nc.getSpecialDayEvent().add(ev2);
        return nc;
    }

    static XmlProcessingContext generateContext() {
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

