package eu.organicity.users;

import eu.organicity.accounts.permissions.UserIdentifier;

/**
 * This implements the basic role.
 * 
 * @author boldt
 */
public class UserImpl implements User {

	private String id;
	private String name;
	private String firstName;
	private String lastName;
	private String email;

	// Needed by Jackson/JAX-B
	public UserImpl() {}
	
	public UserImpl(UserIdentifier user) {
		setId(user.getId());
		setName(user.getName());
		setFirstName(user.getFirstName());
		setLastName(user.getLastName());
		setEmail(user.getEmail());
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof User) {
			User u = (User) o;
			return this.getId().equals(u.getId()) && this.getName().equals(u.getName());
		}
		return false;
	}
	
}