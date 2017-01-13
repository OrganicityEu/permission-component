package eu.organicity;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * This is the interface for the basic `Role`
 *
 * @author Dennis Boldt
 */
@JsonDeserialize(as=RedirectUriImpl.class)
public interface RedirectUri {
	public void setRedirectUris(List<String> uris);
	public List<String> getRedirectUris();
}