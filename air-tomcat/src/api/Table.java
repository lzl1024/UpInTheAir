package api;

/**
 * 
 * Table to store the data
 *
 */
public class Table {
    public Long tweets;
    public String retweetList;

    public Table(Long tweets, String retweetList) {
        super();
        this.tweets = tweets;
        this.retweetList = retweetList;
    }

}
