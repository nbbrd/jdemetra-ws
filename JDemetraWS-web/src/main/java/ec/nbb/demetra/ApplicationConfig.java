/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbb.demetra;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Mats Maggi
 */
@ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        // Swagger resources
        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        resources.add(io.swagger.jaxrs.json.JacksonJsonProvider.class);

        // Application resources
        resources.add(ec.nbb.demetra.exception.DemetraExceptionMapper.class);
        resources.add(org.glassfish.jersey.jackson.JacksonFeature.class);
        resources.add(ec.nbb.demetra.filter.GZipWriterInterceptor.class);
        resources.add(ec.nbb.demetra.filter.GZipReaderInterceptor.class);
        resources.add(ec.nbb.demetra.rest.TerrorResource.class);
        resources.add(ec.nbb.demetra.rest.HelloResource.class);
        resources.add(ec.nbb.demetra.rest.AnomalyDetectionResource.class);
        resources.add(ec.nbb.demetra.rest.BalancingResource.class);
        resources.add(ec.nbb.demetra.rest.SeasonalAdjustmentResource.class);

        return resources;
    }
}
