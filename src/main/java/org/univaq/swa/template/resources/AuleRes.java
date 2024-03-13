/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.univaq.swa.framework.model.Aula;


/**
 *
 * @author miche
 */

@Path("aule")
public class AuleRes {
    
    // 3
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAula(Aula payload){
        return Response.ok().build();
    }
    
    // 4 TODO
    @POST
    @Path("{nome: [a-zA-Z]+}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignGruppo(@PathParam("nome") String nome){
        return Response.ok().build();
    }
    
    // 5 TODO
    @GET
    @Path("{nome: [a-zA-Z]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoAula(@PathParam("nome") String nome){
        return Response.ok().build();
    }
    
    // 6 TODO
    @GET
    @Path("{nome: [a-zA-Z]+}/attrezzature")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttrezzatureAula(@PathParam("nome") String nome){
        return Response.ok().build();
    }    
    
    // 2
    @GET
    @Path("csv")
    @Produces("text/csv")
    public Response exportAuleCsv(){
        return Response.ok().build();
    }
    
    // 2
    @POST
    @Path("csv")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importAuleCsv(){
        return Response.ok().build();
    }
    
}
