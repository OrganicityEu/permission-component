package eu.organicity.mapper;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Provider
public class ServerErrorExceptionMapper implements ExceptionMapper<ServerErrorException> {
 
	private static Logger LOG = LoggerFactory.getLogger(ServerErrorExceptionMapper.class);
	
    @Override
    public Response toResponse(ServerErrorException ex) {
    	LOG.info("Server Error: " +  ex.toString());

    	JSONObject json = new JSONObject();
    	json.put("error", "Internal Server Error");
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(json.toString()).build();
    }
	
}