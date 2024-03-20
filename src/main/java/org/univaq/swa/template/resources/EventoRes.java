/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.univaq.swa.framework.model.Evento;

/**
 *
 * @author miche
 */
public class EventoRes {
    
    private final Evento e;

    // CREARE MAPPA PER STAMPARE OGGETTO EVENTO
    public EventoRes(Evento ev){
        this.e = ev;
    }
    
    // 8 TODO
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEvento(@PathParam("evento") int idEvento){
        return Response.noContent().build();
    }
    
    // 9 TODO
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoEvento(@PathParam("evento") int idEvento){
        return Response.ok().build();
    }
}
