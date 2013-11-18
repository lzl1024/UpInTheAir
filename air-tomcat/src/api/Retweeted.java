package api;

import java.io.UnsupportedEncodingException;

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
    public String tweets(@QueryParam("userid") Long userid) {
        StringBuilder builder = new StringBuilder(Constants.ANS_TITLE);
        int real;

        // send query to database
        if (userid != null && (real = findKey(userid)) != -1) {
            String ans = null;
            try {
                ans = new String(NumberofTweets.table[real].retweetList, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (ans != null) {
                builder.append(ans);
            }
        }

        return builder.toString();
    }

    private int findKey(Long userid) {
        if (userid == NumberofTweets.UserMax) {
            return NumberofTweets.index.length - 1;
        }

        if (userid / Constants.divisor >= NumberofTweets.index.length) {
            return -1;
        }

        int basic = (int) (userid / Constants.divisor);
        short offset = (short) (userid % Constants.divisor);

        if (basic == 0) {
            for (int i = NumberofTweets.index[0] - 1; i >= 0; i--) {
                if (NumberofTweets.table[i].Idoffset == offset) {
                    return i;
                } else if (NumberofTweets.table[i].Idoffset < offset) {
                    return -1;
                }
            }

        } else if (NumberofTweets.index[basic] == NumberofTweets.index[basic - 1]) {
            return -1;
        } else {
            for (int i = NumberofTweets.index[basic] - 1; i >= NumberofTweets.index[basic - 1]; i--) {
                if (NumberofTweets.table[i].Idoffset == offset) {
                    return i;
                } else if (NumberofTweets.table[i].Idoffset < offset) {
                    return -1;
                }
            }
        }

        return -1;
    }
}
