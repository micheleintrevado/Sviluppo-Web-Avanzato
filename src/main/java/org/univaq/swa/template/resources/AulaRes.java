/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.univaq.swa.framework.model.Aula;

/**
 *
 * @author miche
 */

public class AulaRes {
    
    private final Aula a;

    public AulaRes(Aula aula) {
        // TODO prendere l'aula dal DB e crearla (oggetto java)
        this.a = aula;
    }
    
    private static final String DS_NAME = "java:comp/env/jdbc/auleweb";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }
    
    // 4 TODO
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignGruppo(@PathParam("nome") String nome){
        return Response.ok().build();
    }
    
    // 5 TODO
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoAula(){
        return Response.ok(a).build();
    }
    
    // 6 TODO
    @GET
    @Path("attrezzature")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttrezzatureAula(@PathParam("nome") String nome){
        return Response.ok().build();
    }
}
