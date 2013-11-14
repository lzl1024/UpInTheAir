package api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import util.Constants;

@Path("q3")
public class NumberofTweets {
    /**
     * Number of Tweets request
     * 
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String tweets(@QueryParam("userid_min") String userid_min,
            @QueryParam("userid_max") String userid_max) {
        StringBuilder builder = new StringBuilder(Constants.ANS_TITLE);

        // send query to database
        for (int i = 0; i < 10000; i++) {
        builder.append(userid_min);
        builder.append("\n");
        builder.append(userid_max);
        }

        return builder.toString();
    }
}