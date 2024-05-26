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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.univaq.swa.framework.model.Evento;
import org.univaq.swa.framework.model.Ricorrenza;
import org.univaq.swa.framework.model.TipoRicorrenza;
import org.univaq.swa.framework.model.Tipologia;
import org.univaq.swa.template.exceptions.RESTWebApplicationException;

/**
 *
 * @author miche
 */
public class EventoRes {

    private final Evento e;

    public EventoRes(Evento ev) {
        this.e = ev;
    }

    private static final String DB_NAME = "java:comp/env/jdbc/auleweb";
    private static final String QUERY_SELECT_EVENTO = "SELECT E.id, E.nome, E.orario_inizio, E.orario_fine, E.descrizione, E.nome_organizzatore, E.email_responsabile, E.tipologia, E.id_master, E.id_aula, E.id_corso, R.id AS id_master, R.tipo AS tipo_ricorrenza, R.data_termine FROM Evento E LEFT JOIN Ricorrenza R ON E.id_master = R.id WHERE E.id = ?;";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DB_NAME);
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
    public Response getInfoEvento(@PathParam("id") int idEvento) throws RESTWebApplicationException{
        try {
            Evento evento = null;
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTO)) {
                ps.setInt(1, idEvento);
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        evento = obtainEvento(rs);
                    }
                }
            }
            return Response.ok(evento).build();
        } catch (SQLException e) {
            e.printStackTrace();    
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    private Evento obtainEvento(ResultSet rs) {
        try {
            Evento e = new Evento();

            e.setId(rs.getInt("id"));
            e.setNome(rs.getString("nome"));
            e.setOrarioInizio(rs.getTimestamp("orario_inizio").toLocalDateTime());
            e.setOrarioFine(rs.getTimestamp("orario_fine").toLocalDateTime());
            e.setDescrizione(rs.getString("descrizione"));
            e.setNomeOrganizzatore(rs.getString("nome_organizzatore"));
            e.setEmailResponsabile(rs.getString("email_responsabile"));
            e.setTipologia(Tipologia.valueOf(rs.getString("tipologia")));
            
            if (rs.getString("id_master") != null ){
                Ricorrenza r = new Ricorrenza();
                r.setId(rs.getInt("id_master"));
                switch (rs.getString("tipo_ricorrenza")) {
                    case "giornaliera":
                        r.setTipoRicorrenza(TipoRicorrenza.giornaliera);
                        break;
                    case "settimanale":
                        r.setTipoRicorrenza(TipoRicorrenza.settimanale);
                        break;
                    case "mensile":
                        r.setTipoRicorrenza(TipoRicorrenza.mensile);
                        break;
                }
                r.setDataTermine(rs.getTimestamp("data_termine").toLocalDateTime());
            }
            
            return e;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
