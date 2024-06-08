package org.univaq.swa.template.resources;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;

import jakarta.ws.rs.container.ContainerRequestContext;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.io.InputStream;
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
import org.univaq.swa.framework.security.Logged;
import org.univaq.swa.framework.utility.CsvUtility;
import org.univaq.swa.template.exceptions.RESTWebApplicationException;

/**
 *
 * @author miche
 */
@Path("aule")
public class AuleRes {

    @Context
    private ServletContext servletContext;

    private static final String DS_NAME = "java:comp/env/jdbc/auleweb";
    private static final String QUERY_SELECT_CONFIGURAZIONE_AULE = "SELECT aula.id, aula.nome AS nome_aula, luogo, edificio, piano, capienza, email_responsabile, prese_elettriche, prese_rete, note, gruppo.nome AS nome_gruppo FROM aula LEFT JOIN aula_gruppo ON aula.id = aula_gruppo.id_aula LEFT JOIN gruppo ON aula_gruppo.id_gruppo = gruppo.id;";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }

    public AuleRes() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    @GET
    @Path("{id: [1-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAula(@PathParam("id") int idAula) throws RESTWebApplicationException {
        Aula aula = new Aula();
        aula.setId(idAula);
        AulaRes aulaRes = new AulaRes(aula);

        return aulaRes.getInfoAula(idAula);
    }

    // 10
    @GET
    @Path("{idAula: [1-9][0-9]*}/eventi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAulaSettimana(@PathParam("idAula") int idAula, @QueryParam("rangeStart") String rangeStart) {
        Aula aula = new Aula();
        aula.setId(idAula);
        AulaRes aulaRes = new AulaRes(aula);

        return aulaRes.getEventiAulaSettimana(idAula, rangeStart);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id: [1-9][0-9]*}/gruppi")
    @Logged
    public Response assignGruppo(@Context UriInfo uriinfo, @Context HttpServletRequest req, @Context SecurityContext sec, @PathParam("id") int idAula, HashMap<String, Object> gruppo) throws RESTWebApplicationException {
        // System.out.println("AULE RES ---------------------------" + req.getProperty("token"));
        System.out.println("HTTP REQUEST ASSIGN: " + req.getAttribute("token"));

        Aula aula = new Aula();
        aula.setId(idAula);
        AulaRes aulaRes = new AulaRes(aula);

        return aulaRes.assignGruppoAula(uriinfo, sec, idAula, gruppo);
    }

    // 3
    @POST
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAula(@Context UriInfo uriinfo,@Context ContainerRequestContext req, HashMap<String, Object> aula) {
        System.out.println("AULA NOME: " + aula.get("nome"));
        System.out.println("AULA CAPIENZA: " + aula.get("capienza"));
        System.out.println("AULA CAPIENZA CLASS: " + aula.get("capienza").getClass());

        String addAulaQuery = "INSERT INTO `aula` (`nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)";
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(addAulaQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, (String) aula.get("nome"));
            ps.setString(2, (String) aula.get("luogo"));
            ps.setString(3, (String) aula.get("edificio"));
            ps.setString(4, (String) aula.get("piano"));
            ps.setInt(5, (int) aula.get("capienza"));
            ps.setString(6, (String) aula.get("email_responsabile"));
            ps.setInt(7, (int) aula.get("prese_elettriche"));
            ps.setInt(8, (int) aula.get("prese_rete"));
            ps.setString(9, (String) aula.get("note"));

            ps.executeUpdate();

            try ( ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                URI uri = uriinfo.getBaseUriBuilder()
                        .path(AuleRes.class)
                        .path(AuleRes.class, "getAula")
                        .build(id);
                System.out.println("URI: " + uri);
                return Response.created(uri).build();
            }
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // 2
    @GET
    @Path("csv")
    @Produces("text/csv")
    public Response exportAuleCsv() {
        try {
            ArrayList<HashMap<String, Object>> configurazioneAule = new ArrayList<HashMap<String, Object>>();

            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_CONFIGURAZIONE_AULE)) {
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, Object> confAula = new HashMap<String, Object>();
                        confAula.put("id", rs.getInt("id"));
                        confAula.put("nomeAula", rs.getString("nome_aula"));
                        confAula.put("luogo", rs.getString("luogo"));
                        confAula.put("edificio", rs.getString("edificio"));
                        confAula.put("piano", rs.getString("piano"));
                        confAula.put("capienza", rs.getInt("capienza"));
                        confAula.put("emailResponsabile", rs.getString("email_responsabile"));
                        confAula.put("preseElettriche", rs.getInt("prese_elettriche"));
                        confAula.put("preseRete", rs.getInt("prese_rete"));
                        confAula.put("note", rs.getString("note"));
                        confAula.put("nomeGruppo", rs.getString("nome_gruppo"));
                        configurazioneAule.add(confAula);
                    }
                }
            }

            // csv file
            String path = servletContext.getRealPath("");
            File fileCsv = new File(path, "csv\\configurazione_aule.csv");
            if (!fileCsv.getParentFile().exists()) {
                fileCsv.getParentFile().mkdirs();
            }
            CsvUtility.csvAuleWrite(fileCsv, configurazioneAule);
            return Response.ok(fileCsv, "text/csv").
                    header("Content-Disposition", "attachment;filename=configurazione_aule.csv")
                    .build();
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // 2
    @POST
    @Path("csv")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Logged
    public Response importAuleCsv(@Context UriInfo uriinfo, InputStream csvFile) throws NamingException, SQLException {
        ArrayList<HashMap<String, Object>> csvData = CsvUtility.csvAuleRead(csvFile);
        Connection con = getPooledConnection();
        final String addAulaQuery = "INSERT INTO `aula` (`nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        final String addAulaGruppoQuery = "";
        final String addGruppoQuery = "";
        for (HashMap<String, Object> aula : csvData) {
            try ( PreparedStatement addAulaStatement = con.prepareStatement(addAulaQuery, Statement.RETURN_GENERATED_KEYS)) {
                addAulaStatement.setString(1, (String) aula.get("nomeAula"));
                addAulaStatement.setString(2, (String) aula.get("luogo"));
                addAulaStatement.setString(3, (String) aula.get("edificio"));
                addAulaStatement.setString(4, (String) aula.get("piano"));
                addAulaStatement.setInt(5, Integer.parseInt((String) aula.get("capienza")));
                addAulaStatement.setString(6, (String) aula.get("emailResponsabile"));
                addAulaStatement.setInt(7, Integer.parseInt((String) aula.get("preseElettriche")));
                addAulaStatement.setInt(8, Integer.parseInt((String) aula.get("preseRete")));
                addAulaStatement.setString(9, (String) aula.get("note"));

                addAulaStatement.executeUpdate();
                try ( ResultSet rsAddAula = addAulaStatement.getGeneratedKeys();) {
                    rsAddAula.next();
                    int aula_id = rsAddAula.getInt(1);

                    // vedo se l'aula appartiene a un gruppo e se quest'ultimo è già esistente nel DB non lo creo nuovamente
                    if ((String) aula.get("nomeGruppo") != "") {
                        final String selectGruppoQuery = "SELECT * FROM GRUPPO where nome = ?;";
                        try ( PreparedStatement selectGruppoStatement = con.prepareStatement(selectGruppoQuery, Statement.RETURN_GENERATED_KEYS)) {
                            selectGruppoStatement.setString(1, (String) aula.get("nomeGruppo"));
                            try ( ResultSet rsSelectGruppo = selectGruppoStatement.executeQuery()) {
                                if (rsSelectGruppo.next()) {
                                    // caso in cui il gruppo specifico è già esistente, devo solo associare l'aula al gruppo

                                    final String insertAulaGruppoQuery = "INSERT INTO `aula_gruppo` (`id_aula`,`id_gruppo`) VALUES (?,?);";
                                    try ( PreparedStatement insertAulaGruppoStatement = con.prepareStatement(insertAulaGruppoQuery, Statement.RETURN_GENERATED_KEYS)) {
                                        insertAulaGruppoStatement.setLong(1, aula_id);
                                        insertAulaGruppoStatement.setLong(2, rsSelectGruppo.getInt(1));
                                        insertAulaGruppoStatement.executeUpdate();
                                    }
                                } else {
                                    // caso in cui il gruppo specifico non è già esistente. quindi devo crearlo

                                    // creazione nel DB del nuovo gruppo
                                    final String insertGruppoQuery = "INSERT INTO `gruppo` (`nome`) VALUES (?);";
                                    try ( PreparedStatement insertGruppoStatement = con.prepareStatement(insertGruppoQuery, Statement.RETURN_GENERATED_KEYS)) {
                                        insertGruppoStatement.setString(1, (String) aula.get("nomeGruppo"));
                                        insertGruppoStatement.executeUpdate();
                                        // PRENDO L'ID DEL GRUPPO CHE HO APPENA CREATO
                                        try ( ResultSet rsInsertGruppo = insertGruppoStatement.getGeneratedKeys();) {
                                            rsInsertGruppo.next();
                                            int gruppo_id = rsInsertGruppo.getInt(1);

                                            // creazione nel DB dell'associazione tra aula e gruppo
                                            final String insertAulaGruppoQuery = "INSERT INTO `aula_gruppo` (`id_aula`,`id_gruppo`) VALUES (?,?);";
                                            try ( PreparedStatement insertAulaGruppoStatement = con.prepareStatement(insertAulaGruppoQuery, Statement.RETURN_GENERATED_KEYS)) {

                                                insertAulaGruppoStatement.setLong(1, aula_id);
                                                insertAulaGruppoStatement.setLong(2, gruppo_id);
                                                insertAulaGruppoStatement.executeUpdate();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                throw new RESTWebApplicationException(ex);
            }
        }
        URI uri = uriinfo.getBaseUriBuilder()
                .path(AuleRes.class)
                .path(AuleRes.class, "exportAuleCsv")
                .build();
        return Response.created(uri).build();
    }

    @GET
    @Path("{id: [1-9][0-9]*}/attrezzature")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttrezzatureAula(@PathParam("id") int idAula) throws RESTWebApplicationException {
        Aula aula = new Aula();
        aula.setId(idAula);
        AulaRes aulaRes = new AulaRes(aula);

        return aulaRes.getAttrezzatureAula(idAula);
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
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    private Aula obtainConfigurazioneAula(ResultSet rs) {
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
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }
}
