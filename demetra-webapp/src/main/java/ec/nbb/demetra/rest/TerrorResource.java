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
package ec.nbb.demetra.rest;

import ec.nbb.demetra.Messages;
import ec.nbb.demetra.model.terror.TerrorRequest;
import ec.nbb.demetra.model.terror.TerrorResult;
import ec.nbb.demetra.model.terror.TerrorResults;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.xml.information.XmlInformationSet;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.IPreprocessor;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * WebService providing access to Terror processing (CheckLast)
 * @author Mats Maggi
 */
@Path("/terror")
@Api(value = "/terror")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class TerrorResource {

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Terror processing", notes = "Creates a terror processing.", response = TerrorResults.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of terror", response = TerrorResults.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response terror(@ApiParam(name = "TerrorRequest", required = true) TerrorRequest request) {
        try {
            TsCollection tsCollection = request.getSeries().createTSCollection();
            TerrorResults results = new TerrorResults();
            int nbLast = request.getNbLast();

            if (nbLast <= 0) {
                throw new Exception(Messages.POSITIVE_NB_LAST);
            }

            if (tsCollection == null || tsCollection.getCount() == 0) {
                throw new Exception(Messages.NO_SERIES);
            }

            for (Ts ts : tsCollection) {
                IPreprocessor p = TramoSpecification.defaultPreprocessor(TramoSpecification.Default.valueOf(request.getSpecification()));
                CheckLast cl = new CheckLast(p);
                cl.setBackCount(nbLast);
                if (cl.check(ts.getTsData())) {
                    results.add(new TerrorResult(ts.getName(), cl.getValues(), cl.getForecastsValues(), cl.getScores()));
                }
            }
            return Response.status(Status.OK).entity(results).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).entity(String.format(Messages.UNKNOWN_SPEC, request.getSpecification())).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/info")
    @Consumes({MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML})
    @ApiOperation(value = "Terror processsing (XmlInformationSet)", notes = "Creates a terror processing. (Uses a XmlInformationSet as input and output)", response = XmlInformationSet.class)
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "Successful processing of terror", response = XmlInformationSet.class),
                @ApiResponse(code = 400, message = "Bad request", response = String.class),
                @ApiResponse(code = 500, message = "Invalid request", response = String.class)
            }
    )
    public Response terrorXml(@ApiParam(name = "TerrorRequest", required = true) XmlInformationSet request) {
        String spec = null;
        try {
            InformationSet set = request.create();
            TsCollectionInformation tsCollInfo = set.get("series", TsCollectionInformation.class);
            TerrorResults results = new TerrorResults();
            int nbLast = set.get("nbLast", Integer.class);
            spec = set.get("specification", String.class);

            if (nbLast <= 0) {
                throw new Exception(Messages.POSITIVE_NB_LAST);
            }

            if (tsCollInfo == null) {
                throw new Exception(Messages.NO_SERIES);
            }
            InformationSet result = new InformationSet();
            
            for (TsInformation tsInfo : tsCollInfo.items) {
                IPreprocessor p = TramoSpecification.defaultPreprocessor(TramoSpecification.Default.valueOf(spec));
                CheckLast cl = new CheckLast(p);
                cl.setBackCount(nbLast);
                if (cl.check(tsInfo.data)) {
                    results.add(new TerrorResult(tsInfo.name, cl.getValues(), cl.getForecastsValues(), cl.getScores()));
                    InformationSet subSet = result.subSet(tsInfo.name);
                    subSet.add("value", cl.getValues());
                    subSet.add("score", cl.getScores());
                    subSet.add("forecast", cl.getForecastsValues());
                }
            }
            XmlInformationSet xmlSet = new XmlInformationSet();
            xmlSet.copy(result);
            return Response.status(Status.OK).entity(xmlSet).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).entity(String.format(Messages.UNKNOWN_SPEC, spec)).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
