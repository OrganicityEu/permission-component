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
	READ_GLOBAL_ROLES("readGlobalRoles"),
	READ_LOCAL_ROLES("readLocalRoles"),
	EDIT_GLOBAL_ROLES("editGlobalRoles"),
	EDIT_LOCAL_ROLES("editLocalRoles"),

	LIST_USERS("listUsers"),
	FIND_USER_BY_MAIL("findUserByEmail"),
	
	GET_USER_DETAILS("getUserDetails"),
	EDIT_USER_DETAILS("editUserDetails"),

	/*
	 * CLIENTS
	 */
	CREATE_CLIENT("createClient"),
	DELETE_CLIENT("deleteClient"),
	READ_CLIENT("readClient"),
	READ_CLIENT_SECRET("readClientSecret"),
	READ_CLIENT_REDIRECTURIS("readClientRedirectUris"),
	EDIT_CLIENT_REDIRECTURIS("editClientRedirectUris"),
	
	/*
	 * ROLES
	 */
	
	GET_SUBS_BY_ROLE("getSubsPerRole");
	
	/*
	 * HELPER
	 */
	
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

    // @see: https://stackoverflow.com/questions/604424/lookup-enum-by-string-value
    public static AccessRoles fromString(String text) {
        for (AccessRoles b : AccessRoles.values()) {
          if (b.name.equalsIgnoreCase(text)) {
            return b;
          }
        }
        return null;
      }
    
}
