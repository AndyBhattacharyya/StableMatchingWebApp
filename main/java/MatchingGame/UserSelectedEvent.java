package MatchingGame;


import java.util.ArrayList;
import java.util.List;

public class UserSelectedEvent extends Event{
    private User user;
    public UserSelectedEvent(User user, List<String> string_rankings) {
        //perform action associated with event
        this.user = user;
        List<User> rankings = new ArrayList<User>();
        for(String usernames:string_rankings){
            rankings.add(user.getLobby().getUserByUsername(usernames));
        }
        user.setUserSelection(rankings);
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
