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
        CreateOrJoinLobby.userSessionsMap.get(JSESSIONID).setAttribute("out", session.getBasicRemote());
        System.out.println("Set out for user: " + JSESSIONID);
    }
    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket lobby closed: " + session.getId());
        //Delete corresponding HttpSession to put output object
        CreateOrJoinLobby.userSessionsMap.remove(JSESSIONID);
        System.out.println("Removed user from userSessionsMap: " + JSESSIONID);
    }
}