package eu.organicity.users;

/**
 * This implements a raw password
 * 
 * @author boldt
 */
public class PasswordImpl implements Password {

	private String password;

	// Needed by Jackson/JAX-B
	public PasswordImpl() {}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Password) {
			Password p = (Password) o;
			return this.getPassword().equals(p.getPassword());
		}
		return false;
	}
	
}