package eu.organicity;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.Claims;

/*
 * @see: https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Bearer Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {

            // Validate the token
            String clientid = validateToken(token);
            System.out.println("TOKEN accepted");
            requestContext.getHeaders().add("X-ClientID", clientid);            
        } catch (Exception e) {
            System.out.println("TOKEN invalid");
            e.printStackTrace();
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private String validateToken(String token) throws Exception {
		JwtParser fwtparser = new JwtParser();
		Claims claims = fwtparser.parseJWT(token);
		System.out.println("Component realm: " + claims.get("aud"));
		
		/*
		 * TODO: Check, if client ID has "role" to change the permissions
		 * If not, 403 Forbidden
		 */
		
		return (String) claims.get("aud");
    }
}