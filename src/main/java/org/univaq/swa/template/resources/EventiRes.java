package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.univaq.swa.framework.model.Evento;

/**
 *
 * @author miche
 */

@Path("eventi")
public class EventiRes {
    Connection databaseConnection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/auleweb?serverTimezone=Europe/Rome", "auleWebUser", "auleWebPassword");
    
    public EventiRes() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }
    
    
    @Path("{nome: [a-zA-Z]+}")
    public EventoRes getEvento(@PathParam("nome") String nomeEvento) throws SQLException {
        Evento evento = new Evento();
        try (PreparedStatement query = databaseConnection.prepareStatement("SELECT * FROM EVENTO WHERE NOME = ?")){
            query.setString(1, nomeEvento);
            try (ResultSet result = query.executeQuery()){
                if(result.next()){
                    System.out.println(result.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new EventoRes(nomeEvento);
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
