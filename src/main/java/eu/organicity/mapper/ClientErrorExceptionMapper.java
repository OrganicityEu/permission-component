package eu.organicity.mapper;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {
 
	private static Logger LOG = LoggerFactory.getLogger(ClientErrorExceptionMapper.class);
	
    @Override
    public Response toResponse(ClientErrorException ex) {
    	
    	LOG.info("Client Error: " + ex.getMessage());
    	
    	ex.getStackTrace();
    	
    	JSONObject json = new JSONObject();
    	json.put("error", ex.getMessage());
        return Response.status(ex.getResponse().getStatus()).entity(json.toString()).build();
    }
}