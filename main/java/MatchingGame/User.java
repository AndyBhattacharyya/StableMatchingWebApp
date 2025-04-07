package MatchingGame;



import java.io.File;
import org.json.simple.*;

public class User {
    private String name;
    boolean isReady;
    boolean isUploading;
    boolean hasSelected;
    Lobby lobby;
    File userUploadedFile;

    public User(String name){
        this.name=name;
        this.isReady=false;
        this.isUploading=false;
        this.hasSelected=false;
        this.lobby = null;
        this.userUploadedFile = null;
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
        json_user.put("isReady", this.isReady);
        json_user.put("hasSelected", this.hasSelected);
        json_user.put("isUploading", this.isUploading);
        return json_user;
    }
    public String toString(){
        return this.name;
    }
    public boolean hasUploadedFile(){
        return this.userUploadedFile!=null;
    }
    public Lobby getLobby(){
        return this.lobby;
    }
}
