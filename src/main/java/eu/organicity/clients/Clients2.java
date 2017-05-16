package eu.organicity.clients;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.InternalServerErrorException;
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

import eu.organicity.AccessRoles;
import eu.organicity.Config;
import eu.organicity.Secured;
import eu.organicity.Utils;
import eu.organicity.accounts.permissions.Accounts;
import eu.organicity.accounts.permissions.MySqlConfig;
import eu.organicity.users.Users;

@Path("/clients2")
public class Clients2 extends Application{

	private static Logger log = LoggerFactory.getLogger(Users.class);

	private Accounts accounts;
	
	public Clients2() {
		//Clients2.log.info("####################################################################");
		//Clients2.log.info("java.home: " + System.getProperty("java.home"));
		//Clients2.log.info("java.version: " + System.getProperty("java.version"));
		//Clients2.log.info("javax.net.ssl.trustStore: " + System.getProperty("javax.net.ssl.trustStore"));
		//Clients2.log.info("javax.net.ssl.trustStorePassword: " + System.getProperty("javax.net.ssl.trustStorePassword"));
		//Clients2.log.info("####################################################################");
		
		MySqlConfig mysqlConfig = new MySqlConfig(Config.connectionUrl, Config.connectionUser, Config.connectionPassword);
		accounts = Accounts.withBasicAuth(Config.basicAuth, mysqlConfig);
	}
	
	@POST
	@Secured({AccessRoles.CREATE_CLIENT, AccessRoles.READ_CLIENT_SECRET})
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response postClient(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Client-Roles") String roles,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		Client2 client
	) {
		log.info("#### Create new Client ####");
		log.info("Client ID: " + clientid);
		log.info("New Client Name:" + client.getClientId());
		log.info("New Client Roles:" + client.getRoles().toString());
		log.info("###########################");
		
		List<AccessRoles> clientRoles = Utils.convertRolesStringToAccessRoles(roles);
		
		JSONObject jsonObject = accounts.registerClient(client.getClientId(), client.getRoles(), clientRoles.contains(AccessRoles.READ_CLIENT_SECRET));

		// this will be a JSON object, or an exception is thrown internally and already handled by JAX-RS!
		String clientName = jsonObject.get("client_id").toString();
		// Remove the global scope
		accounts.setFullScope(clientName, false);
		
		// Set experimenter and participant scope
		// This is neede, if the experimenters create applications to authenticate experimenters or participants
		// Thus, they can see this assigned roles
		accounts.setClientScopeRole(clientName, "experimenter");
		accounts.setClientScopeRole(clientName, "participant");
		return Response.status(201).entity(jsonObject.toString()).build();
	}

	/**
	 * file:///home/boldt/Schreibtisch/KeyCloak/Keycloak%20Admin%20REST%20API.html#_create_a_new_client
	 * 
	 * @param clientid
	 * @param sub
	 * @param uriInfo
	 * @param clientName
	 * @return
	 */
	@GET
	@Secured({AccessRoles.READ_CLIENT, AccessRoles.READ_CLIENT_SECRET})
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{clientName}")
	public Response getClientById(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Client-Roles") String roles,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		@PathParam("clientName") String clientName
	) {
		log.info("#### Get redirect URIs for a given client ####");
		log.info("Client ID: " +clientid);
		log.info("Client Name: " + clientName);
		log.info("##############################################");
		
		List<AccessRoles> clientRoles = Utils.convertRolesStringToAccessRoles(roles);
		
		JSONObject client = accounts.getClient(clientName, clientRoles.contains(AccessRoles.READ_CLIENT_SECRET));
		return Response.status(200).entity(client.toString()).build();
	}
	
	@DELETE
	@Secured({AccessRoles.DELETE_CLIENT})
	@Path("/{clientName}")
	public Response deleteClientById(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		@PathParam("clientName") String clientName
	) {
		log.info("#### Delete given client id ####");
		log.info("Client ID: " +clientid);
		log.info("Client Name: " + clientName);
		log.info("##############################################");
		
		boolean isClientDeleted = accounts.deleteClient(clientName);
		if (isClientDeleted) {
			return Response.status(Status.NO_CONTENT).entity("").build();
		} 
		
		throw new InternalServerErrorException("Client with ID " + clientName + " not deleted!");
	}
	
}
