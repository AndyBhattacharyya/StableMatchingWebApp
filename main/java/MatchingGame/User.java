package MatchingGame;



import java.io.File;
import org.json.simple.*;

public class User {
    private String name;
    boolean isReady;
    boolean isUploading;
    boolean hasSelected;
    boolean isMale;
    Lobby lobby;
    File userUploadedFile;

    public User(String name, boolean isMale, File userUploadedFile){
        this.name=name;
        this.isReady=false;
        this.isUploading=false;
        this.hasSelected=false;
        this.lobby = null;
        this.userUploadedFile = userUploadedFile;
        this.isMale=isMale;
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
