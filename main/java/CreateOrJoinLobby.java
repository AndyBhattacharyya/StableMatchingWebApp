import MatchingGame.Lobby;
import MatchingGame.User;
import MatchingGame.UserCreateLobbyEvent;
import MatchingGame.UserJoinLobbyEvent;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.websocket.RemoteEndpoint;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CreateOrJoinLobby extends HttpServlet {

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
    private int fileUploads = 0;
    private File UPLOAD_DIRECTORY;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //ensure directory is always empty
        UPLOAD_DIRECTORY = new File(getServletContext().getRealPath("") + "/" + "uploads");
        UPLOAD_DIRECTORY.mkdir();
    }

     public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //Parameters of request made by frontend
     String hostName = null;
     boolean isMale = false;
     String lobbyName = null;
     int maxPlayers = -1;
     File userFile;
     /*
     User required information to create a session
     hostName
     hostGender
     lobbyName
     maxPlayers
     userFile
      */
     HttpSession userSession = req.getSession(true);
     User user;
     //Creating a user for session
     if((user = (User)userSession.getAttribute("user")) == null) {
         userFile = new File(UPLOAD_DIRECTORY, "image"+ ++fileUploads +".png");
         for(Part part : req.getParts()) {
             //look into ways to better handle the possible null pointer exception
             if (part.getContentType() != null && part.getContentType().equals("image/png")) {
                 //begin processing file
                 try (
                         InputStream img_data = part.getInputStream();
                         FileOutputStream fos = new FileOutputStream(userFile)
                 ) {
                     while (img_data.available() > 0) {
                         fos.write(img_data.read());
                         fos.flush();
                     }
                 } catch (IOException e) {
                     System.out.println(e);
                 }
             } else if ((hostName = part.getHeader("hostName")) != null) {
             } else if (part.getHeader("hostGender") != null) {
                 isMale = part.getHeader("hostGender").equalsIgnoreCase("male");
             } else if ((lobbyName = part.getHeader("lobbyName")) != null) {
             } else if (part.getHeader("maxPlayers") != null) {
                 try {
                     maxPlayers = Integer.parseInt(part.getHeader("maxPlayers"));
                 } catch (NumberFormatException e) {
                     maxPlayers = -1;
                 }
             } else {
                 //Error with request, notify
                 res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                 return;
             }
         }
         if(hostName!=null && lobbyName!=null &&maxPlayers!=-1) {
             userSession.setAttribute("user",new User(hostName, isMale, userFile));
         }
         else {
             //Error with request, notify
             res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             return;
         }


     }
     //Updating a user for session
     else{

         for(Part part : req.getParts()) {
             //look into ways to better handle the possible null pointer exception
             if (part.getContentType() != null && part.getContentType().equals("image/png")) {
                 //begin updating file
                 try (
                         InputStream img_data = part.getInputStream();
                         //Ensure that this overwrites
                         FileOutputStream fos = new FileOutputStream(user.getUserUploadedFile())
                 ) {
                     while (img_data.available() > 0) {
                         fos.write(img_data.read());
                         fos.flush();
                     }
                 } catch (IOException e) {
                     System.out.println(e);
                 }
             } else if ((hostName = part.getHeader("hostName")) != null) {
             } else if (part.getHeader("hostGender") != null) {
                 isMale = part.getHeader("hostGender").equalsIgnoreCase("male");
             } else if ((lobbyName = part.getHeader("lobbyName")) != null) {
             } else if (part.getHeader("maxPlayers") != null) {
                 try {
                     maxPlayers = Integer.parseInt(part.getHeader("maxPlayers"));
                 } catch (NumberFormatException e) {
                     maxPlayers = -1;
                 }
             } else {
                 //Error with request, notify
                 res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                 return;
             }
         }
         if(hostName!=null && lobbyName!=null) {
             user.setGender(isMale);
             user.setName(hostName);
         }
         else {
             //Error with request, notify
             res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             return;
         }
     }
     //Join or Create lobby logic here depending on path
         String action = req.getServletPath().substring(1);
     if(action.equalsIgnoreCase("create")) {
         //create lobby logic here
         new UserCreateLobbyEvent(user, lobbyName, maxPlayers);
         LOBBIES.put(lobbyName, user);
     }
     else if(action.equalsIgnoreCase("join")) {
         //join lobby logic here
         User lobby = LOBBIES.get(lobbyName);
         new UserJoinLobbyEvent(user, lobby);
     }
         broadcast();
         res.sendRedirect("/lobby.html");
    }
}
