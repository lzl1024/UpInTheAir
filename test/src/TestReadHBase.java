import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Test HBase read.
 * 
 * @author Yinsu Chu (yinsuc)
 * 
 */
public class TestReadHBase {
	public static void main(String[] args) {
		System.out.println("test begin");

		/* target row */
		final String time = "2013-10-22 09:55:43";

		/* HBase column family and column */
		final byte[] COLUMN_FAMILY = Bytes.toBytes("tweet");
		final byte[] COLUMN = Bytes.toBytes("content");

		/* set configurations */
		Configuration config = HBaseConfiguration.create();
		config.clear();

		/* master IP address */
		config.set("hbase.zookeeper.quorum", "54.205.169.96");
		config.set("hbase.zookeeper.property.clientPort", "2181");

		/* master DNS name */
		config.set("hbase.master",
				"ec2-54-205-169-96.compute-1.amazonaws.com:60000");

		HTable tweetTable = null;
		Scan scan = null;
		ResultScanner rs = null;
		byte[] timeArray = Bytes.toBytes(time);
		try {
			tweetTable = new HTable(config, "tweettable");
			scan = new Scan();
			scan.addColumn(COLUMN_FAMILY, COLUMN);

			/* scan the table */
			rs = tweetTable.getScanner(scan);
			System.out.println("scanning...");

			/* search for results */
			for (Result r = rs.next(); r != null; r = rs.next()) {
				if (Arrays.equals(r.getRow(), timeArray)) {
					System.out.println("record found: "
							+ new String(r.getValue(COLUMN_FAMILY, COLUMN)));
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
		System.out.println("test end");
	}
}
