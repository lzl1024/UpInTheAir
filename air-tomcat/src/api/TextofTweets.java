package api;

import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import util.Constants;

@Path("q2")
public class TextofTweets {
	/**
	 * TextofTweets request
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String tweets(@QueryParam("time") String time) {
		final byte[] COLUMN_FAMILY = Bytes.toBytes("tweet");
		final byte[] COLUMN = Bytes.toBytes("content");
		StringBuilder builder = new StringBuilder(Constants.ANS_TITLE);

		Configuration config = HBaseConfiguration.create();
		config.clear();
		config.set("hbase.zookeeper.quorum",
				"ec2-50-16-144-110.compute-1.amazonaws.com");
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.master",
				"ec2-50-16-144-110.compute-1.amazonaws.com:60000");
		HTable tweetTable = null;
		Scan scan = null;
		ResultScanner rs = null;
		byte[] timeArray = Bytes.toBytes(time);
		try {
			tweetTable = new HTable(config, "tweettable");
			scan = new Scan();
			scan.addColumn(COLUMN_FAMILY, COLUMN);
			rs = tweetTable.getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {
				if (Arrays.equals(r.getRow(), timeArray)) {
					builder.append(new String(r.getValue(COLUMN_FAMILY, COLUMN)));
					break;
				}
			}
		} catch (Exception ex) {
			System.err.println("failed to retrieve result from hbase");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (tweetTable != null) {
				try {
					tweetTable.close();
				} catch (Exception ex) {
					System.err.println("failed to close hbase table");
				}
			}
		}

		return builder.toString();
	}
}
