package MatchingGame;


public class UserSelectedEvent extends Event{
    private User user;
    public UserSelectedEvent(User user) {
        //perform action associated with event
        this.user = user;
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
