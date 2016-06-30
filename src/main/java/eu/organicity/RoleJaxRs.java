package eu.organicity;

import java.util.HashMap;
import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/users")
public class RoleJaxRs extends Application {

	private HashMap<String, LinkedList<Role>> roles;
	
	public RoleJaxRs() {
		roles = new HashMap<String, LinkedList<Role>>();
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response getAllRoles(@PathParam("userid") String userid) {

		if(roles.containsKey(userid)) {
			return Response.status(Status.OK).entity(roles.get(userid)).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("NOT FOUND").build();
		}
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response postRole(@PathParam("userid") String userid, Role role) {
		
		if(!roles.containsKey(userid)) {
			roles.put(userid, new LinkedList<Role>());
		}
		
		if(!roles.get(userid).contains(role)) {
			roles.get(userid).add(role);
		}
		
		return Response.status(Status.CREATED).entity(role).build();
	}	

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles/{rolename}")
	public Response getRoleByName(@PathParam("userid") String userid, @PathParam("rolename") String rolename) {

		if(roles.containsKey(userid)) {
			if(roles.get(userid).contains(new RoleImpl(rolename))) {
				return Response.status(Status.OK).entity("OK").build();
			} 
		} 
		
		return Response.status(Status.NOT_FOUND).entity("NOT FOUND").build();
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles/{rolename}")
	public Response deleteRoleByName(@PathParam("userid") String userid, @PathParam("rolename") String rolename) {

		if(roles.containsKey(userid)) {
			Role tmpRole = new RoleImpl(rolename);
			if(roles.get(userid).contains(tmpRole)) {
				roles.get(userid).remove(tmpRole);
				return Response.status(Status.OK).entity("OK").build();
			} 
		} 
		
		return Response.status(Status.NOT_FOUND).entity("NOT FOUND").build();
	}
	
	
	
}