package edu.asupoly.ser422.lab3.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.asupoly.ser422.lab3.model.PhoneEntry;
import edu.asupoly.ser422.lab3.services.PhoneBookService;
import edu.asupoly.ser422.lab3.services.PhoneBookServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

// Only doing JSON
@Path("/phones")
@Produces({MediaType.APPLICATION_JSON})
public class PhoneEntryResource {
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
    @Path("{number}")
    public Response getPhoneEntry(@PathParam("number") String pid) {
        // This isn't correct - what if the authorId is not for an active author?
        // let's use Jackson instead. ObjectMapper will build a JSON string and we use
        // the ResponseBuilder to use that. Note the result looks the same
        try {
            // bad info 406 error, originally
            if(pid == null || pid.equals("")) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" 406 Invalid Input: Include a phone number. \" }").build();
            }

            // search for our stuff
            PhoneEntry phoneEntry = __bService.getPhoneEntry(pid);
            String aString = new ObjectMapper().writeValueAsString(phoneEntry);

            // not found error
            if(aString == null || aString.equals("") || aString.equals("null")) return Response.status(Response.Status.NOT_FOUND).entity("{ \" 404 Resource Not Found. \" }").build();

            // ok all good
            return Response.status(Response.Status.OK).entity(aString).build();
        } catch (Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" Cannot find anything. \" }").build();
        }
    }

    /**
     * must have 3 or 4 values
     * @param name
     * @return
     */
    @POST
    @Consumes("text/plain")
    public Response createPhoneEntry(String name) {

        try {
            String[] values = name.split(" ");
            if (values.length == 3 || values.length == 4) {
                int aid = -1;
                String bookID = null;

                // null or not
                if (values.length == 4) {

                    // try it out
                    try {
                        Integer.parseInt(values[0]);

                    } catch (NumberFormatException exc) {
                        exc.printStackTrace();
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" Invalid Format. First value must be phone number. \"}").build();
                    }

                    // check if it exists or not
                    PhoneEntry test = __bService.getPhoneEntry(values[0]);

                    if(test != null) {
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" Resource already exists. \" }").build();
                    }

                    bookID = values[3];
                    aid = __bService.createPhoneEntry(values[0], values[1], values[2], bookID);
                } else {
                    // check if it exists or not
                    PhoneEntry test = __bService.getPhoneEntry(values[0]);

                    if(test != null) {
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" Resource already exists. \" }").build();
                    }

                    aid = __bService.createPhoneEntry(values[0], values[1], values[2], bookID);
                }

                if (aid == -1) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" EXCEPTION INSERTING INTO DATABASE! \" }").build();
                } else if (aid == 0) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" ERROR INSERTING INTO DATABASE! \" }").build();
                }
                return Response.status(Response.Status.CREATED)
                        .header("Location", String.format("%s/%s", _uriInfo.getAbsolutePath().toString(), aid))
                        .entity("{ \"phoneNumber\" : \"" + values[0] + "\"\n,\"phoneBookID\" : \"" + bookID + "\"}").build();
            }

            // may have only 3 or 4
            else {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("406 Invalid Input: Separate input by spaces. Must have 3 or 4 values only.").build();
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" Unexpected Error. Please try again or report. \" }").build();
        }
    }
    /**
     * must have 3 or 4 values
     * @param name
     * @return
     */
    @PUT
    @Consumes("text/plain")
    public Response updatePhoneEntryToPhoneBook(String name) {
        try {
            String[] values = name.split(" ");

            // only 2 length is allowed
            if (values.length == 2) {


                // make sure they're both ints
                try {
                    Integer.parseInt(values[0]);
                    Integer.parseInt(values[1]);

                } catch (NumberFormatException exc) {
                    exc.printStackTrace();
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" Invalid Format. Values must be integer IDs of phone number and phonebook ID to assign to. \"}").build();
                }

                // check if they exist
                PhoneEntry phoneEntry = __bService.getPhoneEntry(values[0]);

                if (phoneEntry == null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("{ \" Phone number does not exist. \" }").build();
                }

                List<PhoneEntry> phoneBook = __bService.getAllEntriesFromPhoneBook(values[1]);
                if (phoneBook == null || phoneBook.size() <= 0) {
                    return Response.status(Response.Status.NOT_FOUND).entity("{ \" Phone book does not exist. \" }").build();
                }

                // check if phone entry already has a phone book
                if (phoneEntry.getPhoneBookID() != null) {
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" Phone number is already associated with a phone book. \" }").build();
                }

                // finally change it
                boolean success = __bService.updatePhoneBookToPhoneEntry(values[1], values[0]);

                if (success) {
                    return Response.status(Response.Status.CREATED)
                            .header("Location", String.format("%s/%s", _uriInfo.getAbsolutePath().toString(), values[0]))
                            .entity("{ \"phoneNumber\" : \"" + values[0] + "\"\n,\"phoneBookID\" : \"" + values[1] + "\"}").build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" Unexpected Error. Please try again or report. \" }").build();
                }
            }
            else if (values.length == 3) {
                try {
                    Integer.parseInt(values[0]);

                } catch (NumberFormatException exc) {
                    exc.printStackTrace();
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" Invalid Format. The first value must be an integer ID representing the phone number to change. \"}").build();
                }

                // check if they exist
                PhoneEntry phoneEntry = __bService.getPhoneEntry(values[0]);

                if (phoneEntry == null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("{ \" Phone number does not exist. \" }").build();
                }

                // update values
                phoneEntry.setFirstName(values[1]);
                phoneEntry.setLastName(values[2]);

                // finally change it
                boolean success = __bService.updatePhoneEntryNames(phoneEntry);

                if (success) {
                    return Response.status(Response.Status.CREATED)
                            .header("Location", String.format("%s/%s", _uriInfo.getAbsolutePath().toString(), values[0]))
                            .entity("{ \"phoneNumber\" : \"" + values[0] + "\"}").build();
                }
                else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" Unexpected Error. Please try again or report. \" }").build();
                }
            }
            else {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(" { \" 406 Invalid Input: Separate input by spaces. Must have 2 or 3 values only. \" } ").build();
            }
        }

        catch(Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" Unexpected Error. Please try again or report. \" }").build();
        }
    }

    @DELETE
    @Consumes("text/plain")
    @Path("{number}")
    public Response deletePhoneEntry(@PathParam("number") String name) {
        try {
            String[] values = name.split(" ");

            // first value must be an int
            try {
                Integer.parseInt(values[0]);

            } catch (NumberFormatException exc) {
                exc.printStackTrace();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{ \" Invalid Format. The first value must be an integer ID representing the phone number to change. \"}").build();
            }

            // test if it successfully deleted
            if (__bService.deletePhoneEntry(values[0])) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("{ \"message \" : \"No such phone " + values[0] + "\"}").build();
            }


        }
        catch(Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \" Unexpected Error. Please try again or report. \" }").build();
        }
    }

    @PATCH
    public Response patchEntry (String number) {
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity("{ \"message \" : \"PATCH not supported\"}").build();
    }
}
