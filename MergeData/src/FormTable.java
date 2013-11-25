import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * Get the table from origin csv file, the outfile is sort by userid
 * 
 */
public class FormTable {
    public static class Table {
        public long numOfTweet;
        public TreeSet<Long> retweetList;

        public Table() {
            super();
            this.numOfTweet = 0;
            this.retweetList = new TreeSet<Long>();
        }
    }

    public static void main(String[] args) throws Exception {
        String inputfile = "new_table.csv";
        String outputfile = "output_total_new.csv";

        TreeMap<Long, Table> userTable = new TreeMap<Long, Table>();

        BufferedReader reader = new BufferedReader(new FileReader(inputfile));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] element = line.split(",");
            Long userid = Long.parseLong(element[0]);
            Long retweet = Long.parseLong(element[1]);

            // update number of tweets
            Table row;
            if (!userTable.containsKey(userid)) {
                row = new Table();
            } else {
                row = userTable.get(userid);
            }
            row.numOfTweet++;
            userTable.put(userid, row);

            // update retweet list
            if (retweet != -1L) {
                if (!userTable.containsKey(retweet)) {
                    row = new Table();
                } else {
                    row = userTable.get(retweet);
                }
                row.retweetList.add(userid);
                userTable.put(retweet, row);
            }
        }

        // output csv
        PrintWriter writer = new PrintWriter(new FileWriter(outputfile));
        for(Entry<Long, Table> entry : userTable.entrySet()) {
            StringBuilder builder = new StringBuilder(entry.getKey().toString());
            builder.append(",");
            builder.append(entry.getValue().numOfTweet);
            
            for (Long retweet : entry.getValue().retweetList) {
                builder.append(",");
                builder.append(retweet);
            }
            
            writer.println(builder.toString());
        }
        writer.flush();
        writer.close();
        reader.close();
    }
}
