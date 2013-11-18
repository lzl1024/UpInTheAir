package api;

/**
 * 
 * Table to store the data
 *
 */
public class Table {
    public long tweets;
    public String retweetList;
    public short Idoffset;

    public Table(long tweets, String retweetList, short Idoffset) {
        super();
        this.tweets = tweets;
        this.retweetList = retweetList;
        this.Idoffset = Idoffset;
    }

}
