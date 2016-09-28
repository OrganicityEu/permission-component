package eu.organicity;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.organicity.accounts.permissions.Accounts;
import eu.organicity.accounts.permissions.MySqlConfig;

@Path("/clients")
public class Clients extends Application{

	private static Logger log = LoggerFactory.getLogger(Users.class);

	private Accounts accounts;
	
	public Clients() {
		
		Clients.log.info("####################################################################");
		Clients.log.info("java.home: " + System.getProperty("java.home"));
		Clients.log.info("java.version: " + System.getProperty("java.version"));
		Clients.log.info("javax.net.ssl.trustStore: " + System.getProperty("javax.net.ssl.trustStore"));
		Clients.log.info("javax.net.ssl.trustStorePassword: " + System.getProperty("javax.net.ssl.trustStorePassword"));
		Clients.log.info("####################################################################");
		
		MySqlConfig mysqlConfig = new MySqlConfig(Config.connectionUrl, Config.connectionUser, Config.connectionPassword);
		accounts = Accounts.withBasicAuth(Config.basicAuth, mysqlConfig);
	}
	
	@POST
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/create")
	public Response postRole(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		@PathParam("userid") String userid,
		Client client
	) {

		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");
		if(!hasCreateClientRole(clientRoles)) {
			return Response.status(Status.FORBIDDEN).build();
		}		
		
		try {
			log.info("#### Create new Client ####");
			log.info("Client ID: " + clientid);
			log.info("Client Roles: " + clientRoles.toString());
			log.info("User ID: " + userid);
			log.info("New Client Name:" + client.getClientName());
			log.info("New Client URI:" + client.getClientUri());
			log.info("New Redirect URI:" + client.getRedirectUri());
			log.info("####################");

			JSONObject jsonObject = accounts.registerClient(client.getClientName(), client.getClientUri(), client.getRedirectUri());
			if(jsonObject != null) {
				return Response.status(201).entity(jsonObject.toString()).build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}		

	private boolean hasCreateClientRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.CREATE_CLIENT);
	}	
	
	
}
