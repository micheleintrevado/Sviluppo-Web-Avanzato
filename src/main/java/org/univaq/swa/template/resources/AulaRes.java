package org.univaq.swa.template.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
import java.util.ArrayList;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.univaq.swa.framework.model.Attrezzatura;
import org.univaq.swa.framework.model.Aula;
import org.univaq.swa.template.exceptions.RESTWebApplicationException;

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
    private static final String QUERY_SELECT_AULA = "SELECT * FROM aula WHERE id = ?";
    private static final String QUERY_SELECT_ATTREZZATURA_AULA = "SELECT attrezzatura.id, attrezzatura.tipo "
            + "FROM attrezzatura join aula_attrezzatura on attrezzatura.id = aula_attrezzatura.id_attrezzatura where aula_attrezzatura.id_aula = ?";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }

    // 4 TODO
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@Path("assignGruppo")
    public Response assignGruppoAula(@Context UriInfo uriinfo, @PathParam("id") int idAula, HashMap<String, Object> gruppo) {
        System.out.println("---------------------------> MI TROVO IN assignGruppoAula DI AULA SINGOLA RES");
        String addAulaQuery = "INSERT INTO `aula_gruppo` (`id_aula`,`id_gruppo`) VALUES (?,?)";
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(addAulaQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idAula);
            ps.setInt(2, (int) gruppo.get("id_gruppo"));

            ps.executeUpdate();

            URI uri = uriinfo.getBaseUriBuilder()
                    .path(AuleRes.class)
                    .path(AuleRes.class, "getAula")
                    .build(idAula);

            return Response.created(uri).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 5 TODO
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

    // 6 TODO
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

            a.setId(rs.getInt("id"));
            a.setNome(rs.getString("nome"));
            a.setLuogo(rs.getString("luogo"));
            a.setEdificio(rs.getString("edificio"));
            a.setPiano(rs.getString("piano"));
            a.setCapienza(rs.getInt("capienza"));
            a.setEmailResponabile(rs.getString("email_responsabile"));
            a.setPreseElettriche(rs.getInt("prese_elettriche"));
            a.setPreseRete(rs.getInt("prese_rete"));
            a.setNote(rs.getString("note"));
            return a;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
