package eu.organicity;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.Claims;

/*
 * @see: https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
	
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("A Bearer token must be provided in the Authorization header.", Response.status(Status.UNAUTHORIZED).build());
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        // Validate the token
        Claims claims;
        try {
            claims = validateToken(token);
        } catch (Exception e) {
            System.err.println("Token invalid: " + e.getMessage());
            throw new NotAuthorizedException("The provided Bearer token is not valid.", Response.status(Status.UNAUTHORIZED).build());
        }
        String sub = (String) claims.get("sub");

        System.out.println("Sub: " + sub);
        // Validate the roles
        List<AccessRoles> rolesFound = validateRoles(claims);
        System.out.println("Roles: " + rolesFound.toString());
        
        requestContext.getHeaders().add("X-ClientID", (String) claims.get("clientId"));
        requestContext.getHeaders().add("X-Client-Roles", Utils.convertAccessRolesToRolesString(rolesFound)); 
        requestContext.getHeaders().add("X-Sub", sub);
    }
    
    private List<AccessRoles> validateRoles(Claims claims) {

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<AccessRoles> methodRoles = extractRoles(resourceMethod);
        
        // No role check required
        if(methodRoles.size() == 0) {
        	return new LinkedList<>();
        }
    	
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> resourceAccess = claims.get("resource_access", Map.class);
        if(resourceAccess == null) {
        	throw new ForbiddenException("No authorzation to access this resource! (1)");
        }
        
        Map<String, Object> accountsPermissions = resourceAccess.get("accounts-permissions");
        if(accountsPermissions == null) {
        	throw new ForbiddenException("No authorzation to access this resource! (2)");
        }
        
        @SuppressWarnings("unchecked")
		List<String> tokenRoles = (List<String>) accountsPermissions.get("roles");
        if(tokenRoles == null) {
        	throw new ForbiddenException("No authorzation to access this resource! (3)");
        }

        //System.out.println("Annotated roles: " + methodRoles.toString());
        //System.out.println("User roles: " + tokenRoles.toString());
        
        List<AccessRoles> rolesFound = new LinkedList<>();
        
        for (AccessRoles mr : methodRoles) {
			for (String ar : tokenRoles) {
				if(ar.equals(mr.toString())) {
					rolesFound.add(mr);
				}
			}
		}
        
        if(rolesFound.size() == 0) {
        	throw new ForbiddenException("No authorzation to access this resource! (4)");	
        }
        
        return rolesFound;
        
    }
    
    // Extract the roles from the annotated element
    private List<AccessRoles> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<AccessRoles>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<AccessRoles>();
            } else {
            	AccessRoles[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }    

    private Claims validateToken(String token) throws Exception {
		JwtParser fwtparser = new JwtParser();
		Claims claims = fwtparser.parseJWT(token);
		
		/*
		 * TODO: Check, if client ID has "role" to change the permissions
		 * If not, 403 Forbidden
		 */
		
		return claims;
    }
}