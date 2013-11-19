package api;

import java.util.Arrays;

/**
 * 
 * Table to store the data
 * 
 */
public class Table {
	public long tweets;
	public byte[] retweetList;
	public short Idoffset;

	public Table(long tweets, byte[] retweetList, short Idoffset) {
		super();
		this.tweets = tweets;
		this.retweetList = retweetList;
		this.Idoffset = Idoffset;
	}

	@Override
	public String toString() {
		return "Table [tweets=" + tweets + ", retweetList="
				+ Arrays.toString(retweetList) + ", Idoffset=" + Idoffset + "]";
	}

}
