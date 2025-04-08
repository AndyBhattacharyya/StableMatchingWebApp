import MatchingGame.User;
import MatchingGame.UserReadyUpEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class Ready extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        //Session validation
        HttpSession session = req.getSession(false);
        User user;
        if(session == null || (user =(User) session.getAttribute("user")) ==null){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.sendRedirect("/index.html");
        }
        else{
            new UserReadyUpEvent(user);
            boolean isMale = user.getGender();
            res.setContentType("application/json");
            res.getWriter().println("{\"isMale\": " + isMale + "}");
        }
    }
}
