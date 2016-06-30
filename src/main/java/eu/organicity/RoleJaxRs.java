package eu.organicity;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;

@Path("/users")
public class RoleJaxRs extends Application {

	private HashMap<String, LinkedList<String>> roles;
	
	public RoleJaxRs() {
		roles = new HashMap<String, LinkedList<String>>();
	}
	
	@GET
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response getAllRoles(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid) {

		System.out.println("Client ID: " + clientid);
		System.out.println("User ID: " + userid);

		//Accounts a = new Accounts();
		//a.login("TOKEN");
		//System.out.println("Login Successful");
		//a.setUserRole(userId, role);

		if(roles.containsKey(userid)) {
			return Response.status(Status.OK).entity(roles.get(userid)).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("NOT FOUND").build();
		}
	}
	
	@POST
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response postRole(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid, InputStream inputStream) {

		try {
			// @see: https://stackoverflow.com/questions/38125756/consume-json-string-with-jax-rs
			ObjectMapper mapper = new ObjectMapper();
			String rolename = mapper.readValue(inputStream, String.class);
			System.out.println("Role:" + rolename);

			System.out.println("Client ID: " + clientid);
			System.out.println("User ID: " + userid);

			if(!roles.containsKey(userid)) {
				roles.put(userid, new LinkedList<String>());
			}

			if(!roles.get(userid).contains(rolename)) {
				roles.get(userid).add(rolename);
			}

			return Response.status(Status.CREATED).entity(rolename).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).entity("BAD REQUEST").build();
		}
	}	

	@GET
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles/{rolename}")
	public Response getRoleByName(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid, @PathParam("rolename") String rolename) {

		System.out.println("Client ID: " + clientid);
		System.out.println("User ID: " + userid);
		System.out.println("Role:" + rolename);

		if(roles.containsKey(userid)) {
			if(roles.get(userid).contains(rolename)) {
				return Response.status(Status.OK).entity("OK").build();
			} 
		} 
		
		return Response.status(Status.NOT_FOUND).entity("NOT FOUND").build();
	}

	@DELETE
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles/{rolename}")
	public Response deleteRoleByName(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid, @PathParam("rolename") String rolename) {

		System.out.println("Client ID: " + clientid);
		System.out.println("User ID: " + userid);
		System.out.println("Role:" + rolename);

		if(roles.containsKey(userid)) {
			if(roles.get(userid).contains(rolename)) {
				roles.get(userid).remove(rolename);
				return Response.status(Status.OK).entity("OK").build();
			} 
		} 
		
		return Response.status(Status.NOT_FOUND).entity("NOT FOUND").build();
	}
	
}