package eu.organicity;

import eu.organicity.accounts.permissions.UserIdentifier;

/**
 * This implements the basic role.
 * 
 * @author boldt
 */
public class UserImpl implements User {

	private String id;
	private String name;

	// Needed by Jackson
	public UserImpl() {}
	
	public UserImpl(UserIdentifier user) {
		setId(user.getId());
		setName(user.getName());
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

	@Override
	public boolean equals(Object o) {
		if(o instanceof User) {
			User u = (User) o;
			return this.getId().equals(u.getId()) && this.getName().equals(u.getName());
		}
		return false;
	}
	
}