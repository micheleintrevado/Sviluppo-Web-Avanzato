package org.univaq.swa.template.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 *
 * @author miche
 */

@Path("eventi")
public class EventiRes {
    
    @GET
    @Produces("application/json")
    public int prova(){
       return 1;
    }
    
}
