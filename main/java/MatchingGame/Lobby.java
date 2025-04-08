package MatchingGame;

import jakarta.websocket.RemoteEndpoint;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PipedReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


enum GAMESTATE {
    READYUP, SELECT, MATCHING, DISPLAY
    /*
    Encapsulate Game state transition as enums, directly modifying the Lobby object passed to it
    with methods
    Ex: A user leaving during the readyup will require less action then a user leaving during upload
     */


}


public class Lobby {

    private User host;
    private Set<User> users;
    private int usersReady;
    private int usersUploaded;
    private int maxPlayers;
    private int currentPlayers;
    private int usersSelected;
    private String lobbyName;


    private GAMESTATE gameState;

    public static void main(String[] args){
        /*
        //Testing
        User u1 = new User("Andy");
        User u2 = new User("George");
        User u3 = new User("Patrick");
        User u4 = new User("Jordan");
        new UserCreateLobbyEvent(u1, u1.toString(), 4);
        new UserJoinLobbyEvent(u2,u1);
        new UserJoinLobbyEvent(u3,u1);
        new UserJoinLobbyEvent(u4,u1);
        Lobby json_rep = u1.lobby;
        System.out.println(json_rep);
        //Regular Lobby Flow
        //User Join
        new UserCreateLobbyEvent(u1, u1.toString());
        new UserJoinLobbyEvent(u2,u1);
        new UserJoinLobbyEvent(u3,u1);
        new UserJoinLobbyEvent(u4,u1);
        //User Ready Up
        new UserReadyUpEvent(u1);
        new UserReadyUpEvent(u2);
        new UserReadyUpEvent(u3);
        new UserReadyUpEvent(u4);
        //User Upload
        new UserUploadedFileEvent(u1, null);
        new UserUploadedFileEvent(u2, null);
        new UserUploadedFileEvent(u3, null);
        new UserUploadedFileEvent(u4, null);
        //User Select
        new UserSelectedEvent(u1);
        new UserSelectedEvent(u2);
        new UserSelectedEvent(u3);
        new UserSelectedEvent(u4);
         */
    }

    public Lobby(User host, String lobbyName, int maxPlayers){
        this.host = host;
        this.lobbyName = lobbyName;
        users = new HashSet<>();
        users.add(host);
        this.maxPlayers =  maxPlayers;
        currentPlayers = 0;
        usersReady = 0;
        usersSelected = 0;
        gameState = GAMESTATE.READYUP;
        //review anonymous inner class scoping: Lobby.this construct
        EventDispatcher.registerHandler(UserCreateLobbyEvent.class, this::UserCreateLobbyEventHandler);
        EventDispatcher.registerHandler(UserJoinLobbyEvent.class, this::UserJoinLobbyEventHandler);
        EventDispatcher.registerHandler(UserReadyUpEvent.class, this::UserReadyUpEventHandler);
        EventDispatcher.registerHandler(UserLeaveLobbyEvent.class, this::UserLeaveLobbyEventHandler);
        EventDispatcher.registerHandler(UserUploadedFileEvent.class, this::UserUploadedFileEventHandler);
        EventDispatcher.registerHandler(UserSelectedEvent.class, this::UserSelectedEventHandler);
    }




    public void broadcastLobbyToUsers() throws IOException {
        for(User user: users){
            try {
                user.broadcastToUser().sendText(this.toString());
            } catch (NullPointerException e) {}
        }

    }
    public User getUserByUsername(String userfind){
        User ret = null;
        for(User user: users){
            if(user.getName().equalsIgnoreCase(userfind)){
                ret = user;
            }
        }
        return ret;
    }

    public String toString(){
/* JSON Representation of User utilizing json.simple
{
    "username": "Andy",
    "isReady": "true",
    "isUploaded": "/users/Andy"
    "hasSelected": Selection encapsulation
    "Lobby": true
}

JSON Representation of Lobby

"usersReady": true
"usersUploaded": true
"usersSelected": true
"lobbyname": Andy's Lobby
"users": [
    {
    "username": "Andy",
    "isReady": "true",
    "isUploaded": "/users/Andy"
    "hasSelected": Selection encapsulation
    "Lobby": true
    },
    {
    "username": "Andy",
    "isReady": "true",
    "isUploaded": "/users/Andy"
    "hasSelected": Selection encapsulation
    "Lobby": true
    }
]
*/
        //Not the best approach, since it is quite redundant
        JSONObject json_lobby = new JSONObject();
        json_lobby.put("lobbyName", lobbyName);
        json_lobby.put("host", host.toString());
        json_lobby.put("maxPlayers", this.maxPlayers);
        json_lobby.put("gameState", gameState.toString());
        json_lobby.put("usersSelected", usersSelected);
        json_lobby.put("usersReady", usersReady);
        json_lobby.put("currentPlayers", this.currentPlayers);
        ArrayList<JSONObject> json_users = new ArrayList<>();
        for(User user: users) {
            json_users.add(user.jsonUser());
        }
        json_lobby.put("users", json_users);
        return json_lobby.toJSONString();
    }


