package eu.organicity;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize(as=RoleImpl.class)
public interface Role {

	public void setRole(String role);
	public String getRole();
	
}
