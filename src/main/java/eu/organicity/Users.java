package eu.organicity;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.organicity.accounts.permissions.Accounts;
import eu.organicity.accounts.permissions.MySqlConfig;
import eu.organicity.accounts.permissions.UserIdentifier;

/**
 * This is the implementation of the "Organicity Accounts - Permission Component", version `0.1.3-development`, 
 * which is described here:
 * 
 * https://organicityeu.github.io/api/Permissions.html#/definitions/Role
 * 
 * @author Dennis Boldt
 *
 */
@Path("/users")
public class Users extends Application {

	private static Logger log = LoggerFactory.getLogger(Users.class);

	private Accounts accounts;
	
	public Users() {
		
		Users.log.info("####################################################################");
		Users.log.info("java.home: " + System.getProperty("java.home"));
		Users.log.info("java.version: " + System.getProperty("java.version"));
		Users.log.info("javax.net.ssl.trustStore: " + System.getProperty("javax.net.ssl.trustStore"));
		Users.log.info("javax.net.ssl.trustStorePassword: " + System.getProperty("javax.net.ssl.trustStorePassword"));
		Users.log.info("####################################################################");
		
		MySqlConfig mysqlConfig = new MySqlConfig(Config.connectionUrl, Config.connectionUser, Config.connectionPassword);
		accounts = Accounts.withBasicAuth(Config.basicAuth, mysqlConfig);
	}

	/**
	 * This endpoint supplies a list of available user names and userIds that
	 * can be used for the more detailed actions.
	 *
	 * @param clientid The client id, forwarded by the AuthenticationFilter
	 * @param sub The subject, forwarded by the AuthenticationFilter
	 * @param offset The offset in the list of users, given as the amount of
	 * 			users to skip. Defaults to 0, thus returning the start of
	 * 			the list.
	 * @param count The maximum amount of users to return as the result of the
	 *			call. Only values between 1 and 50 are allowed, in order to
	 * 			limit the result size. Defaults to 50.
	 * @param email By using this parameter, it is possible to search for users
	 * 			with a given email. This should return at most one user. Using
	 * 			this parameter, the role `account-permissions:findUserByEmail`
	 * 			is required
	 *
	 * @return A HTTP response:
	 * 	200 OK - The requested list of users has been extracted and is returned
	 * 		as the body of the reply. If no users matched the query (i.e. the
	 * 		offset is too high), an empty array may be returned.
	 * 	401 Unauthorized - The requesting client did not supply identify information
	 * 		(Handled by the AuthenticationFilter)
	 * 	403 Forbidden - The requesting client does not have sufficient permissions
	 * 		to inspect the roles of users.
	 *  404 Not Found - The entity cannot be found (using the parameter email)
	 * 	422 Unprocessable Entity - The given query parameters where not valid.
	 * 		This might occur if either the given offset or count where negative,
	 * 		or if count was too large.
	 */
	@GET
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response getallUsers(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@DefaultValue("0") @QueryParam("offset") Integer offset,
		@DefaultValue("50") @QueryParam("count") Integer count,
        @QueryParam("email") String email
	) {

		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");

		log.info("#### Get Role ####");
		log.info("Client ID: " + clientid);
		log.info("Client Roles: " + clientRoles.toString());
		log.info("From: " + offset);
		log.info("To:" + count);
		log.info("Mail:" + email);
		log.info("##################");

		List<User> users = new LinkedList<>();

		if(email != null) {
			if(hasfindUserByEmailRole(clientRoles)) {
				UserIdentifier userIdentifier = accounts.findUserByEmail(email);
				if(userIdentifier != null) {
					users.add(new UserImpl(userIdentifier));
					return Response.ok(users).build();
				}

				return Response.status(Status.NOT_FOUND).build();
			}
		} else if(hasListUsersRole(clientRoles)) {
		    if (offset < 0 || count <= 0 || count > 50) {
		    	return Response.status(422).entity("Offset or count values not valid").build();
		    }

			List<UserIdentifier> userIdentifiers = accounts.getUsers(offset, count);
			for (UserIdentifier userIdentifier : userIdentifiers) {
				users.add(new UserImpl(userIdentifier));
			}

			return Response.ok(users).build();
		}

		return Response.status(Status.FORBIDDEN).build();
	}

