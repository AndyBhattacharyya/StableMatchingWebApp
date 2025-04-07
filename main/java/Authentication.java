import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class Authentication {
    /*
    Ensure that every request has a user object associated with it
     */
    public static boolean isAuthenticated(HttpServletRequest req){
        if(req.getSession(false)==null){
            return false;
        }
        return true;
    }
}
