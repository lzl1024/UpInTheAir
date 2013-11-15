import java.util.Comparator;

/**
 * 
 * Helper class to get a line record
 * 
 */
public class Record {
    public long userid;
    public String time;
    public long tweetid;
    public String tweet;
    public long retweet;

    public Record(long userid, String time, long tweetid, String line,
            long retweet) {
        super();
        this.userid = userid;
        this.time = time;
        this.tweetid = tweetid;
        this.tweet = line;
        this.retweet = retweet;
    }

    public Record() {
    }

    // meke record to be a string array
    public String[] makeup() {
        String[] result = {Long.toString(this.userid), time, tweet, Long.toString(retweet)};
        return result;
    }
    /**
     * 
     * Comparator to sort as userid
     * 
     */
    public static class UserPrio implements Comparator<Record> {

        @Override
        public int compare(Record rec1, Record rec2) {
            if (rec1.userid > rec2.userid) {
                return 1;
            } else if (rec1.userid < rec2.userid) {
                return -1;
            }
            return 0;
        }

    }

    /**
     * 
     * Comparator to sort as retweet id
     * 
     */
    public static class RetweetPrio implements Comparator<Record> {

        @Override
        public int compare(Record rec1, Record rec2) {
            if (rec1.retweet > rec2.retweet) {
                return 1;
            } else if (rec1.retweet < rec2.retweet) {
                return -1;
            }
            return 0;
        }

    }
    
    @Override
    public String toString() {
        return "Record [userid=" + userid + ", time=" + time + ", tweetid="
                + tweetid + ", tweet=" + tweet + ", retweet=" + retweet + "]";
    }

}
