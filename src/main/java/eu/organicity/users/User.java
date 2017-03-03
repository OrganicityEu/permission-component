package eu.organicity.users;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * This is the interface for the basic `User
 * `
 * @author Dennis Boldt
 */
@JsonDeserialize(as=UserImpl.class)
public interface User {

	public void setId(String id);
	public String getId();

	public void setName(String name);
	public String getName();

	public String getFirstName();
	public void setFirstName(String firstName);

	public String getLastName();
	public void setLastName(String lastName);

	public String getEmail();
	public void setEmail(String email);

}