package org.univaq.swa.framework.security;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import jakarta.ws.rs.core.UriInfo;
import java.sql.SQLException;

/**
 *
 * @author didattica
 */
@Path("auth")
public class AuthenticationRes {

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@Context UriInfo uriinfo,
            @Context ContainerRequestContext req,
            //un altro modo per ricevere e iniettare i parametri con JAX-RS...
            @FormParam("username") String username,
            @FormParam("password") String password) {
        try {
            Integer id = AuthHelpers.getInstance().authenticateUser(username, password);
            if (id != null) {
                //String authToken = AuthHelpers.getInstance().issueToken(uriinfo, id); //NO JWT
                String authToken = AuthHelpers.getInstance().issueTokenJWT(uriinfo, username); // Con JWT
                //Restituiamolo in tutte le modalità, giusto per fare un esempio...
                return Response.ok(authToken)
                        .cookie(new NewCookie.Builder("token").value(authToken).build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(UNAUTHORIZED).build();
    }

    @DELETE
    @Path("logout")
    @Logged
    public Response logout(@Context ContainerRequestContext req) throws SQLException, ClassNotFoundException {
        //proprietà estratta dall'authorization header 
        //e iniettata nella request dal filtro di autenticazione
        String token = (String) req.getProperty("token");
        AuthHelpers.getInstance().revokeToken(token);
        return Response.noContent()
                //eliminiamo anche il cookie con il token
                .cookie(new NewCookie.Builder("token").value("").maxAge(0).build())
                .build();
    }

    //Metodo per fare "refresh" del token senza ritrasmettere le credenziali
    @GET
    @Path("refresh")
    @Logged
    public Response refresh(@Context ContainerRequestContext req, @Context UriInfo uriinfo) throws SQLException, ClassNotFoundException {
        //proprietà iniettata nella request dal filtro di autenticazione
        String username = (String) req.getProperty("user");
        String token = (String) req.getProperty("token");
        // String newtoken = AuthHelpers.getInstance().issueToken(uriinfo, username, token); // NO JWT
        String newtoken = AuthHelpers.getInstance().issueTokenJWT(uriinfo, username); // con JWT
        return Response.ok(newtoken)
                .cookie(new NewCookie.Builder("token").value(newtoken).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newtoken).build();

    }
}
