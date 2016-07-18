package eu.organicity;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * This class is needed to enable JAX-RS within the Wildfly application server
 *  
 * @author Dennis Boldt
 */
@ApplicationPath("/")
public class JaxRsActivator extends Application {}