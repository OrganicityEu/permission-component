package eu.organicity.mapper;
import java.net.UnknownHostException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Provider
public class UnknownHostExceptionMapper implements ExceptionMapper<UnknownHostException> {
 
	private static Logger log = LoggerFactory.getLogger(UnknownHostExceptionMapper.class);
	
    @Override
    public Response toResponse(UnknownHostException ex) {
    	log.info("Unknown Host: " + ex.toString());

    	JSONObject json = new JSONObject();
    	json.put("error", "Internal Server Error");
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(json.toString()).build();
    }
}