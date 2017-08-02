package eu.organicity.users;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * This is the interface for the basic `User
 * `
 * @author Dennis Boldt
 */
@JsonDeserialize(as=PasswordImpl.class)
public interface Password {

	public void setPassword(String password);
	public String getPassword();

}