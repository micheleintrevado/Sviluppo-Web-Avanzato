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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.univaq.swa.framework.model.Evento;
import org.univaq.swa.framework.model.Tipologia;
import org.univaq.swa.template.exceptions.RESTWebApplicationException;

/**
 *
 * @author miche
 */
@Path("eventi")
public class EventiRes {

    private static final String DS_NAME = "java:comp/env/jdbc/auleweb";
    private static final String QUERY_SELECT_EVENTO = "SELECT * FROM evento WHERE id = ?";
    

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }

    public EventiRes() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }
    
    private Evento createEvento(Map <String,Object> evento){
        Evento e = new Evento();
        e.setId((int) evento.get("id"));
        e.setNome((String) evento.get("nome"));
        e.setOrarioInizio((LocalDateTime) evento.get("orarioInizio"));
        e.setOrarioFine((LocalDateTime) evento.get("orarioFine"));
        e.setDescrizione((String) evento.get("descrizione"));
        e.setNomeOrganizzatore((String) evento.get("nomeOrganizzatore"));
        e.setEmailResponsabile((String) evento.get("emailResponsabile"));
        e.setTipologia((Tipologia) evento.get("tipologia"));
        
        return e;
        
    }
    
    @PATCH
    @Path("{id: [1-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEvento(@PathParam("id") int idEvento, Map<String, Object> fieldsToUpdate){
        System.out.println("PATCH DEGLI EVENTI");
        for(var x : fieldsToUpdate.keySet()){
            System.out.println(x);
        }
        
        Evento evento = new Evento();
        evento.setId(idEvento);
        EventoRes eventoRes = new EventoRes(evento);
        return eventoRes.updateEvento(idEvento, fieldsToUpdate);
    }
    
    @GET
    @Path("{id: [1-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public EventoRes getEvento(@PathParam("id") int idEvento) throws RESTWebApplicationException {
        try {
            //List<S> l = new ArrayList();
            Evento evento = null;
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTO)) {
                ps.setInt(1, idEvento);
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        evento = obtainEvento(rs);
                        
                    }
                }
            }
            return new EventoRes(evento);
            // return "<html lang=\"en\"><body><h1>" + l.toString() + "</h1></body></html>";
            // return Response.ok(l).build();
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("SQL SQL SQL SQL");
            return null;
        } catch (NamingException e) {
            //e.printStackTrace();
            System.out.println("NAMING NAMING NAMING NAMING");
            return null;
        }
    }

    // 7
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEvento() {
        return Response.ok().build();
    }

    // 12 TODO --> gestione parametri
    @GET
    @Produces("text/calendar")
    public Response getEventiForRange() {
        return Response.ok().build();
    }

    // 10 TODO, Cambiare nome
    @GET
    @Path("aule/{nome: [a-zA-Z]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAulaSettimana(@PathParam("nome") String nome
    ) {
        return Response.ok().build();
    }

    // 11
    @GET
    @Path("attuali")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAttuali() {
        return Response.ok().build();
    }

    // 11 TODO --> gestione parametri (x ore)
    @GET
    @Path("prossimi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiProssimi() {
        return Response.ok().build();
    }

    private Evento obtainEvento(ResultSet rs) {
        try {
            Evento e = new Evento();

            e.setId(rs.getInt("id"));
            e.setNome(rs.getString("nome"));
            //Date date = rs.getDate("orario_inizio");
            //e.setOrarioInizio(date.getTime().toLocalDateTime());
            e.setOrarioInizio(rs.getTimestamp("orario_inizio").toLocalDateTime());
            e.setOrarioFine(rs.getTimestamp("orario_fine").toLocalDateTime());
            e.setDescrizione(rs.getString("descrizione"));
            e.setNomeOrganizzatore(rs.getString("nome_organizzatore"));
            e.setEmailResponsabile(rs.getString("email_responsabile"));
            e.setTipologia(Tipologia.valueOf(rs.getString("tipologia")));
            return e;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
