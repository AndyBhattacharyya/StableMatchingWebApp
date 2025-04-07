import MatchingGame.User;
import MatchingGame.UserCreateLobbyEvent;
import MatchingGame.UserJoinLobbyEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class JoinLobby extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if(!Authentication.isAuthenticated(req) || req.getSession(false).getAttribute("user")==null){
            //No session established
            HttpSession user = req.getSession(true);
            String username = req.getParameter("username");
            if(username!=null)
                user.setAttribute("user", new User(username));
        }
        //join lobby logic here
        HttpSession session = req.getSession(false);
        String lobbyname = req.getParameter("lobbyName");
        User user = (User) session.getAttribute("user");
        User lobby = CreateLobby.LOBBIES.get(lobbyname);
        if(lobby!=null) {
            new UserJoinLobbyEvent(user, lobby);
            CreateLobby.broadcast();
        }
    }
}
