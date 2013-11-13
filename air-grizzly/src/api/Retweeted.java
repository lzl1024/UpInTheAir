package api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import util.Constants;

@Path("q4")
public class Retweeted {
    /**
     * Who retweeted a tweet request
     * 
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String tweets(@QueryParam("userid") String userid) {
        StringBuilder builder = new StringBuilder(Constants.ANS_TITLE);

        // send query to database
        builder.append(userid);

        return builder.toString();
    }
}
