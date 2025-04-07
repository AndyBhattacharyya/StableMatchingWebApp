package MatchingGame;

public class UserCreateLobbyEvent extends Event{

    private User user;
    public UserCreateLobbyEvent(User user, String lobbyname, int maxPlayer) {
        //perform action associated with event
        this.user = user;
        user.lobby = new Lobby(user, lobbyname, maxPlayer);
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
