package api;

import java.io.BufferedReader;
import java.io.FileReader;
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

    // read file
    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    Constants.FILE_LOC));
            System.out.println("Begin to Read");
            long total = 0;

            String line;

            while ((line = reader.readLine()) != null) {
                String[] element = line.split(",");
                Long userId = Long.parseLong(element[0]);
                total += Long.parseLong(element[1]);

                Table row = new Table(total, null);

                // get retweet list
                if (element.length > 2) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < element.length; i++) {
                        builder.append(element[i] + "\n");
                    }

                    row.retweetList = builder.toString();
                }

                table.add(row);
                index.put(userId, table.size() - 1);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end!");
    }

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
        long total = 0L;
        userid_min--;

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
        return builder.toString();
    }
}