    //set up event handler instance method references
    public void UserSelectedEventHandler(UserSelectedEvent uEvent){
        if(uEvent.getUserLobby() == this && gameState == GAMESTATE.SELECT){
            User tmp = uEvent.getUser();
            usersSelected++;
            if(usersSelected == maxPlayers){
                gameState = GAMESTATE.MATCHING;
            }
            try{
                broadcastLobbyToUsers();
            } catch (IOException e) {}
            System.out.println(uEvent.getUser() + "has made their selection in lobby: " + this);
        }
        /*
            User tmp = uEvent.getUser();
            if(tmp.isReady() && usersReady < maxPlayers) {
                System.out.println("User " + uEvent.getUser() + " is ready in lobby " + this);
                usersReady++;
            }
            else if(!tmp.isReady() && usersReady > 0){
                System.out.println("User " + uEvent.getUser() + " is unready in lobby " + this);
                usersReady--;
            }

            if(usersReady == maxPlayers){
                gameState = GAMESTATE.SELECT;
            }
            else if(gameState == GAMESTATE.SELECT){
                gameState = GAMESTATE.READYUP;
            }
            try {
                broadcastLobbyToUsers();
            } catch(IOException e){}

         */
    }


    public void UserUploadedFileEventHandler(UserUploadedFileEvent uEvent){
        if(uEvent.getUserLobby() == this && gameState != GAMESTATE.SELECT) {
            usersUploaded++;
            System.out.println("User  " + uEvent.getUser() + "has uploaded a file");
            if (usersUploaded == maxPlayers) {
                gameState = GAMESTATE.SELECT;
                System.out.println(GAMESTATE.SELECT);
            }
        }
    }


    public void UserLeaveLobbyEventHandler(UserLeaveLobbyEvent uEvent){
        if(users.contains(uEvent.getUser())){
            users.remove(uEvent.getUser());
            if(users.isEmpty()){
                /*
                Handle "disbanding" lobby
                 */
            }
            if(uEvent.getUser() == host){
                /*
                switch lobby hosts when hosts decides to leave, so the lobby is still good
                 */
                host = users.iterator().next();
                System.out.println("User " + host + " is now the lobby host of " + this);
            }
            usersReady--;
            usersSelected--;
            System.out.println("User " + uEvent.getUser() + " left the lobby " + this);

            if (gameState != GAMESTATE.READYUP) {
                gameState = GAMESTATE.READYUP;
                System.out.println(GAMESTATE.READYUP);
            }

        }
    }

    public void UserReadyUpEventHandler(UserReadyUpEvent uEvent){
        if(this==uEvent.getUserLobby()){
            User tmp = uEvent.getUser();
            if(tmp.isReady() && usersReady < maxPlayers) {
                usersReady++;
                System.out.print("User " + uEvent.getUser() + " is ready in lobby ");
            }
            else if(!tmp.isReady() && usersReady > 0){
                usersReady--;
                if(tmp.hasSelected){
                    tmp.unsetUserSelection();
                    usersSelected--;
                }
                System.out.println("User " + uEvent.getUser() + " is unready in lobby ");
            }

            if(usersReady == maxPlayers){
                gameState = GAMESTATE.SELECT;
            }
            else if(gameState == GAMESTATE.SELECT){
                gameState = GAMESTATE.READYUP;
            }
            try {
                broadcastLobbyToUsers();
            } catch(IOException e){}
            System.out.println(this);
        }
    }
    public void UserCreateLobbyEventHandler(UserCreateLobbyEvent uEvent){
        //this check ensures that our lobby handles the associated event and not others
        if(this==uEvent.getUserLobby()){
            currentPlayers++;
            System.out.println("User " + this.host + " has created this lobby: " + this);
            try {
                broadcastLobbyToUsers();
            } catch(IOException e){}
        }
    }

    public void UserJoinLobbyEventHandler(UserJoinLobbyEvent uEvent){
        if(this==uEvent.getUserLobby()){
            if(users.size() < maxPlayers && users.add(uEvent.getUser())) {
                System.out.println("User " + uEvent.getUser() + " has joined this lobby: " + this.host);
                currentPlayers++;
                System.out.println(this);
                try {
                    broadcastLobbyToUsers();
                } catch(IOException e){}
            }
        }
    }


}
