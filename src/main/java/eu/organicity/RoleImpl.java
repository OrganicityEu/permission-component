package eu.organicity;

/**
 * This implements the basic role.
 * 
 * @author boldt
 */
public class RoleImpl implements Role {

	private String role;

	// Needed by Jackson
	public RoleImpl() {}
	
	public RoleImpl(String role) {
		setRole(role);
	}
	
	@Override
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String getRole() {
		return this.role;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Role) {
			Role r = (Role) o;
			return this.getRole().equals(r.getRole());
		}
		return false;
	}
	
}