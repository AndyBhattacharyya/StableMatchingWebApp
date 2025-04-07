package MatchingGame;

import java.io.File;

public class UserUploadedFileEvent extends Event{

    private User user;
    public UserUploadedFileEvent(User user, File userPicture) {
        //perform action associated with event
        this.user = user;
        user.userUploadedFile = userPicture;
        //Dispatch our own event
        EventDispatcher.dispatch(this);
    }
    //useful aux methods for events
    public Lobby getUserLobby(){
        return user.lobby;
    }

    public User getUser() {
        return user;
    }
}
