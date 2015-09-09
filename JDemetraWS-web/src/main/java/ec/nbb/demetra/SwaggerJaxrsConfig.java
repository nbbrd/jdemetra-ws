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

import io.swagger.jaxrs.config.BeanConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author Mats Maggi
 */
@WebServlet(name = "SwaggerJaxrsConfig", loadOnStartup = 2)
public class SwaggerJaxrsConfig extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) {
        try {
            super.init(servletConfig);
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setVersion("1.0.0");
            beanConfig.setSchemes(new String[]{"http"});
            beanConfig.setHost("srvdqrdd2.nbb.local:9998");
            beanConfig.setTitle("JDemetra Web Service");
            beanConfig.setBasePath("/demetra/api");
            beanConfig.setResourcePackage("ec.nbb.demetra.rest");
            beanConfig.setScan(true);

        } catch (ServletException e) {
            System.out.println(e.getMessage());
        }
    }
}
