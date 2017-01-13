package eu.organicity.clients;

import java.util.List;

/**
 * This implements the basic role.
 *
 * @author boldt
 */
public class RedirectUriImpl implements RedirectUri {

	private List<String> uris;

	// Needed by Jackson
	public RedirectUriImpl() {}

	@Override
	public void setRedirectUris(List<String> uris) {
		this.uris = uris;
	}

	@Override
	public List<String> getRedirectUris() {
		return this.uris;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof RedirectUri) {
			RedirectUri r = (RedirectUri) o;
			return this.getRedirectUris().equals(r.getRedirectUris());
		}
		return false;
	}
}