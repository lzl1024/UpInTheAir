package api;

/**
 * 
 * Table to store the data
 *
 */
public class Table {
    public Long userId;
    public Long tweets;
    public String retweetList;

    public Table(Long userId, Long tweets, String retweetList) {
        super();
        this.userId = userId;
        this.tweets = tweets;
        this.retweetList = retweetList;
    }

}
