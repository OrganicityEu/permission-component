package eu.organicity.clients;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * This is the interface for the basic `Role`
 * 
 * @author Dennis Boldt
 */
@JsonDeserialize(as=ClientImpl.class)
public interface Client {

	public void setClientName(String name);
	public String getClientName();

	public void setClientUri(String uri);
	public String getClientUri();

	public void setRedirectUri(String uri);
	public String getRedirectUri();
	
}