##JDemetra+ Web Service

This web service provides a way to interact with JDemetra+ algorithms over the REST technology.

It is a free and open-source software (FOSS) developed under the [EUPL licence](http://ec.europa.eu/idabc/eupl).

##Client-side

* Register `GZipEncoder.class` to deserialize response
* Register `GZipDecoder.class` to serialize the request (post the GZIPInputStream as entity)
* Register `GZIPContentEncodingFilter.class` to add the header “Content-Encoding” to the re
quest (otherwise the call overrides the explicit given header)

###Call Example

	JerseyClientBuilder jcb = new JerseyClientBuilder();
	jcb.register(GZipEncoder.class);
	JerseyClient jc = jcb.build();
	
	JerseyWebTarget jwt = jc.target("http://localhost:9998/demetra/api");
	Response resp = jwt.path("sa/x13/RSA4c")
	    .request(MediaType.APPLICATION_JSON)
	    .acceptEncoding("gzip")
	    .post(Entity.entity(ts, MediaType.APPLICATION_JSON));

##Server-side
_NB : The standalone project can be built to produce a jar that starts the server (so, an application server like Glassfish is not needed but the swagger documentation can't be accessed at the moment)_

###Exception Mapper

Create a class implementing `ExceptionMapper<T>` and annotate it with `@Provider`

####Example

    @Provider
    public class DemetraExceptionMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable exception) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                           .entity(exception.getMessage())
                           .type(MediaType.TEXT_PLAIN)
                           .build();
        }
    }

##Application Config

To avoid using a web.xml file, a class can take the role of configuring and registering providers/servlets/resources. Create a class extending Application and annotate it with `@ApplicationPath(“path-name”)`. Path name corresponds to the api path on which the configuration has to be applied.

Using swagger, you need to add these resources :

* `io.swagger.jaxrs.listing.ApiListingResource.class` (Resource generating Api doc)
* `io.swagger.jaxrs.listing.SwaggerSerializers.class` (Serializers used by the web browser)
* `io.swagger.jaxrs.json.JacksonJsonProvider.class` (Mapper used for Json serialization)

You’ll also need to register the **ExceptionMapper**, all the resources (classes containing web service methods), and if used the **GZipWriterInterceptor** and/or **GZipReaderInterceptor**.

##GZip Compression

Filters catching the request or response and wraps the output/input stream in a corresponding GZip stream to enable automatic compression/decompression.

###GZipReaderInterceptor

    @Provider
    public class GZipReaderInterceptor implements ReaderInterceptor {

	    @Override
	    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
	        String header = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
	        if (header != null && header.contains("gzip")) {
	            InputStream originalInputStream = context.getInputStream();
	            context.setInputStream(new GZIPInputStream(originalInputStream));
	        }
	        return context.proceed();
	    }
    }

###GZipWriterInterceptor

    @Provider
    @Compress
    public class GZipWriterInterceptor implements WriterInterceptor {
	 
        @Override
        public void aroundWriteTo(WriterInterceptorContext context)
                    throws IOException, WebApplicationException {
            context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, "gzip");
        
            final OutputStream outputStream = context.getOutputStream();
            context.setOutputStream(new GZIPOutputStream(outputStream));
            context.proceed();
        }
    }


###@Compress annotation

Enables the compression of a response only on web service methods annotated with `@Compress`

    @NameBinding
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Compress { }

##Cors Filter

A filter is needed to allow request from cross domains (browser, code, …)

###Example

    @WebFilter(filterName = "HTML5CorsFilter", urlPatterns = {"/api/*"})
    public class HTML5CorsFilter implements javax.servlet.Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletResponse res = (HttpServletResponse) response;
            res.addHeader("Access-Control-Allow-Origin", "*");
            res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
            res.addHeader("Access-Control-Allow-Headers", "Content-Type, Content-Encoding, Accept-Encoding");
            chain.doFilter(request, response);
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException { }

        @Override
        public void destroy() { }
    }

##Glassfish configuration

###SSL Security

Activate the SSL3 under `Configurations > server-config > Network Listeners > http-listener-2 > SSL`

To allow SSL enabling, you have to add 2 JVM options for the server-config :

- `-Djavax.net.ssl.trustStorePassword=master-password`
- `-Djavax.net.ssl.keyStorePassword=master-password`

###Certificate

Glassfish creates its own keystore and cacerts when creating the domain. They can be found under :
 `<Glassfish Folder>\glassfish\domains\domain1\config`.
* keystore.jks (contains a single certificate)
* cacerts.jks (contains trusted certificates)

_NB : Passwords are by default “changeit”_

If the certificate is not signed by a trusted organization, you’ll need to install it to your Java certificates. To do so, you’ll first need to export the certificate from the keystore :
`keytool -export -alias s1as -file s1as.crt -keystore keystore.jks`

And then import it into the trusted certificates :
`keytool -import -trustcacerts -file s1as.crt -alias s1as -keystore $JAVA_HOME/jre/lib/security/cacerts`

If you need to generate a keystore :
`keytool -genkey -alias <alias> -keyalg RSA -keystore <file.jks> -keysize 2048`

**_Important : When prompted for a First and Last Name (saved as CN in the keystore), you need to provide the host name (Example : if service is hosted at “www.myservice.be/api/”, the CN must be “www.myservice.be”)_**

You can then import it into the cacerts of Glassfish (after generating the crt file : see above)
`keytool -import -trustcacerts -file <file.crt> -alias <alias> -keystore cacerts.jks`

If you want to delete a previously added certificate to a keystore :
`keytool -delete -alias <alias> -keystore <keystore.jks>`

To list certificates informations contained in a keystore :
`keytool -list -v -keystore <keystore.jks>`

(Add `-alias <alias>` if you only want to show one specified certificate)

###Replacement of old libraries

Out of the box, Glassfish 4.1 contains old versions of Jackson libraries which create some conflicts. I replaced them with the version 2.4.5 (or newest if available). 
These libraries can be found under `<Glassfish Folder>\glassfish\modules` :

* jackson-databind.jar
* jackson-jaxrs-base.jar
* jackson-jaxrs-json-provider.jar
* jackson-annotations.jar
* jackson-core.jar
* jackson-module-jaxb-annotations.jar

##Swagger Configuration servlet

This servlet is used to generate some documentation over the API and to configure the Swagger UI that will parse the API to generate the documentation.

_NB : The setResourcePackage(String package) allows defining the package location of resources. Several resources can be added by separating them with a “,”_

    @WebServlet(name = "SwaggerJaxrsConfig", loadOnStartup = 2)
    public class SwaggerJaxrsConfig extends HttpServlet {

        @Override
        public void init(ServletConfig servletConfig) {
            try {
                super.init(servletConfig);
                BeanConfig beanConfig = new BeanConfig();
                beanConfig.setVersion("1.0.0");
                beanConfig.setSchemes(new String[]{"http"});
                beanConfig.setHost("localhost:9998");
                beanConfig.setPrettyPrint(true);
                beanConfig.setTitle("JDemetra+ Web Service");
                Info info = new Info();
                info.setTitle("JDemetra+ Web Service");
                info.setDescription("Web service JDemetra+ algorithms");
                info.setVersion("1.0.0");
                beanConfig.setInfo(info);

                beanConfig.setBasePath("/demetra/api");
                beanConfig.setResourcePackage("ec.nbb.demetra.rest");
                beanConfig.setScan(true);      
            } catch (ServletException e) {
                System.out.println(e.getMessage());
            }
        }
    }

