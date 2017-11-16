package eu.organicity.clients;

/**
 * This implements the basic role.
 * 
 * @author boldt
 */
public class Client2Impl implements Client2 {

	private String clientId;
	private String[] roles;

	// Needed by Jackson
	public Client2Impl() {}

	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	
	@Override
	public String[] getRoles() {
		return roles;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Client2) {
			Client2 r = (Client2) o;
			return (
				this.getClientId().equals(r.getClientId())
				// TODO: Handle client roles
			);
		}
		return false;
	}
	
}