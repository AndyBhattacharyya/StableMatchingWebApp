package MatchingGame;


public class UserReadyUpEvent extends Event {
    private User user;
    public UserReadyUpEvent(User user) {
        //perform action associated with event
        this.user = user;
        user.isReady = true;
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
