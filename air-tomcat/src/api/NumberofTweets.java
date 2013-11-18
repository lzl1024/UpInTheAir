package api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import util.Constants;

@Path("q3")
public class NumberofTweets {
    // index map, key : userid, value : record index
    // public static HashMap<Long, Integer> index = new HashMap<Long,
    // Integer>();
    public static int[] index = new int[(int) Constants.INDEX_SIZE];

    // table to store the records
    public static Table[] table = new Table[Constants.SIZE];
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
            if (userid_min > UserMax || userid_min > userid_max) {
                builder.append(total);
                return builder.toString();
            }

            userid_min--;
            if (userid_max >= UserMax) {
                userid_max = UserMax;
            } else if (userid_max > Constants.MARK) {
                userid_max = Constants.MARK;
            }
            
            if (userid_min > Constants.MARK) {
                userid_min = Constants.MARK;
            }

            int maxIndex = findLast(userid_max);
            int minIndex;

            if (maxIndex != -1) {
                if (userid_min <= 0 || (minIndex = findLast(userid_min)) == -1) {
                    total = table[maxIndex].tweets;
                } else {
                    total = table[maxIndex].tweets - table[minIndex].tweets;
                }
            }
            builder.append(total);
        }
        return builder.toString();
    }

    private int findLast(long userId) {
        if (userId <= 0 || table[0].Idoffset > userId) {
            return -1;
        }
        // userMax
        if (userId / Constants.divisor >= index.length) {        
            return index[index.length - 1] - 1;
        }
        int basic = (int) (userId / Constants.divisor);
        short offset = (short) (userId % Constants.divisor);
        
        if (basic == 0) {
            for (int i = index[0]-1; i >=0; i--) {
                if(table[i].Idoffset <= offset) {
                    return i;
                }
            }
            
        } else if (index[basic] == index[basic-1]){
            return index[basic] -1;
        } else {
            for (int i = index[basic]-1; i >=index[basic-1]; i--) {
                if(table[i].Idoffset <= offset) {
                    return i;
                }
            }
            return index[basic-1] -1;
        }
        
        return -1;
    }
}
