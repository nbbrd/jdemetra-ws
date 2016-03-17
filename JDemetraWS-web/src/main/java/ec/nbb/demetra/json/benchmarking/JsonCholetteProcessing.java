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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * POJO use for the Cholette Processing request
 *
 * @author Mats Maggi
 */
@ApiModel
public class JsonCholetteProcessing {

    @ApiModelProperty
    public XmlTsData x;

    @ApiModelProperty
    public XmlTsData y;

    @ApiModelProperty
    public double rho = 1;

    @ApiModelProperty
    public double lambda = 1;

    @ApiModelProperty
    public String bias = TsCholette.BiasCorrection.None.toString();

    @ApiModelProperty
    public TsAggregationType agg = TsAggregationType.Sum;
}
