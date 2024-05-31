package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import jakarta.ws.rs.QueryParam;

import jakarta.ws.rs.container.ContainerRequestContext;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.univaq.swa.framework.model.Attrezzatura;
import org.univaq.swa.framework.model.Aula;
import org.univaq.swa.framework.model.Gruppo;
import org.univaq.swa.framework.security.Logged;
import org.univaq.swa.template.exceptions.RESTWebApplicationException;
import org.univaq.swa.framework.model.Evento;
import org.univaq.swa.framework.model.Tipologia;

/**
 *
 * @author miche
 */
public class AulaRes {

    private final Aula a;

    public AulaRes(Aula aula) {
        this.a = aula;
    }

    private static final String DS_NAME = "java:comp/env/jdbc/auleweb";
    private static final String QUERY_SELECT_EVENTI_SETTIMANA = "SELECT * FROM auleweb.evento where orario_inizio > ? and orario_fine < ? and id_aula = ?;";
    private static final String QUERY_SELECT_AULA = "SELECT * FROM aula WHERE id = ?";
    private static final String QUERY_SELECT_ATTREZZATURA_AULA = "SELECT attrezzatura.id, attrezzatura.tipo "
            + "FROM attrezzatura join aula_attrezzatura on attrezzatura.id = aula_attrezzatura.id_attrezzatura where aula_attrezzatura.id_aula = ?";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }

    // 4
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // @Logged
    //@Path("gruppi")
    public Response assignGruppoAula(@Context UriInfo uriinfo, @Context ContainerRequestContext req, @Context SecurityContext sec, @PathParam("id") int idAula, HashMap<String, Object> gruppo) {
        String addAulaQuery = "INSERT INTO `aula_gruppo` (`id_aula`,`id_gruppo`) VALUES (?,?)";
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(addAulaQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idAula);
            ps.setInt(2, (int) gruppo.get("id_gruppo"));

            ps.executeUpdate();

            URI uri = uriinfo.getBaseUriBuilder()
                    .path(AuleRes.class)
                    .path(AuleRes.class, "getAula")
                    .build(idAula);
            System.out.println(uri);

            return Response.created(uri).build();
        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 5
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoAula(@PathParam("id") int idAula) throws RESTWebApplicationException {
        try {
            Aula aula = null;
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_AULA)) {
                ps.setInt(1, idAula);
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        aula = obtainAula(rs);
                    }
                }
            }
            return Response.ok(aula).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        } catch (NamingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // 6 
    @GET
    @Path("attrezzature")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttrezzatureAula(@PathParam("id") int idAula) throws RESTWebApplicationException {
        try {
            ArrayList<Attrezzatura> attrezzature = new ArrayList<Attrezzatura>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_ATTREZZATURA_AULA)) {
                ps.setInt(1, idAula);
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        attrezzature.add(obtainAttrezzatura(rs));
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
    
    //10
    @GET
    //@Path("aule/{id: [1-9][0-9]*}/{settimana: }")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAulaSettimana(@PathParam("idAula") int idAula, @QueryParam("rangeStart") String rangeStart) {
        LocalDateTime dataOraInizio = LocalDateTime.parse(rangeStart, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime dataOraFine = dataOraInizio.plusDays(7);
        try {
            ArrayList<Evento> eventi = new ArrayList<Evento>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTI_SETTIMANA)) {
                ps.setTimestamp(1, Timestamp.valueOf(dataOraInizio));
                ps.setTimestamp(2, Timestamp.valueOf(dataOraFine));
                ps.setInt(3, idAula);
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Evento e = obtainEvento(rs);
                        eventi.add(e);
                    }
                }
            }
            return Response.ok(eventi).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        } catch (NamingException ex) {
            ex.printStackTrace();
            return null;
        }
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
            return e;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Attrezzatura obtainAttrezzatura(ResultSet rs) {
        try {
            Attrezzatura a = new Attrezzatura();

            a.setId(rs.getInt("id"));
            a.setTipo(rs.getString("tipo"));
            return a;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Aula obtainAula(ResultSet rs) {
        try {
            Aula a = new Aula();

            int idAula = rs.getInt("id");

            a.setId(idAula);
            a.setNome(rs.getString("nome"));
            a.setLuogo(rs.getString("luogo"));
            a.setEdificio(rs.getString("edificio"));
            a.setPiano(rs.getString("piano"));
            a.setCapienza(rs.getInt("capienza"));
            a.setEmailResponabile(rs.getString("email_responsabile"));
            a.setPreseElettriche(rs.getInt("prese_elettriche"));
            a.setPreseRete(rs.getInt("prese_rete"));
            a.setNote(rs.getString("note"));

            final String query_attrezzatura = "SELECT aula_attrezzatura.id as id_aula_attrezzatura, attrezzatura.id as id_attrezzatura, attrezzatura.tipo, aula_attrezzatura.id_aula FROM auleweb.attrezzatura join auleweb.aula_attrezzatura on attrezzatura.id = aula_attrezzatura.id where id_aula = ?;";
            final String query_gruppo = "SELECT aula_gruppo.id_aula as id_aula, aula_gruppo.id_gruppo as id_gruppo, gruppo.nome as nome_gruppo, gruppo.descrizione as descrizione_gruppo FROM auleweb.gruppo join aula_gruppo on gruppo.id = aula_gruppo.id_gruppo where aula_gruppo.id_aula = ?;";
            // final String query_aule_associate = "SELECT aula.nome FROM auleweb.aula_gruppo join gruppo on gruppo.id = id_gruppo join aula on aula.id = id_aula where id_gruppo = ?;";

            ArrayList<Gruppo> gruppi = new ArrayList<Gruppo>();
            ArrayList<Attrezzatura> attrezzature = new ArrayList<Attrezzatura>();

            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(query_gruppo)) {
                ps.setInt(1, idAula);
                try ( ResultSet rsGruppo = ps.executeQuery()) {
                    while (rsGruppo.next()) {
                        Gruppo group = new Gruppo();
                        group.setId(rsGruppo.getInt(2));
                        group.setNome(rsGruppo.getString(3));
                        group.setDescrizione(rsGruppo.getString(4));
                        // group.setAuleAssociate(null);
                        gruppi.add(group);

                    }
                }
            } catch (NamingException ex) {
                Logger.getLogger(AulaRes.class.getName()).log(Level.SEVERE, null, ex);
            }

            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(query_attrezzatura)) {
                ps.setInt(1, idAula);
                try ( ResultSet rsAttrezzatura = ps.executeQuery()) {
                    while (rsAttrezzatura.next()) {
                        Attrezzatura attrezzatura = new Attrezzatura();
                        attrezzatura.setId(rsAttrezzatura.getInt(2));
                        attrezzatura.setTipo(rsAttrezzatura.getString(3));
                        attrezzature.add(attrezzatura);
                    }
                }
            } catch (NamingException ex) {
                Logger.getLogger(AulaRes.class.getName()).log(Level.SEVERE, null, ex);
            }

            a.setGruppiAssociati(gruppi);
            a.setAttrezzatureAssociate(attrezzature);
            return a;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
