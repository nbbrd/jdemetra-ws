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
package ec.nbb.demetra.json.benchmarking;

import ec.benchmarking.simplets.TsCholette;
import ec.tss.xml.XmlTsData;
import ec.tstoolkit.timeseries.TsAggregationType;

/**
 *
 * @author Mats Maggi
 */
public class JsonCholetteProcessing {

    public XmlTsData x, y;
    public double rho = 1, lambda = 1;
    public String bias = TsCholette.BiasCorrection.None.toString();
    public TsAggregationType agg = TsAggregationType.Sum;
}
