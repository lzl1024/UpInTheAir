package api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import util.Constants;

@Path("q2")
public class TextofTweets {

	/* HBase table, column family and column */
	private static final byte[] TABLE = Bytes.toBytes("tweettable");
	private static final byte[] COLUMN_FAMILY = Bytes.toBytes("tweet");
	private static final byte[] COLUMN = Bytes.toBytes("content");

	private static Configuration config = null;

	static {

		/* set configurations */
		config = HBaseConfiguration.create();
		config.clear();

		/* master IP address */
		config.set("hbase.zookeeper.quorum", "54.205.124.206");
		config.set("hbase.zookeeper.property.clientPort", "2181");

		/* master DNS name */
		config.set("hbase.master",
				"ec2-54-205-124-206.compute-1.amazonaws.com:60000");
	}

	/**
	 * TextofTweets request
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String tweets(@QueryParam("time") String time) {

		/* edge cases */
		if (time == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder(Constants.ANS_TITLE);

		HTable tweetTable = null;
		try {
			tweetTable = new HTable(config, TABLE);
		} catch (Exception ex) {
			System.out.println("failed to connect to hbase");
			return null;
		}

		Result result = null;
		byte[] timeArray = Bytes.toBytes(time);
		Get get = new Get(timeArray);

		/* get tweet from hbase */
		try {
			result = tweetTable.get(get);
		} catch (Exception ex) {
			System.out.println("failed to retrieve result from hbase");
			try {
				tweetTable.close();
			} catch (Exception nestedEx) {
				System.out.println("failed to close connection to hbase");
			}
			return null;
		}

		builder.append(new String(result.getValue(COLUMN_FAMILY, COLUMN)));

		try {
			tweetTable.close();
		} catch (Exception nestedEx) {
			System.out.println("failed to close connection to hbase");
		}

		return builder.toString();
	}
}
