package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    
    // 7
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEvento(){
        return Response.ok().build();
    }
    
    // 12 TODO --> gestione parametri
    @GET
    @Produces("text/calendar")
    public Response getEventiForRange(){
        return Response.ok().build();
    }
    
    // 8 TODO
    @PATCH
    @Path("{evento: [0-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEvento(@PathParam("evento") int idEvento){
        return Response.noContent().build();
    }
    
    // 9 TODO
    @GET
    @Path("{evento: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoEvento(@PathParam("evento") int idEvento){
        return Response.ok().build();
    }
    
    // 10 TODO, Cambiare nome
    @GET
    @Path("aule/{nome: [a-zA-Z]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAulaSettimana(@PathParam("nome") String nome){
        return Response.ok().build();
    }
    
    // 11
    @GET
    @Path("attuali")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAttuali(){
        return Response.ok().build();
    }
    
    // 11 TODO --> gestione parametri (x ore)
    @GET
    @Path("prossimi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiProssimi(){
        return Response.ok().build();
    }    
}
