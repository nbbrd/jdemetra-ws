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

import java.net.URI;
import java.net.URISyntaxException;
import org.openide.util.Exceptions;

/**
 *
 * @author Mats Maggi
 */
public class TestConfig {

    private static final boolean isLocal = true;

    public static final String LOCAL_URL = "http://localhost:9998/demetra/api";

    public static String getUrl() {
        return LOCAL_URL;
    }

    public static URI getURI() {
        try {
            return new URI(getUrl());
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
