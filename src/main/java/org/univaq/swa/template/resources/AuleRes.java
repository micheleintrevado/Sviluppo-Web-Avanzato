package org.univaq.swa.template.resources;

import jakarta.servlet.ServletContext;
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
import java.io.BufferedWriter;
import java.net.URI;
import java.nio.file.Files;
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
import org.univaq.swa.framework.model.Aula;
import org.univaq.swa.framework.security.Logged;
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
    private static final String QUERY_SELECT_CONFIGURAZIONE_AULE = "select aula.id, aula.nome as nome_aula, luogo, edificio, piano, capienza, email_responsabile, prese_elettriche, prese_rete, note, gruppo.nome as nome_gruppo from aula join aula_gruppo on aula.id = aula_gruppo.id_aula join gruppo on aula_gruppo.id_gruppo = gruppo.id";
    

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }
    
    public AuleRes() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }
    
    @GET
    @Path("{id: [1-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAula(@PathParam("id") int idAula)throws RESTWebApplicationException {
        Aula aula = new Aula();
        aula.setId(idAula);
        AulaRes aulaRes = new AulaRes(aula);
        
        return aulaRes.getInfoAula(idAula);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id: [1-9]+}")
    public Response assignGruppo(@Context UriInfo uriinfo, @PathParam("id") int idAula, HashMap<String,Object> gruppo)throws RESTWebApplicationException {
        Aula aula = new Aula();
        aula.setId(idAula);
        AulaRes aulaRes = new AulaRes(aula);
        
        return aulaRes.assignGruppoAula(uriinfo,idAula,gruppo);
    }
    
    // 3
    @POST
    @Logged
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAula(@Context UriInfo uriinfo, HashMap<String, Object> evento){
        String addAulaQuery = "INSERT INTO `aula` (`nome`, `luogo`, `edificio`, `piano`, `capienza`, `email_responsabile`, `prese_elettriche`, `prese_rete`, `note`) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)";
        try ( Connection con = getPooledConnection(); PreparedStatement ps = con.prepareStatement(addAulaQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, (String) evento.get("nome"));
            ps.setString(2, (String) evento.get("luogo"));
            ps.setString(3, (String) evento.get("edificio"));
            ps.setString(4, (String) evento.get("piano"));
            ps.setInt(5, (int) evento.get("capienza"));
            ps.setString(6, (String) evento.get("email_responsabile"));
            ps.setInt(7, (int) evento.get("prese_elettriche"));
            ps.setInt(8, (int) evento.get("prese_rete"));
            ps.setString(9, (String) evento.get("note"));
            
            ps.executeUpdate();
            
            try ( ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                URI uri = uriinfo.getBaseUriBuilder()
                        .path(AuleRes.class)
                        .path(AuleRes.class, "getAula")
                        .build(id);
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
    
    // 2
    @GET
    @Path("csv")
    @Produces("text/csv")
    public Response exportAuleCsv(){
        try {
            ArrayList<HashMap<String, Object>> configurazioneAule = new ArrayList<HashMap<String,Object>>();
            
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_CONFIGURAZIONE_AULE)) {
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
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
            
            //here we build the csv file
            String path = servletContext.getRealPath("") + "\\csv\\configurazioneAule.csv";
            //BufferedWriter writer = Files.newBufferedWriter()
            
            return Response.ok().build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        } catch (NamingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // 2
    @POST
    @Path("csv")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importAuleCsv(){
        return Response.ok().build();
    }
    
    @GET
    @Path("{id: [1-9]+}/attrezzature")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttrezzatureAula(@PathParam("id") int idAula)throws RESTWebApplicationException {
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
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
