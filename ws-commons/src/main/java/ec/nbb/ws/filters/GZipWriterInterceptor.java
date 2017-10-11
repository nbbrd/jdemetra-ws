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
package ec.nbb.ws.filters;

import ec.nbb.ws.annotations.Compress;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import jersey.repackaged.com.google.common.base.MoreObjects;

/**
 * GZIP Interceptor that wraps the response in a gzip output stream. The
 * responses of methods that are annotated with <code>@Compress</code> are
 * automatically gzipped by this interceptor
 *
 * @author Mats Maggi
 */
@Provider
@Compress
public class GZipWriterInterceptor implements WriterInterceptor {

    @Context
    private HttpHeaders httpHeaders;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        MultivaluedMap<String, String> requestHeaders = httpHeaders.getRequestHeaders();
        List<String> acceptEncoding = MoreObjects.firstNonNull(requestHeaders.get(HttpHeaders.ACCEPT_ENCODING), new ArrayList<String>());

        // Compress if client accepts gzip encoding
        for (String s : acceptEncoding) {
            if (s.contains("gzip")) {
                context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, "gzip");

                final OutputStream outputStream = context.getOutputStream();
                context.setOutputStream(new GZIPOutputStream(outputStream));

                break;
            }
        }
        context.proceed();
    }
}
