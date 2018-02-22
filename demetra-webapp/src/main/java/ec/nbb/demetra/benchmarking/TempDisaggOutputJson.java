/*
 * Copyright 2017 National Bank of Belgium
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
package ec.nbb.demetra.benchmarking;

import com.fasterxml.jackson.annotation.JsonProperty;
import ec.nbb.demetra.json.excel.ExcelSeries;
import ec.nbb.demetra.model.rest.utils.RestUtils;

/**
 *
 * @author Mats Maggi
 */
public class TempDisaggOutputJson {
    
    public TempDisaggOutputJson(TempDisaggOutput output) {
        pred = RestUtils.toExcelSeries(output.getPred(), "pred");
        sepred = RestUtils.toExcelSeries(output.getSePred(), "sepred");
        coeffs = output.getCoeff();
        secoeffs = output.getSeCoeff();
        rho = output.getRho();
        dw = output.getDw();
        tstats = output.getTStats();
    }
    
    @JsonProperty("pred")
    public ExcelSeries pred;
    
    @JsonProperty("sepred")
    public ExcelSeries sepred;
    
    @JsonProperty("coeffs")
    public double[] coeffs;
    
    @JsonProperty("secoeffs")
    public double[] secoeffs;

    @JsonProperty("tstats")
    public double[] tstats;
    
    @JsonProperty("rho")
    public double rho;
    
    @JsonProperty("dw")
    public double dw;
}
