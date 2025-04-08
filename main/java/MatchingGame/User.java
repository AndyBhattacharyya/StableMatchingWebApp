package MatchingGame;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jakarta.websocket.RemoteEndpoint;
import org.json.simple.*;

public class User {
    private String name;
    boolean isReady;
    boolean hasSelected;
    List<User> selection;
    boolean isMale;
    Lobby lobby;
    File userUploadedFile;
    RemoteEndpoint.Basic out;

    public User(String name, boolean isMale, File userUploadedFile){
        this.name=name;
        this.isReady=false;
        this.hasSelected=false;
        this.lobby = null;
        this.userUploadedFile = userUploadedFile;
        this.isMale=isMale;
        this.out = null;
        this.selection = null;
    }
    public void setUserSelection(List<User> selection){
        this.selection = selection;
        hasSelected=true;
    }
    public void unsetUserSelection(){
        this.selection = null;
        hasSelected=false;
    }
    public String getName(){
        return this.name;
    }

    public boolean getGender(){
        return isMale;
    }
    public void readyUp(){
        isReady = !isReady;
    }
    public boolean isReady(){
        return isReady;
    }
    public void setOut(RemoteEndpoint.Basic out){
        this.out=out;
    }
    public RemoteEndpoint.Basic broadcastToUser() throws NullPointerException{
        if(out==null){
           throw new NullPointerException("You have to set the out");
        }
        return out;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setGender(boolean isMale) {
        this.isMale = isMale;
    }
    public File getUserUploadedFile() {
        return this.userUploadedFile;
    }

    public JSONObject jsonUser(){
        /*
        Return json representation of user object
        encode representation into json string
        String -> string
        Boolean -> true|false
        null -> null
        List -> array ~ selection
         */
        JSONObject json_user = new JSONObject();
        json_user.put("username", this.name);
        json_user.put("isMale", this.isMale);
        json_user.put("isReady", this.isReady);
        json_user.put("hasSelected", this.hasSelected);
        json_user.put("userimage", "/uploads/"+userUploadedFile.getName());
        return json_user;
    }
    public String toString(){
        return this.name;
    }
    public Lobby getLobby(){
        return this.lobby;
    }

}
