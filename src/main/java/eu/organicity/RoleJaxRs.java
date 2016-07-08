package eu.organicity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

import eu.organicity.accounts.permissions.Accounts;

@Path("/users")
public class RoleJaxRs extends Application {

	private Accounts a = new Accounts();

	public RoleJaxRs() {
		a.login(Config.basicAuth);
	}
	
	@GET
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response getAllRoles(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid) {

		System.out.println("#### Get Roles ####");
		System.out.println("Client ID: " + clientid);
		System.out.println("User ID: " + userid);
		System.out.println("###################");

		try {
			List<String> roles = a.getUserRoles(userid, clientid);
			if(roles != null) {
				return Response.status(Status.OK).entity(roles).build();
			}
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}			
	}
	
	@POST
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response postRole(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid, InputStream inputStream) {

		String rolename = null;
		
		try {
			// @see: https://stackoverflow.com/questions/38125756/consume-json-string-with-jax-rs
			ObjectMapper mapper = new ObjectMapper();
			rolename = mapper.readValue(inputStream, String.class);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}

		try {
			System.out.println("#### Post Roles ####");
			System.out.println("Role:" + rolename);
			System.out.println("Client ID: " + clientid);
			System.out.println("User ID: " + userid);
			System.out.println("####################");

			Boolean success = a.setUserRole(userid, rolename);

			if(success) {
				return Response.status(Status.CREATED).build();
			}
			
			return Response.status(Status.NOT_FOUND).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}	

	@DELETE
	@Secured
	@Path("/{userid}/roles/{rolename}")
	public Response deleteRoleByName(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid, @PathParam("rolename") String rolename) {

		System.out.println("#### Delete Role ####");
		System.out.println("Client ID: " + clientid);
		System.out.println("User ID: " + userid);
		System.out.println("Role:" + rolename);
		System.out.println("#####################");

		try {
			Boolean success = a.removeUserRole(userid, rolename);
			
			if(success) {
				return Response.status(Status.OK).build();
			}
			
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Secured
	@Path("/{userid}/roles/{rolename}")
	public Response getRoleByName(@HeaderParam("X-ClientID") String clientid, @PathParam("userid") String userid, @PathParam("rolename") String rolename) {

		System.out.println("#### Get Role ####");
		System.out.println("Client ID: " + clientid);
		System.out.println("User ID: " + userid);
		System.out.println("Role:" + rolename);
		System.out.println("##################");

		try {
			List<String> roles = a.getUserRoles(userid, clientid);
			
			if(roles != null && roles.contains(rolename)) {
				return Response.status(Status.OK).build();
			}
			
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}	
}