	/**
	 * This endpoint retrieves a list of all roles available to the supplied 
	 * user and visible to the requesting tool. This includes global roles as 
	 * well as roles specific to the requesting tool.
	 * 
	 * @param clientid The client id, forwarded by the AuthenticationFilter
	 * @param sub The subject, forwarded by the AuthenticationFilter
	 * @param userid The user id from the path
	 * @return A HTTP response:
	 * 	200 OK - The request has been processed and the determined roles are returned.
	 * 	401 Unauthorized - The requesting client did not supply identify information
	 * 		(Handled by the AuthenticationFilter)
	 * 	403 Forbidden - The requesting client does not have sufficient permissions 
	 * 		to inspect the roles of users.
	 * 	404 Not Found - The supplied user ID does not denote an existing user.
	 */
	@GET
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response getAllRoles(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@PathParam("userid") String userid
	) {

		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");
		
		log.info("#### Get Roles ####");
		log.info("Client ID: " + clientid);
		log.info("Client Roles: " + clientRoles.toString());
		log.info("User ID: " + userid);
		log.info("####################");		
		
		if(!hasReadRole(clientRoles)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		try {
			// If this is reached, the client has one of the given roles.
			boolean clientOnly = !clientRoles.contains(AccessRoles.READ_GLOBAL_ROLES);
			String clientid2 = clientRoles.contains(AccessRoles.READ_LOCAL_ROLES) ? clientid : null;
			List<String> roles = accounts.getUserRoles(userid, clientid2, clientOnly);
			if(roles == null) {
				return Response.status(Status.NOT_FOUND).build();
			} 
			return Response.ok(roleConverter(roles)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}			
	}
	
	/**
	 * The new role to be assigned to the user.
	 * 
	 * @param clientid The client id, forwarded by the AuthenticationFilter
	 * @param sub The subject, forwarded by the AuthenticationFilter
	 * @param uriInfo Added by JAX-RS to create the Location header
	 * @param userid The user id from the path
	 * @param role The role, which is parsed from the JSON
	 * @return A HTTP response:
	 * 	201 Created - The given role was successfully assigned to the specified user. 
	 * 		This reply is also returned if the role was already assigned to the user previously; 
	 * 		in that case, no changes have been made.
	 * 	401 Unauthorized - The requesting client did not supply identify information
	 * 		(Handled by the AuthenticationFilter)
	 * 	403 Forbidden - The requesting client does not have sufficient permissions to assign 
	 * 		roles to users.
	 * 	404 Not Found - The supplied user ID does not denote an existing user.
	 * 	422 Unprocessable Entity - The supplied role is not valid. This usually 
	 * 		means that an experiment identifier was required as part of the role, 
	 * 		but was not supplied.
	 */
	@POST
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles")
	public Response postRole(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@Context UriInfo uriInfo,
		@PathParam("userid") String userid,
		Role role
	) {

		String rolename = role.getRole();
		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");
		
		if(!hasEditRole(clientRoles)) {
			return Response.status(Status.FORBIDDEN).build();
		}		
		
		try {
			log.info("#### Post Roles ####");
			log.info("Client ID: " + clientid);
			log.info("Client Roles: " + clientRoles.toString());
			log.info("User ID: " + userid);
			log.info("Role:" + rolename);
			log.info("####################");

			String permission = accounts.isRealmRole(rolename) 
				? AccessRoles.EDIT_GLOBAL_ROLES 
				: AccessRoles.EDIT_LOCAL_ROLES;
			
			if(clientRoles.contains(permission)) {
				Boolean success = accounts.setUserRole(userid, rolename);
				
				if(success) {
					//@see: http://stackoverflow.com/a/26094619/605890
					UriBuilder builder = uriInfo.getAbsolutePathBuilder();
					builder.path(rolename);
					return Response.created(builder.build()).build();
				} 
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.status(Status.FORBIDDEN).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}	

	/**
	 * This endpoint allows a requesting client to remove a role from a user, 
	 * effectively revoking permissions or access to specific resources.
	 * 
	 * This endpoint only allows a requesting tool to manipulate the global roles 
	 * or the roles specific to itself. A tool cannot remove permissions assigned 
	 * to the user that are specific to a different tool.
	 * 
	 * @param clientid The client id, forwarded by the AuthenticationFilter
	 * @param sub The subject, forwarded by the AuthenticationFilter
	 * @param userid The user id from the path
	 * @param rolename The rolename from the path
	 * @return A HTTP response:
	 * 	200 OK - The given role has been successfully removed from the supplied user.
	 * 	401 Unauthorized - The requesting client did not supply identify information
	 * 		(Handled by the AuthenticationFilter)
	 * 	403 Forbidden - The requesting client does not have the permissions to remove roles.
	 * 	404 Not Found - The user ID does not describe a valid user, or the role name 
	 * 		does not denote a valid role.
	 * 	422 Unprocessable Entity - The supplied role is not valid. This usually means 
	 * 		that an experiment identifier was required as part of the role, but was not supplied.
	 */
	@DELETE
	@Secured
	@Path("/{userid}/roles/{rolename}")
	public Response deleteRoleByName(
		@HeaderParam("X-ClientID") String clientid,
		@HeaderParam("X-Sub") String sub,
		@PathParam("userid") String userid,
		@PathParam("rolename") String rolename) {

		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");		
		
		log.info("#### Delete Role ####");
		log.info("Client ID: " + clientid);
		log.info("Client Roles: " + clientRoles.toString());
		log.info("User ID: " + userid);
		log.info("Role:" + rolename);
		log.info("#####################");
	
		if(!hasEditRole(clientRoles)) {
			return Response.status(Status.FORBIDDEN).build();
		}		
		
		try {
			
			String permission = accounts.isRealmRole(rolename) 
					? AccessRoles.EDIT_GLOBAL_ROLES 
					: AccessRoles.EDIT_LOCAL_ROLES;
				
			if(clientRoles.contains(permission)) {
				Boolean success = accounts.removeUserRole(userid, rolename);
				
				if(success) {
					return Response.ok().build();
				}
				
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.status(Status.FORBIDDEN).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * This endpoint can be used if a tool is not interested in the complete 
	 * list of assigned roles, but wants to check one specific role for a user.
	 * 
	 * @param clientid The client id, forwarded by the AuthenticationFilter
	 * @param sub The subject, forwarded by the AuthenticationFilter
	 * @param userid The user id from the path
	 * @param rolename The rolename from the path
	 * @return
	 *	200 OK - The role is assigned to the user.
	 * 	401 Unauthorized - The requesting client did not supply identify information
	 * 		(Handled by the AuthenticationFilter)
	 * 	403 Forbidden - The requesting client does not have the permissions to inspect 
	 * 		roles, or the client tried to inspect permissions that are not visible to 
	 * 		the client (e.g. because they belong to a different tool).
	 * 	404 Not Found - The user ID does not describe a valid user, or the role 
	 * 		name does not denote a valid role.
	 * 	422 Unprocessable Entity - The supplied role is not valid. This usually means 
	 * 		that an experiment identifier was required as part of the role, but was 
	 * 		not supplied.
	 */
	@GET
	@Secured
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{userid}/roles/{rolename}")
	public Response getRoleByName(
		@HeaderParam("X-ClientID") String clientid, 
		@HeaderParam("X-Sub") String sub,
		@PathParam("userid") String userid, 
		@PathParam("rolename") String rolename
	) {

		List<String> clientRoles = accounts.getUserRoles(sub, "accounts-permissions");		
		
		log.info("#### Get Role ####");
		log.info("Client ID: " + clientid);
		log.info("Client Roles: " + clientRoles.toString());
		log.info("User ID: " + userid);
		log.info("Role:" + rolename);
		log.info("##################");

		if(!hasReadRole(clientRoles)) {
			return Response.status(Status.FORBIDDEN).build();
		}		
		
		try {
			boolean clientOnly = !clientRoles.contains(AccessRoles.READ_GLOBAL_ROLES);
			String clientid2 = clientRoles.contains(AccessRoles.READ_LOCAL_ROLES) ? clientid : null;
			List<String> roles = accounts.getUserRoles(userid, clientid2, clientOnly);
			
			if(roles != null && roles.contains(rolename)) {
				return Response.ok().build();
			}
			
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	//#########################################################################
	// Local helper
	//#########################################################################

	/**
	 * Converts a List<String> to a List<Role>
	 * 
	 * @param roles A List<String>
	 * @return A List<Role>
	 */
	private List<Role> roleConverter(List<String> roles) {
		List<Role> newRoles = new LinkedList<Role>();
		for (String role: roles) {
			newRoles.add(new RoleImpl(role));
		}
		return newRoles;
	}

	/**
	 * Checks, if the user has the role `accounts-permissions:readGlobalRoles` or `accounts-permissions:readLocalRoles` 
	 * 
	 * @param clientRoles A list of roles which the user has
	 * @return true, if the user has the role, otherwise false
	 */
	private boolean hasReadRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.READ_GLOBAL_ROLES) || clientRoles.contains(AccessRoles.READ_LOCAL_ROLES);
	}
	
	/**
	 * Checks, if the user has the role `accounts-permissions:editGlobalRoles` or `accounts-permissions:editLocalRoles`
	 * 
	 * @param clientRoles A list of roles which the user has
	 * @return true, if the user has the role, otherwise false
	 */
	private boolean hasEditRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.EDIT_GLOBAL_ROLES) || clientRoles.contains(AccessRoles.EDIT_LOCAL_ROLES);
	}
	
	/**
	 * Checks, if the user has the role `accounts-permissions:listUsers`
	 *
	 * @param clientRoles A list of roles which the user has
	 * @return true, if the user has the role, otherwise false
	 */
	private boolean hasListUsersRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.LIST_USERS);
	}
	
	private boolean hasfindUserByEmailRole(List<String> clientRoles) {
		return clientRoles.contains(AccessRoles.FIND_USER_BY_MAIL);
	}

}