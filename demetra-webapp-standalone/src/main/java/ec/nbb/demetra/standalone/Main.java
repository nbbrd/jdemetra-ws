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
package ec.nbb.demetra.standalone;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import java.io.IOException;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author Mats Maggi
 */
public class Main {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:9998/demetra/api/";
    public static final int DEFAULT_PORT = 9998;

    public static HttpServer startServer(int port) {

        final ResourceConfig rc = new ResourceConfig()
                .packages("ec.nbb.demetra.rest")
                .register(ec.nbb.demetra.exception.DemetraExceptionMapper.class)
                .register(ec.nbb.ws.filters.GZipWriterInterceptor.class)
                .register(ec.nbb.ws.filters.GZipReaderInterceptor.class)
                .register(io.swagger.jaxrs.listing.ApiListingResource.class)
                .register(io.swagger.jaxrs.listing.SwaggerSerializers.class)
                .register(ec.nbb.ws.json.JacksonJsonProvider.class)
                .register(org.glassfish.jersey.jackson.JacksonFeature.class)
                .register(ec.nbb.ws.filters.CORSFilter.class);

        UriBuilder builder = UriBuilder.fromUri(BASE_URI).port(port);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("2.2.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setPrettyPrint(true);
        beanConfig.setTitle("JDemetra+ Web Service");
        Info info = new Info();
        info.setTitle("JDemetra+ Web Service");
        info.setDescription("Web service providing access to JDemetra+ algorithms and utilities");
        info.setVersion("2.2.0");
        beanConfig.setInfo(info);

        beanConfig.setBasePath("/demetra/api");
        // Package containing web services to scan
        beanConfig.setResourcePackage("ec.nbb.demetra.rest");
        beanConfig.setScan(true);

        return GrizzlyHttpServerFactory.createHttpServer(builder.build(), rc);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = null;
        try {
            int port = Integer.parseInt(args[0]);
            server = startServer(port);
        } catch (Exception ex) {
            server = startServer(DEFAULT_PORT);
        }
        CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(Main.class.getClassLoader(), "/webapp/");
        httpHandler.setFileCacheEnabled(false);
        server.getServerConfiguration().addHttpHandler(httpHandler, "/demetra/api");
        server.start();

        System.out.println(String.format("Jersey app started. Service available at "
                + "%s\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdown();
    }
}
