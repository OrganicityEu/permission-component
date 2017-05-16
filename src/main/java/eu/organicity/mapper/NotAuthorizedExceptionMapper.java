package eu.organicity.mapper;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
 
	private static Logger LOG = LoggerFactory.getLogger(NotAuthorizedExceptionMapper.class);
	
    @Override
    public Response toResponse(NotAuthorizedException ex) {
    	LOG.info("Not Authorized: " + ex.getMessage());
    	
    	JSONObject json = new JSONObject();
    	json.put("error", "Unauthorized: " + ex.getMessage());
        return Response.status(Status.UNAUTHORIZED).entity(json.toString()).build();
    }
}