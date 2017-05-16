package eu.organicity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

/**
 * This is just a simple status page which returns 200 OK
 * 
 * @author Dennis Boldt
 *
 */

@Path("/status")
public class Status extends Application {

	@GET
	public Response status() {
		return Response.status(javax.ws.rs.core.Response.Status.OK).entity("Service is running.").build();
	}


}