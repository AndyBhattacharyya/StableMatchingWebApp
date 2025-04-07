import jakarta.websocket.*;
import jakarta.websocket.server.*;

import java.io.IOException;

@ServerEndpoint("/lobbies")
public class LobbyDisplay {
    /*
    Objective:
    (1) We need a way to send lobby updates upon event changes to users: RemoteEndpoint
    (2) This actual contents of the message will be JSON representation of the game-lobby state ~ encoders
    (3) Error handling
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
        try {
            CreateOrJoinLobby.registerListener(session.getBasicRemote());
        } catch(IOException e) {
            System.out.println("Failed to register");
        }



    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("Received: " + message);
        //session.getBasicRemote().sendText("Echo: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket closed: " + session.getId());
        CreateOrJoinLobby.unregisterListener(session.getBasicRemote());
    }
}
