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
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import io.swagger.util.Json;
import java.net.URI;
import java.net.URISyntaxException;
import org.openide.util.Exceptions;

/**
 *
 * @author Mats Maggi
 */
public class TestConfig {

    public static final String LOCAL_URL = "http://localhost:9998/demetra/api";

    public static URI getURI() {
        try {
            return new URI(LOCAL_URL);
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    public static String serializationJson(Object obj) throws JsonProcessingException {
        ObjectMapper commonMapper = Json.mapper();
        AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
        // first Jaxb, second Jackson annotations
        commonMapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
        commonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        commonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        return commonMapper.writeValueAsString(obj);
    }

}
