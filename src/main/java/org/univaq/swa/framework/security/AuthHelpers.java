package org.univaq.swa.framework.security;

import jakarta.ws.rs.core.UriInfo;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * Una classe di utilità di supporto all'autenticazione qui tutto è finto, non
 * usiamo JWT o altre tecnologie
 *
 */
public class AuthHelpers {

    private static AuthHelpers instance = null;
    private final JWTHelpers jwtInstance;
    private static final String DS_NAME = "java:comp/env/jdbc/auleweb";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DS_NAME);
        return ds.getConnection();
    }

    public AuthHelpers() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        jwtInstance = JWTHelpers.getInstance();
    }

    // aggiungere hash
    public Integer authenticateUser(String username, String password) {
        String authenticate_query = "SELECT id FROM admin where username = ? and password = ?;";
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(authenticate_query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, (String) username);
            ps.setString(2, (String) password);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    return id;
                }
            }
        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String issueToken(UriInfo context, Integer id) {
        String add_token = "UPDATE admin SET token=? WHERE id = ?";
        String token = UUID.randomUUID().toString();
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(add_token, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(2, id);
            ps.setString(1, token);
            ps.executeUpdate();
            return token;
        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String issueTokenJWT(UriInfo context, String username) {
        String add_token = "UPDATE admin SET token=? WHERE username = ?";
        String token = jwtInstance.issueToken(context, username);
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(add_token, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(2, username);
            ps.setString(1, token);
            ps.executeUpdate();
            return token;

        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String issueToken(UriInfo context, String username, String oldtoken) {
        String add_token = "UPDATE admin SET token=? WHERE username = ? and token = ?";
        String token = UUID.randomUUID().toString();
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(add_token, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(2, username);
            ps.setString(3, oldtoken);
            ps.setString(1, token);
            ps.executeUpdate();
            return token;
        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /* invalida il token */
    public void revokeToken(String token) {
        String revoke_token = "UPDATE admin SET token=null WHERE token = ?";
        String safe_updates = "SET SQL_SAFE_UPDATES = 0";
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(revoke_token, Statement.RETURN_GENERATED_KEYS)) {
            PreparedStatement su = con.prepareStatement(safe_updates);
            su.execute();

            ps.setString(1, token);

            ps.executeUpdate();
        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        }
    }

    // CHECK del tipo: select username from admin where token = ?;
    public String validateToken(String token) {
        String validate_token = "SELECT username from admin where token = ?";
        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(validate_token)) {
            ps.setString(1, token);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; //lo username andrebbe derivato dal token!
    }

    public static AuthHelpers getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new AuthHelpers();
        }
        return instance;
    }

}
