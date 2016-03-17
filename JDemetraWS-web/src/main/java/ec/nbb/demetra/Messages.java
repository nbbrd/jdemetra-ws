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
package ec.nbb.demetra;

/**
 * Error messages used by the web services
 *
 * @author Mats Maggi
 */
public class Messages {

    // GENERAL
    public static final String TS_NULL = "The given ts is null !";
    public static final String TSCOLLECTION_NULL = "The given collection of ts is null or empty !";
    public static final String TS_EMPTY = "The given ts doesn't contain any observation !";
    public static final String UNKNOWN_METHOD = "Unable to recognize the provided method : %s !";
    public static final String UNKNOWN_SPEC = "Could not find a default specification from : %s !";
    public static final String PROCESSING_ERROR = "The processing has returned no results !";
    public static final String NO_SERIES = "At least one time series must be provided !";
    public static final String TS_CREATION_ERROR = "Unable to create the Ts (Name : %s) !";

    // CHECK LAST
    public static final String POSITIVE_NB_LAST = "NbLast parameter must be greater than 0 (provided : %d) !";
    public static final String CHECKLAST_ERROR = "The Check Last processing has failed !";

    // HODRICK PRESCOTT
    public static final String HP_ERROR = "Hodrick Prescott processing has returned an error !";
    public static final String UNKNOWN_TARGET = "Unable to get the targetted Sa series for target : %s !";

    // OUTLIERS DETECTION
    public static final String NO_OUTLIERS_TYPE = "At least one outlier type must be specified !";
}
