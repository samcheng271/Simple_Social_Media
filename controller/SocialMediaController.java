package Controller;

import java.sql.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Util.ConnectionUtil;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        // app.get("example-endpoint", this::exampleHandler);
        ObjectMapper objectMapper = new ObjectMapper();

        // Register
        app.post("register", ctx -> {
            String Json = ctx.body();
            Account account = objectMapper.readValue(Json, Account.class);
            // Check if username and password are valid and if account already exists in database
            if(account.getUsername().isEmpty() || account.getPassword().length() < 4 || accountExists(account.getUsername())) {
                ctx.status(400);
            } else {
                addAccount(account.getUsername(), account.getPassword());
                ctx.status(200);
                account.setAccount_id(0);
                ctx.json(account);
            }
        });


        return app;
    }

    // /**
    //  * This is an example handler for an example endpoint.
    //  * @param context The Javalin Context object manages information about both the HTTP request and response.
    //  */
    // private void exampleHandler(Context context) {
    //     context.json("sample text");
    // }

    // add account to database
    private void addAccount(String username, String password) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("insert into account (username, password) values (?, ?);");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean accountExists(String username) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from account where username = ?;");
            ps.setString(1, username);  
            ResultSet rs = ps.executeQuery();
            // System.out.println("checker: "+rs.getString("username"));
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // view all values in database
    private void viewDatabase() {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from account;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                System.out.println(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}