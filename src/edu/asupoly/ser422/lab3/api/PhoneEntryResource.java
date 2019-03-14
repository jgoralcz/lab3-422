package edu.asupoly.ser422.lab3.api;

import edu.asupoly.ser422.lab3.model.PhoneBook;
import edu.asupoly.ser422.lab3.model.PhoneEntry;
import edu.asupoly.ser422.lab3.services.PhoneBookService;
import edu.asupoly.ser422.lab3.services.PhoneBookServiceFactory;


import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

//import javax.ws.rs.PATCH;

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
     */
    /** @apiDefine ActivityNotFoundError
     * @apiError (Error 4xx) {404} NotFound Activity cannot be found
     */
    /**
     * @apiDefine InternalServerError
     * @apiError (Error 5xx) {500} InternalServerError Something went wrong at server, Please contact the administrator!
     */
    /**
     * @apiDefine NotImplementedError
     * @apiError (Error 5xx) {501} NotImplemented The resource has not been implemented. Please keep patience, our developers are working hard on it!!
     */

    /**
     * @api {get} phones/:number Request phone entry
     * @apiName getPhoneEntries
     * @apiGroup phones
     *
     * @apiParam {number} phonenumber the unique phone number.
     *
     * @apiError (Error 4xx) {400} BadRequest The <code>phonenumber</code> was not found or is not an integer.
     * @apiError (Error 4xx) {404} ResourceNotFound The phone entry could not be found.
     * @apiError (Error 5xx) {500} InternalServerError An unexpected error occurred.
     * @apiSuccessExample Success-Response:
     *  HTTP/1.1 200 OK
     *  { "firstName": "Jimmy", "lastName": "V", "phoneNumber": "480110987", "phoneBookID": null}
     */
    @GET
    @Path("{number}")
    public Response getPhoneEntriesFromPhoneBook(@PathParam("number") String pid) {
        try {
            // bad info 406 error, originally
            if(pid == null || pid.equals("")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"400 Invalid Input: Must include phone number.\" }").build();
            }

            try {
                Integer.parseInt(pid);
            }
            catch(Exception exc) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"400 Invalid Input: Must have integer phone number.\" }").build();
            }

            // search for our stuff
            PhoneEntry pe = __bService.getPhoneEntry(pid);
            String aString = new ObjectMapper().writeValueAsString(pe);

            // not found error
            if(aString == null || aString.equals("") || aString.equals("null")) {
                return Response.status(Response.Status.NOT_FOUND).entity("{ \"error\": \"404 Resource Not Found.\" }").build();
            }

            // ok all good
            return Response.status(Response.Status.OK).entity(aString).build();

        } catch (Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\": \"Cannot find anything.\" }").build();
        }
    }

    /**
     * @api {post} phones/:name Add phone entry
     * @apiName createPhoneEntry
     * @apiGroup phones
     *
     * @apiParam {name} name sent in the body separated by spaces.
     * @apiDescription Accepts a body of three or four elements separated by spaces.
     * In the order of <code>phoneNumber</code>, <code>firstName</code>, <code>lastName</code>, <code>phoneBookID</code>
     * @apiExample Example body Usage:
     * 480110987 Jimmy V 1
     *
     *
     * @apiError (Error 4xx) {400} BadRequest The <code>phonenumber</code> was not found, is not an integer, or already exists.
     * @apiError (Error 5xx) {500} InternalServerError An unexpected error occurred or database could not insert the request.
     * @apiSuccessExample Success-Response:
     *  HTTP/1.1 201 OK
     *  Location: http://localhost:8081/lab3_jgoralcz/rest/phones/481283
     *  {
     *     "phoneNumber": "481283",
     *     "phoneBookID": "1"
     *  }
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
                        return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Invalid Format. First value must be phone number.\"}").build();
                    }

                    // check if it exists or not
                    PhoneEntry test = __bService.getPhoneEntry(values[0]);

                    if(test != null) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Resource already exists.\" }").build();
                    }

                    bookID = values[3];
                    aid = __bService.createPhoneEntry(values[0], values[1], values[2], bookID);
                } else {
                    // check if it exists or not
                    PhoneEntry test = __bService.getPhoneEntry(values[0]);

                    if(test != null) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Resource already exists.\" }").build();
                    }

                    aid = __bService.createPhoneEntry(values[0], values[1], values[2], bookID);
                }

                if (aid == -1) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\": \"EXCEPTION INSERTING INTO DATABASE!\"}").build();
                } else if (aid == 0) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\": \"ERROR INSERTING INTO DATABASE!\"}").build();
                }
                return Response.status(Response.Status.CREATED)
                        .header("Location", String.format("%s/%s", _uriInfo.getAbsolutePath().toString(), aid))
                        .entity("{ \"phoneNumber\" : \"" + values[0] + "\",\"phoneBookID\" : \"" + bookID + "\"}").build();
            }

            // may have only 3 or 4
            else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Separate input by spaces. Must have 3 or 4 values only.\"}").build();
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"Unexpected Error. Please try again or report.\" }").build();
        }
    }

    /**
     * @api {put} phones/:name Set the names of a particular phoneNumber (3) or assign a phonebookID to a phone number (2)
     * @apiName updatePhoneEntryToPhoneBook
     * @apiGroup phones
     *
     * @apiParam {name} name sent in the body separated by spaces.
     * @apiDescription Accepts a body of two or three elements separated by spaces.
     * In the order of <code>phoneNumber</code> <code>phoneBookID</code> OR <code>phoneNumber</code> <code>firstName</code> <code>lastName</code>
     * @apiExample Example body Usage with three elements, updating the names:
     * 480110987 Jimmy Vam
     * @apiExample Example body Usage with two elements, updating the phoneNumber on phoneBookID:
     * 480110987 1
     *
     * @apiError (Error 4xx) {400} BadRequest The <code>phonenumber</code> is not an integer, or already exists.
     * @apiError (Error 4xx) {404} NotFound The <code>phonenumber</code> could not be found or the <code>phonebook</code> does not exist.
     * @apiError (Error 5xx) {500} InternalServerError An unexpected error occurred or database could not insert the request.
     * @apiSuccessExample Success-Response:
     *  HTTP/1.1 201 OK
     *  Location: http://localhost:8081/lab3_jgoralcz/rest/phones/481285
     *  { "phoneNumber": "481285" }
     * @apiErrorExample Error-Response:
     *  HTTP/1.1 400 Bad Request
     *  Location: http://localhost:8081/lab3_jgoralcz/rest/phones/481285
     *  { "error": "Phone number is already associated with a phone book." }
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
                    return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Invalid Format. Values must be integer IDs of phone number and phonebook ID to assign to.\"}").build();
                }

                // check if they exist
                PhoneEntry phoneEntry = __bService.getPhoneEntry(values[0]);

                if (phoneEntry == null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("{ \"error\": \"Phone number does not exist.\" }").build();
                }

                PhoneBook phoneBook = __bService.getAllEntriesFromPhoneBook(values[1]);
                if (phoneBook == null || phoneBook.size() <= 0) {
                    return Response.status(Response.Status.NOT_FOUND).entity("{ \"error\": \"Phone book does not exist.\" }").build();
                }

                // check if phone entry already has a phone book
                if (phoneEntry.getPhoneBookID() != null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Phone number is already associated with a phone book.\" }").build();
                }

                // finally change it
                boolean success = __bService.updatePhoneBookToPhoneEntry(values[1], values[0]);

                if (success) {
                    return Response.status(Response.Status.CREATED)
                            .header("Location", String.format("%s/%s", _uriInfo.getAbsolutePath().toString(), values[0]))
                            .entity("{ \"phoneNumber\" : \"" + values[0] + "\",\"phoneBookID\" : \"" + values[1] + "\"}").build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\": \"Unexpected Error. Please try again or report.\" }").build();
                }
            }
            else if (values.length == 3) {
                try {
                    Integer.parseInt(values[0]);

                } catch (NumberFormatException exc) {
                    exc.printStackTrace();
                    return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Invalid Format. The first value must be an integer ID representing the phone number to change.\"}").build();
                }

                // check if they exist
                PhoneEntry phoneEntry = __bService.getPhoneEntry(values[0]);

                if (phoneEntry == null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("{ \"error\": \"Phone number does not exist.\" }").build();
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
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\": \"Unexpected Error. Please try again or report.\" }").build();
                }
            }
            else {
                return Response.status(Response.Status.BAD_REQUEST).entity(" { \"error\": \" Separate input by spaces. Must have 2 or 3 values only.\" } ").build();
            }
        }

        catch(Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\": \"Unexpected Error. Please try again or report.\" }").build();
        }
    }

    /**
     * @api {delete} phones/:number Delete a Phone Entry
     * @apiName deletePhoneEntry
     * @apiGroup phones
     *
     * @apiParam {number} phonenumber the unique phone number.
     *
     * @apiError (Error 4xx) {400} BadRequest The <code>phonenumber</code> was not found, is not an integer, or already exists.
     * @apiError (Error 5xx) {500} InternalServerError Unexpected error deleting. Try again or report to administrators.
     * @apiSuccessExample Success-Response:
     *  HTTP/1.1 204 NO CONTENT
     *  @apiSuccessExample Error-Response:
     *  HTTP/1.1 500 Internal Server Error
     */
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
                return Response.status(Response.Status.BAD_REQUEST).entity("{ \"error\": \"Invalid Format. The first value must be an integer ID representing the phone number to change. \"}").build();
            }

            // test if it successfully deleted
            if (__bService.deletePhoneEntry(values[0])) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("{ \"error\": \"No such phone " + values[0] + "\"}").build();
            }


        }
        catch(Exception exc) {
            exc.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{ \"error\": \"Unexpected Error. Please try again or report. \" }").build();
        }
    }

    /**
     * @api {patch} phones/:number Patch Not Supported
     * @apiName patchEntry
     * @apiGroup phones
     * @apiDescription NOT SUPPORTED
     *
     * @apiError (Error 4xx) {405} MethodNotAllowed Cannot use PATCH.
     * @apiErrorExample Error-Response:
     *  HTTP/1.1 405 OK
     *  { "error": "PATCH not supported"}
     */
    @PATCH
    public Response patchEntry () {
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity("{ \"error\" : \"PATCH not supported\"}").build();
    }
}
