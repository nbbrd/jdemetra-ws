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
package ec.nbb.demetra.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

/**
 * GZIP Interceptor that reads all requests and look for a gzip header. If the
 * header exists, the response input stream in converted to a gzip stream to
 * allow the decompression
 *
 * @author Mats Maggi
 */
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
