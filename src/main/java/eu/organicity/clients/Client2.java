package eu.organicity.clients;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * This is the interface for the basic `Role`
 * 
 * @author Dennis Boldt
 */
@JsonDeserialize(as=ClientImpl2.class)
public interface Client2 {

	public void setClientId(String clientId);
	public String getClientId();

	public void setRoles(String[] roles);
	public String[] getRoles();
	
}