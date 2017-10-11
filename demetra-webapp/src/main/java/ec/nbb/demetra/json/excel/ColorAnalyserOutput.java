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
package ec.nbb.demetra.json.excel;

import com.fasterxml.jackson.annotation.JsonProperty;
import ec.tstoolkit.sarima.SarimaSpecification;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mats Maggi
 */
public class ColorAnalyserOutput {

    public static class Outlier {

        @JsonProperty("position")
        public int position;
        @JsonProperty("code")
        public String code;
        @JsonProperty("value")
        public double value;
        @JsonProperty("stdError")
        public double stdError;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(code).append("_").append(position).append('\t').append(value).append('\t').append(stdError).append(System.lineSeparator());
            return builder.toString();
        }
    }

    public static class Missing {

        @JsonProperty("position")
        public int position;
        @JsonProperty("value")
        public double value;
        @JsonProperty("stdError")
        public double stdError;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(position).append('\t').append(value).append('\t').append(stdError).append(System.lineSeparator());
            return builder.toString();
        }
    }

    @JsonProperty("logs")
    public boolean logs;
    @JsonProperty("arima")
    public SarimaSpecification arima;
    @JsonProperty("td")
    public int td;
    @JsonProperty("easter")
    public boolean easter;
    @JsonProperty("outliers")
    public List<Outlier> outliers = new ArrayList<>();
    @JsonProperty("missings")
    public List<Missing> missings = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Log: ").append(logs).append(System.lineSeparator());
        builder.append("Arima: ").append(arima).append(System.lineSeparator());
        builder.append("TD: ").append(td).append(System.lineSeparator());
        builder.append("Easter: ").append(easter).append(System.lineSeparator());
        builder.append("Outliers: ").append(System.lineSeparator());
        outliers.forEach(o -> builder.append("    ").append(o));
        builder.append("Missings: ").append(System.lineSeparator());
        missings.forEach(o -> builder.append("    ").append(o));

        return builder.toString();
    }
}
