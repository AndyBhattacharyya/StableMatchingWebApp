package MatchingGame;


public class UserJoinLobbyEvent extends Event {
    private User user;
    public UserJoinLobbyEvent(User user, User userLobby) {
        //perform action associated with event
        this.user = user;
        user.lobby = userLobby.lobby;
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
