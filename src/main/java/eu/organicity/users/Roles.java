package eu.organicity.users;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.organicity.AccessRoles;
import eu.organicity.Config;
import eu.organicity.Secured;
import eu.organicity.accounts.permissions.Accounts;
import eu.organicity.accounts.permissions.MySqlConfig;

/**
 * This is the implementation of the "Organicity Accounts - Permission Component", version `0.1.3-development`, 
 * which is described here:
 * 
 * https://organicityeu.github.io/api/Permissions.html#/definitions/Role
 * 
 * @author Dennis Boldt
 *
 */
@Path("/roles")
public class Roles extends Application {

	private static Logger log = LoggerFactory.getLogger(Roles.class);

	private Accounts accounts;
	
	public Roles() {
		MySqlConfig mysqlConfig = new MySqlConfig(Config.connectionUrl, Config.connectionUser, Config.connectionPassword);
		this.accounts = Accounts.withBasicAuth(Config.basicAuth, mysqlConfig);
	}

	@GET
	public Response getallUsers() {
		return Response.ok().status(Status.FORBIDDEN).entity("ROOT").build();
	}
	
	@GET
	@Secured({AccessRoles.GET_SUBS_BY_ROLE})
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{role}")
	public Response getSubsByRole(
		@PathParam("role") String role
	) {
		log.info("#### Get Users by role ####");
		log.info("Role: " + role);
		log.info("###########################");

		if(role != null) {
			return Response.ok(this.accounts.getSubsPerRole(role)).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
}