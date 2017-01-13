package eu.organicity;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
	@Path("/")
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
			log.info("###########################");

			JSONObject jsonObject = accounts.registerClient(client.getClientName(), client.getClientUri(), client.getRedirectUri());
			if(jsonObject != null) {
				String clientName = jsonObject.get("client_id").toString();
				// Remove the global scope
				accounts.setFullScope(clientName, false);
				// Set experimenter and participant role
				accounts.setClientScopeRole(clientName, "experimenter");
				accounts.setClientScopeRole(clientName, "participant");
				return Response.status(201).entity(jsonObject.toString()).build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

	/*
	 * Like this, it is not REST conform
	 */
	@POST
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/create")
	@Deprecated
	public Response postRoleDepricated(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		@PathParam("userid") String userid,
		Client client
	) {
		return postRole(clientid, sub, uriInfo, userid, client);
	}

	@GET
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{clientName}/redirecturis")
	public Response getRedirectUris(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		@PathParam("clientName") String clientName
	) {
		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");
		if(!hasReadClientRedirectUriRole(clientRoles)) {
			return Response.status(Status.FORBIDDEN).build();
		}

		try {
			log.info("#### Get redirect URIs for a given client ####");
			log.info("Client ID: " + clientid);
			log.info("Client Roles: " + clientRoles.toString());
			log.info("Client Name: " + clientName);
			log.info("##############################################");

			if(clientName == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("clientName not detected!").build();
			}

			List<String> uris = accounts.getRedirectUris(clientName);

			RedirectUri redirectUris = new RedirectUriImpl();
			redirectUris.setRedirectUris(uris);
			return Response.status(201).entity(redirectUris).build();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

	@PUT
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{clientName}/redirecturis")
	public Response putRedirectUris(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		@PathParam("clientName") String clientName,
		RedirectUri redireturis
	) {
		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");
		if(!hasUpdateClientRedirectUriRole(clientRoles)) {
			return Response.status(Status.FORBIDDEN).build();
		}

		try {
			log.info("#### Update redirect URIs for a given client ####");
			log.info("Client ID: " + clientid);
			log.info("Client Roles: " + clientRoles.toString());
			log.info("Client Name: " + clientName);
			log.info("##############################################");

			if(clientName != null) {

				// Get the existing roles from the keycloak
				ArrayList<String> urisA = new ArrayList<>();
				List<String> inKeycloak = accounts.getRedirectUris(clientName);
				for (String uri : inKeycloak) {
					urisA.add(uri);
				}
				log.info("Existing URIs:" + urisA.toString());

				// Get the roles provided by the user
				ArrayList<String> urisB = new ArrayList<>();
				List<String> providedByUser = redireturis.getRedirectUris();
				for (String uri : providedByUser) {
					urisB.add(uri);
				}
				log.info("New URIs: " + urisB.toString());

				// E.g., [a,b,c,d]
				@SuppressWarnings("unchecked")
				ArrayList<String> urisA2 = (ArrayList<String>) urisA.clone();
				// E.g., [b,c,e]
				@SuppressWarnings("unchecked")
				ArrayList<String> urisB2 = (ArrayList<String>) urisB.clone();

				// E.g., [a,d]
				urisA2.removeAll(urisB2);
				log.info("URIs to be removed:" + urisA2.toString());

				// urisA2 contains all URIs to be removed
				for (String uri : urisA2) {
					log.info("Remove URI " + uri);
					accounts.removeRedirectUri(clientName, uri);
				}

				// E.g., [a,b,c,d]
				@SuppressWarnings("unchecked")
				ArrayList<String> urisA3 = (ArrayList<String>) urisA.clone();
				// E.g., [b,c,e]
				@SuppressWarnings("unchecked")
				ArrayList<String> urisB3 = (ArrayList<String>) urisB.clone();

				// E.g., [e]
				urisB3.removeAll(urisA3);
				log.info("URIs to be added:" + urisB3.toString());

				// urisB3 contains all URIs to be added
				for (String uri : urisB3) {
					log.info("Add URI " + uri);
					accounts.addRedirectUri(clientName, uri);
				}

				return Response.status(200).entity("URIs updated").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}	

	private boolean hasCreateClientRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.CREATE_CLIENT);
	}	

	private boolean hasReadClientRedirectUriRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.EDIT_CLIENT_REDIRECTURIS);
	}

	private boolean hasUpdateClientRedirectUriRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.EDIT_CLIENT_REDIRECTURIS);
	}

	
	
}
