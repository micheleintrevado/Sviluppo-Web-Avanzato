
package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.univaq.swa.framework.model.Evento;
import org.univaq.swa.framework.model.Tipologia;

/**
 *
 * @author miche
 */
public class EventoRes {
    
    private final Evento e;

    
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
    public Response getInfoEvento(){
        try{
            //Map <String,Object> evento = createEvento(e);
            return Response.ok(e).build();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Response.ok().build();
    }
    
    private Map <String,Object> createEvento(Evento e){
        Map <String,Object> evento = new HashMap<String,Object>();
        evento.put("id",e.getId());
        evento.put("nome",e.getNome());
        evento.put("orarioInizio", e.getOrarioInizio());
        evento.put("orarioFine",e.getOrarioFine());
        evento.put("descrizione",e.getDescrizione());
        evento.put("nomeOrganizzatore",e.getNomeOrganizzatore());
        evento.put("emailResponsabile",e.getEmailResponsabile());
        evento.put("tipologia",e.getTipologia());

        return evento;
        
    }
}
