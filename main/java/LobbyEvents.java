import MatchingGame.User;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;

import javax.swing.*;
import java.io.IOException;



@ServerEndpoint(value = "/lobby", configurator = CustomConfigurator.class)
public class LobbyEvents {
    /*
    Objective:
     */
    private String JSESSIONID;
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket lobby opened: " + session.getId());
        HandshakeRequest user_req = (HandshakeRequest) session.getUserProperties().get("handshakereq");
        String cookieHeader = user_req.getHeaders().get("Cookie").get(0);
        //Grabbing JSESSIONID
        for (String cookie : cookieHeader.split(";")) {
            cookie = cookie.trim();
            if (cookie.startsWith("JSESSIONID=")) {
                JSESSIONID = cookie.substring("JSESSIONID=".length());
                break;
            }
        }
        //Error handling
        if(JSESSIONID == null) {
            onClose(session);
            return;
        }
        //Access corresponding HttpSession to put output object
        HttpSession tmp= CreateOrJoinLobby.userSessionsMap.get(JSESSIONID);
        User usertmp = (User)tmp.getAttribute("user");
        try {
            usertmp.setOut(session.getBasicRemote());
        } catch (NullPointerException e) {
            onClose(session);
            return;
        }
        System.out.println("Set out for user: " + JSESSIONID);
        try {
            CreateOrJoinLobby.broadcastLobbyToUsers(usertmp);
        }catch(IOException e) {
            System.out.println("Error with broadcast");
        }
    }
    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket lobby closed: " + session.getId());
        //Handle the out set for User being invalid, setting it to null
        HttpSession tmp= CreateOrJoinLobby.userSessionsMap.get(JSESSIONID);
        User usertmp = (User)tmp.getAttribute("user");
        usertmp.setOut(null);
        System.out.println("Removed user from userSessionsMap: " + JSESSIONID);
    }
}