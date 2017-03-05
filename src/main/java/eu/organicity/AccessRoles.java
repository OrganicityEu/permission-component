package eu.organicity;

/**
 * All possible roles
 * 
 * @author Dennis Boldt
 */
public class AccessRoles {

	/*
	 * USERS
	 */
	public static String READ_GLOBAL_ROLES = "accounts-permissions:readGlobalRoles";
	public static String READ_LOCAL_ROLES = "accounts-permissions:readLocalRoles";
	public static String EDIT_GLOBAL_ROLES = "accounts-permissions:editGlobalRoles";
	public static String EDIT_LOCAL_ROLES = "accounts-permissions:editLocalRoles";

	public static String LIST_USERS = "accounts-permissions:listUsers";
	public static String FIND_USER_BY_MAIL = "accounts-permissions:findUserByEmail";
	
	public static String GET_USER_DETAILS = "accounts-permissions:getUserDetails";
	public static String EDIT_USER_DETAILS = "accounts-permissions:editUserDetails";

	/*
	 * CLIENTS
	 */
	public static String CREATE_CLIENT = "accounts-permissions:createClient";
	public static String READ_CLIENT_REDIRECTURIS = "accounts-permissions:readClientRedirectUris";
	public static String EDIT_CLIENT_REDIRECTURIS = "accounts-permissions:editClientRedirectUris";

}
