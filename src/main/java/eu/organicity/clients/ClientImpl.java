package eu.organicity.clients;

/**
 * This implements the basic role.
 * 
 * @author boldt
 */
public class ClientImpl implements Client {

	private String clientUri;
	private String clientName;
	private String redirectUri;

	// Needed by Jackson
	public ClientImpl() {}
	
	@Override
	public String getClientName() {
		return clientName;
	}
	
	@Override
	public String getClientUri() {
		return clientUri;
	}
	
	@Override
	public String getRedirectUri() {
		return redirectUri;
	}
	
	@Override
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	@Override
	public void setClientUri(String clientUri) {
		this.clientUri = clientUri;
	}
	
	@Override
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Client) {
			Client r = (Client) o;
			return (
				this.getClientName().equals(r.getClientName()) &&
				this.getClientUri().equals(r.getClientUri()) &&
				this.getRedirectUri().equals(r.getRedirectUri())
			);
		}
		return false;
	}
	
}