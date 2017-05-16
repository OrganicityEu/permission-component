package eu.organicity;

/**
 * All possible roles
 * 
 * @author Dennis Boldt
 */
public enum AccessRoles {

	/*
	 * USERS
	 */
	READ_GLOBAL_ROLES("accounts-permissions:readGlobalRoles"),
	READ_LOCAL_ROLES("accounts-permissions:readLocalRoles"),
	EDIT_GLOBAL_ROLES("accounts-permissions:editGlobalRoles"),
	EDIT_LOCAL_ROLES("accounts-permissions:editLocalRoles"),

	LIST_USERS("accounts-permissions:listUsers"),
	FIND_USER_BY_MAIL("accounts-permissions:findUserByEmail"),
	
	GET_USER_DETAILS("accounts-permissions:getUserDetails"),
	EDIT_USER_DETAILS("accounts-permissions:editUserDetails"),

	/*
	 * CLIENTS
	 */
	CREATE_CLIENT("accounts-permissions:createClient"),
	DELETE_CLIENT("accounts-permissions:deleteClient"),
	READ_CLIENT("accounts-permissions:readClient"),
	READ_CLIENT_SECRET("accounts-permissions:readClientSecret"),
	READ_CLIENT_REDIRECTURIS("accounts-permissions:readClientRedirectUris"),
	EDIT_CLIENT_REDIRECTURIS("accounts-permissions:editClientRedirectUris"),
	
	/*
	 * ROLES
	 */
	
	GET_SUBS_BY_ROLE("accounts-permissions:getSubsPerRole");
	
    private final String name;       

    private AccessRoles(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return this.name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }

}
