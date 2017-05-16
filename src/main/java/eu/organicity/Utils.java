package eu.organicity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Utils {
	
	public static List<AccessRoles> convertRolesStringToAccessRoles(String roles) {
		
		List<String> clientRoles = Arrays.asList(roles.split(","));
		List<AccessRoles> accessRoles = new LinkedList<>();
		
		for (String role : clientRoles) {
			accessRoles.add(AccessRoles.fromString(role));
		}

		return accessRoles;
	} 
	
	public static String convertAccessRolesToRolesString(List<AccessRoles> accessRoles) {
		List<String> roles = new LinkedList<>();
		for (AccessRoles role : accessRoles) {
			roles.add(role.toString());
		}
		return StringUtils.join(roles, ",");
	} 	

}
