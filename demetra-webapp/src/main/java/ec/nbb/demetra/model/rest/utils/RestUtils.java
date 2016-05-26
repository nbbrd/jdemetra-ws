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
package ec.nbb.demetra.model.rest.utils;

import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import io.swagger.annotations.Api;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Mats Maggi
 */
@Path("/test/xml")
@Api(value = "/test/xml")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class RestUtils {

    public static TsDomain createTsDomain(int start, int end, TsFrequency freq) {
        TsPeriod s = toPeriod(start, freq);
        TsPeriod e = toPeriod(end, freq);
        return new TsDomain(s, e.minus(s));
    }

    public static TsPeriod toPeriod(int period, TsFrequency freq) {
        if (freq.equals(TsFrequency.Undefined)) {
            freq = TsFrequency.Monthly;
        }
        TsPeriod p = new TsPeriod(freq);
        p.set(period / freq.intValue(), period % freq.intValue());
        return p;
    }

    public static int fromTsPeriod(TsPeriod p) {
        int y = p.getYear();
        int pos = p.getPosition();
        int id = y * p.getFrequency().intValue() + pos;
        return id;
    }
}
