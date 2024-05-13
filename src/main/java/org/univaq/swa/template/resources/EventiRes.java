package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.univaq.swa.framework.model.Attrezzatura;
import org.univaq.swa.framework.model.Evento;
import org.univaq.swa.framework.model.Tipologia;
import org.univaq.swa.framework.security.Logged;
import org.univaq.swa.template.exceptions.RESTWebApplicationException;

/**
 *
 * @author miche
 */
@Path("eventi")
public class EventiRes {

    private static final String DB_NAME = "java:comp/env/jdbc/auleweb";
    private static final String QUERY_SELECT_EVENTI_SETTIMANA = "SELECT * FROM evento WHERE week((DATE(orario_inizio))) = week((CONCAT(YEAR(orario_inizio), '-01-01'))) + ? - 1;";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DB_NAME);
        return ds.getConnection();
    }

    public EventiRes() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    private Evento createEvento(Map<String, Object> evento) {
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
    public Response updateEvento(@PathParam("id") int idEvento, Map<String, Object> fieldsToUpdate) {
        Evento evento = new Evento();
        evento.setId(idEvento);
        EventoRes eventoRes = new EventoRes(evento);
        return eventoRes.updateEvento(idEvento, fieldsToUpdate);
    }

    @GET
    @Path("{id: [1-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvento(@PathParam("id") int idEvento) throws RESTWebApplicationException {
        Evento evento = new Evento();
        evento.setId(idEvento);
        EventoRes eventoRes = new EventoRes(evento);

        return eventoRes.getInfoEvento(idEvento);
    }

    // 7
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //@Logged
    public Response addEvento(@Context UriInfo uriinfo, HashMap<String, Object> evento) {
        String addEventoQuery = "INSERT INTO `evento` (`nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_master`, `id_aula`, `id_corso`) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try ( Connection con = getPooledConnection(); PreparedStatement ps = con.prepareStatement(addEventoQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, (String) evento.get("nome"));
            
            var orarioInizio = evento.get("orario_inizio");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse((String) orarioInizio, formatter);
            ps.setTimestamp(2, Timestamp.valueOf(dateTime));
            
            var orarioFine = evento.get("orario_fine");
            dateTime = LocalDateTime.parse((String) orarioFine, formatter);
            ps.setTimestamp(3, Timestamp.valueOf(dateTime));
            
            ps.setString(4, (String)evento.get("descrizione"));
            ps.setString(5, (String)evento.get("nome_organizzatore"));
            ps.setString(6, (String)evento.get("email_responsabile"));
            
            ps.setString(7, (String)evento.get("tipologia"));
            
            ps.setInt(8, (int)evento.get("id_master"));
            ps.setInt(9, (int)evento.get("id_aula"));
            ps.setInt(10, (int)evento.get("id_corso"));
            
            ps.executeUpdate();
            
            try ( ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int idEvento = keys.getInt(1);
                URI uri = uriinfo.getBaseUriBuilder()
                        .path(EventiRes.class)
                        .path(EventiRes.class, "getEvento")
                        .build(idEvento);
                return Response.created(uri).build();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch(NamingException ex){
            ex.printStackTrace();
        }
        return null;
    }

    // 12 TODO --> gestione parametri esportazione in formato iCalendar
    @GET
    @Produces("text/calendar")
    public Response getEventiForRange() {
        return Response.ok().build();
    }

    // 10 TODO, Cambiare nome
    @GET
    @Path("aule/{nome: [a-zA-Z]+}/{settimana: }")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAulaSettimana(@PathParam("nome") String nome
    ) {
        try {
            ArrayList<Attrezzatura> attrezzature = new ArrayList<Attrezzatura>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTI_SETTIMANA)) {
                ps.setInt(1, idAula);
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
                        //attrezzature.add(obtainAttrezzatura(rs));
                    }
                }
            }
            return Response.ok(attrezzature).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        } catch (NamingException ex) {
            ex.printStackTrace();
            return null;
        }
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

}
