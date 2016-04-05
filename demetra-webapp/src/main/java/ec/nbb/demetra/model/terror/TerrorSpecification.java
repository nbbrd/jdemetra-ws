/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
 versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 * See the Licence for the specific language governing
 permissions and limitations under the Licence.
 */
package ec.nbb.demetra.model.terror;

import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import java.util.LinkedHashMap;

/**
 *
 * @author palatej
 */
public class TerrorSpecification implements IProcSpecification {

    static void fillDictionary(String prefix, LinkedHashMap<String, Class> dic) {
        dic.put(InformationSet.item(prefix, METHOD), String.class);
        dic.put(InformationSet.item(prefix, NBACK), Integer.class);
    }

    private String method_ = "TR4";
    private int nback_ = 1;
    public static final String METHOD = "method", NBACK = "nback";

    public TerrorSpecification() {
    }

    @Override
    public TerrorSpecification clone() {
        try {
            return (TerrorSpecification) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public InformationSet write(boolean verbose) {
        return null;
//        InformationSet specInfo = new InformationSet();
//        specInfo.add(ALGORITHM, TerrorProcessingFactory.DESCRIPTOR);
//        if (nback_ != -1 || verbose) {
//            specInfo.set(NBACK, nback_);
//        }
//        specInfo.set(METHOD, method_);
//        return specInfo;
    }

    @Override
    public boolean read(InformationSet info) {
        return true;
//        Integer n = info.get(NBACK, Integer.class);
//        if (n != null)
//            nback_=n;
//        String m=info.get(METHOD, String.class);
//        if (m == null)
//            return false;
//        String M=m.toUpperCase();
//        if (null == TramoSpecification.Default.valueOf(M)) {
//            return false;
//        }
//        method_=M;
//        return true;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method_;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(final String method) {
        String M=method.toUpperCase();
        if (null == TramoSpecification.Default.valueOf(M)) {
            throw new IllegalArgumentException();
        }
        this.method_ = M;
    }

    /**
     * @return the nback
     */
    public int getNback() {
        return nback_;
    }

    /**
     * @param nback the nback to set
     */
    public void setNback(int nback) {
        if (nback < 1) {
            throw new IllegalArgumentException();
        }
        this.nback_ = nback;
    }
}
