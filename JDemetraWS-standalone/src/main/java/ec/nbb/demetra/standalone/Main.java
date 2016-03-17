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

import ec.nbb.demetra.json.JacksonJsonProvider;
import io.swagger.jersey.listing.ApiListingResourceJSON;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 *
 * @author Mats Maggi
 */
public class Main {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:9998/demetra/api/";
    public static final int DEFAULT_PORT = 9998;

    public static HttpServer startServer(int port) {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig rc = new ResourceConfig()
                .packages("ec.nbb.demetra.rest")
                .register(ec.nbb.demetra.exception.DemetraExceptionMapper.class)
                .register(ec.nbb.demetra.filter.GZipWriterInterceptor.class)
                .register(ec.nbb.demetra.filter.GZipReaderInterceptor.class)
                .register(ApiListingResourceJSON.class)
                .register(io.swagger.jaxrs.listing.SwaggerSerializers.class)
                .register(JacksonJsonProvider.class)
                .register(org.glassfish.jersey.jackson.JacksonFeature.class);

        UriBuilder builder = UriBuilder.fromUri(BASE_URI).port(port);

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

        server.start();

        System.out.println(String.format("Jersey app started. Service available at "
                + "%s\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdown();
    }

    private static void printResources() {
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner(), new MethodAnnotationsScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("ec.nbb.demetra.rest"))));

        Set<Method> posts = reflections.getMethodsAnnotatedWith(POST.class);
        System.out.println("\n==== [POST] ====");
        for (Method m : posts) {
            Path root = m.getDeclaringClass().getAnnotation(Path.class);
            String rootPath = (root == null) ? "/" : root.value();
            Path annotation = m.getAnnotation(Path.class);
            String path = (annotation == null) ? "/" : annotation.value();
            System.out.println(String.format("[%s] => %s%s", m.getName(), rootPath, path));
        }
        Set<Method> gets = reflections.getMethodsAnnotatedWith(GET.class);

        System.out.println("\n==== [GET] ====");
        for (Method m : gets) {
            Path root = m.getDeclaringClass().getAnnotation(Path.class);
            String rootPath = (root == null) ? "/" : root.value();
            Path annotation = m.getAnnotation(Path.class);
            String path = (annotation == null) ? "/" : annotation.value();
            System.out.println(String.format("[%s] => %s%s", m.getName(), rootPath, path));
        }
    }
}