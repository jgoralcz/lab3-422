package edu.asupoly.ser422.lab3.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.asupoly.ser422.lab3.model.PhoneBook;
import edu.asupoly.ser422.lab3.services.PhoneBookService;
import edu.asupoly.ser422.lab3.services.PhoneBookServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

// Only doing JSON
@Path("/phonebooks")
@Produces({MediaType.APPLICATION_JSON})
public class PhoneBookResource {
    private static PhoneBookService __bService = PhoneBookServiceFactory.getInstance();

    // Technique for location header taken from
    // http://usna86-techbits.blogspot.com/2013/02/how-to-return-location-header-from.html
    @Context
    private UriInfo _uriInfo;

    /**
     * @apiDefine BadRequestError
     * @apiError (Error 4xx) {400} BadRequest Bad Request Encountered
     * */
    /** @apiDefine ActivityNotFoundError
     * @apiError (Error 4xx) {404} NotFound Activity cannot be found
     * */
    /**
     * @apiDefine InternalServerError
     * @apiError (Error 5xx) {500} InternalServerError Something went wrong at server, Please contact the administrator!
     * */
    /**
     * @apiDefine NotImplementedError
     * @apiError (Error 5xx) {501} NotImplemented The resource has not been implemented. Please keep patience, our developers are working hard on it!!
     * */

    /**
     * @api {get} /authors Get list of Authors
     * @apiName getAuthors
     * @apiGroup Authors
     *
     * @apiUse BadRequestError
     * @apiUse InternalServerError
     *
     * @apiSuccessExample Success-Response:
     * 	HTTP/1.1 200 OK
     * 	[
     *   {"authorId":1111,"firstName":"Ariel","lastName":"Denham"},
     *   {"authorId":1212,"firstName":"John","lastName":"Worsley"}
     *  ]
     *
     * */


    @GET
    @Path("{id}")
    public Response getPhoneBookEntries(@PathParam("id") String pid, @QueryParam("fname") String fname, @QueryParam("lname") String lname) {
        try {

            if( (pid == null || pid.equals("") || pid.equals("null")) && ((fname == null || fname.equals("")) && (lname == null || lname.equals(""))) ) {
                // show unlisted
                PhoneBook pbook = __bService.getUnlistedPhoneEntries();

                // get unlisted errors
                String aString = new ObjectMapper().writeValueAsString(pbook.getBook());

                // not found error
                if(aString == null || aString.equals("") || aString.equals("null")) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" 404 Resource Not Found. \" }").build();
                }

                // ok all good
                return Response.status(Response.Status.OK).entity(aString).build();
            }

            // they gave us an id
            else if(pid != null && !pid.equals("") && !pid.equals("null")) {

                // make sure we have an id
                try {
                    Integer.parseInt(pid);

                } catch (Exception exc) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("{ \" 406 Invalid Input: Must have integer ID. \" }").build();
                }
                PhoneBook pbook;
                // check if we have a first name or last name
                if(fname != null && !fname.equals("") || lname != null && !lname.equals("")) {
                    pbook = __bService.getSubStringPhoneBookPhoneEntries(pid, fname, lname);
                }
                else {
                    pbook = __bService.getAllEntriesFromPhoneBook(pid);
                }

                // get info
                String aString = new ObjectMapper().writeValueAsString(pbook.getBook());

                // not found error
                if(aString == null || aString.equals("") || aString.equals("null")) {
                    return Response.status(Response.Status.NOT_FOUND).entity("{ \" 404 Resource Not Found. \" }").build();
                }

                // ok all good
                return Response.status(Response.Status.OK).entity(aString).build();
            }

            // not valid data
            else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{ \" 406 Invalid Input: Must have integer ID, or first name, or last name to query. \" }").build();
            }

        } catch (Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" INTERNAL ERROR: Cannot find anything. \" }").build();
        }
    }
}
