package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.univaq.swa.framework.model.Evento;

/**
 *
 * @author miche
 */
public class EventoRes {

    private final Evento e;

    public EventoRes(Evento ev) {
        this.e = ev;
    }

    private static final String DS_NAME = "java:comp/env/jdbc/auleweb";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }

    // 8 TODO
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEvento(@PathParam("id") int idEvento, Map<String, Object> fieldsToUpdate) {
        System.out.println("SONO IN UPDATE EVENTO <----------------------------------------------------");
        
        StringBuilder queryBuilder = new StringBuilder("UPDATE evento SET ");
        for (String key : fieldsToUpdate.keySet()) {
            queryBuilder.append(key + " = ?, ");
        }
        // Remove the last ", " characters
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        queryBuilder.append(" WHERE id = ?");
        
        System.out.println("QUERY CREATA:");
        System.out.println(queryBuilder.toString());

        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(queryBuilder.toString())) {
            int i = 1;
            for (String key: fieldsToUpdate.keySet()){
                ps.setObject(i, fieldsToUpdate.get(key));
                i++;
            }
            ps.setInt(i, idEvento);
            int modifiedRows = ps.executeUpdate();
            if (modifiedRows > 0) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NamingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // 9 TODO: vedere perché stampa male il json dell'evento
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoEvento() {
        try {
            System.out.println("SONO IN GET INFO EVENTO <----------------------------------------------------");
            //Map <String,Object> evento = createEvento(e);
            return Response.ok(e).build();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Response.ok().build();
    }

    @POST
    private Map<String, Object> createEvento(Evento e) {
        Map<String, Object> evento = new HashMap<String, Object>();
        evento.put("id", e.getId());
        evento.put("nome", e.getNome());
        evento.put("orarioInizio", e.getOrarioInizio());
        evento.put("orarioFine", e.getOrarioFine());
        evento.put("descrizione", e.getDescrizione());
        evento.put("nomeOrganizzatore", e.getNomeOrganizzatore());
        evento.put("emailResponsabile", e.getEmailResponsabile());
        evento.put("tipologia", e.getTipologia());

        return evento;

    }
}
