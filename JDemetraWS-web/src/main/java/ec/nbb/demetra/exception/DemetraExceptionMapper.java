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
package ec.nbb.demetra.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Mats Maggi
 */
@Provider
public class DemetraExceptionMapper implements ExceptionMapper<TerrorException> {

    @Override
    public Response toResponse(TerrorException exception) {
        exception.printStackTrace();
        return Response.serverError().entity(exception.getMessage()).build();

//        if (exception instanceof NotFoundException) {
//            return Response.status(Status.NOT_FOUND)
//                    .entity(new ApiResponse(ApiResponse.ERROR, exception
//                                    .getMessage())).build();
//        } else if (exception instanceof BadRequestException) {
//            return Response.status(Status.BAD_REQUEST)
//                    .entity(new ApiResponse(ApiResponse.ERROR, exception
//                                    .getMessage())).build();
//        } else if (exception instanceof ApiException) {
//            return Response.status(Status.BAD_REQUEST)
//                    .entity(new ApiResponse(ApiResponse.ERROR, exception
//                                    .getMessage())).build();
//        } else {
//            return Response.status(Status.INTERNAL_SERVER_ERROR)
//                    .entity(new ApiResponse(ApiResponse.ERROR,
//                                    "a system error occured")).build();
//        }
    }

}
