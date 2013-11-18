package api;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import util.Constants;

@Path("q3")
public class NumberofTweets {
    // index map, key : userid, value : record index
    public static HashMap<Long, Integer> index = new HashMap<Long, Integer>();
    // table to store the records
    public static ArrayList<Table> table = new ArrayList<Table>();
    // max userid
    public static Long UserMax = 0L;

    /**
     * Number of Tweets request
     * 
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String tweets(@QueryParam("userid_min") Long userid_min,
            @QueryParam("userid_max") Long userid_max) {
        StringBuilder builder = new StringBuilder(Constants.ANS_TITLE);

        if (userid_min != null && userid_max != null) {
            long total = 0L;
            if (userid_min > UserMax) {
                builder.append(total);
                return builder.toString();
            }
            
            userid_min--;
            if (userid_max > UserMax) {
                userid_max = UserMax;
            }
            
    
            // send query to cache
            while (userid_min > 0 && !index.containsKey(userid_min)) {
                userid_min--;
            }
    
            while (userid_max > 0 && userid_min < userid_max && !index.containsKey(userid_max)) {
                userid_max--;
            }
    
            if (userid_min < userid_max && userid_max > 0) {
                if (userid_min <= 0) {
                    total = table.get(index.get(userid_max)).tweets;
                }else {
                    total = table.get(index.get(userid_max)).tweets
                            - table.get(index.get(userid_min)).tweets;
                }
            }
            builder.append(total);
        }
        return builder.toString();
    }
}
