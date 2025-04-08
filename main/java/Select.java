import MatchingGame.User;
import MatchingGame.UserReadyUpEvent;
import MatchingGame.UserSelectedEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Select extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        //Session validation
        HttpSession session = req.getSession(false);
        User user;
        if(session == null || (user =(User) session.getAttribute("user")) ==null){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.sendRedirect("/index.html");
        }
        else{
            if("application/json".equals(req.getContentType())){
                StringBuilder jsonstring = new StringBuilder();
                String line;

                try (BufferedReader reader = req.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonstring.append(line);
                    }
                }

                JSONObject obj = (JSONObject) JSONValue.parse(jsonstring.toString());
                JSONArray arr = (JSONArray) obj.get("rankings");
                //Runtime cast YOLO, figure out a better way later
                List<String> rankings = arr;
                new UserSelectedEvent(user, rankings);
            }
            else{
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
    public static void main (String args[]){
        String jsonstring = "{\"rankings\": [\"Andy\",\"George\"]}";
        JSONObject obj = (JSONObject) JSONValue.parse(jsonstring);
        JSONArray arr = (JSONArray) obj.get("rankings");
        List<String> rankings = arr;
        System.out.println(rankings.get(0));

    }



}
