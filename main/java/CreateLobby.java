import MatchingGame.Lobby;
import MatchingGame.User;
import MatchingGame.UserCreateLobbyEvent;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.RemoteEndpoint;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CreateLobby extends HttpServlet {

    /*
    Data structure mapping lobbienames to Lobby objects
    Data structure -> JSON to display all lobbies
     */
    public static HashMap<String, User> LOBBIES = new HashMap<>();
    private static Set<RemoteEndpoint.Basic> LISTENERS = new HashSet<>();
    public static void registerListener(RemoteEndpoint.Basic listener) throws IOException {
        LISTENERS.add(listener);
        if(!LOBBIES.isEmpty()) {
            //Build json representation of lobbies
            JSONObject lobbies_rep = new JSONObject();
            ArrayList<Lobby> json_lobbies = new ArrayList<>();
            for (User user : LOBBIES.values()) {
                json_lobbies.add(user.getLobby());
            }
            lobbies_rep.put("lobbies", json_lobbies);
            listener.sendText(lobbies_rep.toJSONString());
        }
    }
    public static void unregisterListener(RemoteEndpoint.Basic listener) {
        LISTENERS.remove(listener);
    }
    public static void broadcast() throws IOException {
        //Build json representation of lobbies
        JSONObject lobbies_rep = new JSONObject();
        ArrayList<Lobby> json_lobbies = new ArrayList<>();
        for(User user : LOBBIES.values()) {
            json_lobbies.add(user.getLobby());
        }
        lobbies_rep.put("lobbies", json_lobbies);
        for(RemoteEndpoint.Basic listener : LISTENERS){
            listener.sendText(lobbies_rep.toJSONString());
        }
    }

 public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     if(!Authentication.isAuthenticated(req) || req.getSession(false).getAttribute("user")==null){
         //No session established
         HttpSession user = req.getSession(true);
         String username = req.getParameter("username");
         if(username!=null)
             user.setAttribute("user", new User(username));
     }
     //Create lobby logic here
     HttpSession session = req.getSession(false);
     String lobbyname = req.getParameter("lobbyname");
     int maxPlayers = Integer.parseInt(req.getParameter("maxPlayers"));
     User user = (User)session.getAttribute("user");
     new UserCreateLobbyEvent(user,lobbyname,maxPlayers);
     LOBBIES.put(lobbyname,user);
     broadcast();
    }
}
