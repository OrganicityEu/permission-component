package eu.organicity;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * This is the interface for the basic `Role`
 * 
 * @author Dennis Boldt
 */
@JsonDeserialize(as=RoleImpl.class)
public interface Role {

	public void setRole(String role);
	public String getRole();
	
}