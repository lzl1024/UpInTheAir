import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Load S3 data (time and tweet) into HBase. tweet = tweetId : tweetText
 * 
 * @author Yinsu Chu (yinsuc)
 * 
 */
public class LoadHBase {

	/**
	 * The map class: (1) parse JSON (2) get time, tweetId, tweetText.
	 * 
	 * @author Yinsu Chu (yinsuc)
	 * 
	 */
	public static class Map extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, Text> {

		/* output key to reducer */
		private Text outputTime = new Text();

		/* output value to reducer */
		private Text tweet = new Text();

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.hadoop.mapred.Mapper#map(java.lang.Object,
		 * java.lang.Object, org.apache.hadoop.mapred.OutputCollector,
		 * org.apache.hadoop.mapred.Reporter)
		 */
		@Override
		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {

			/* string representation of JSON */
			String json = value.toString();
			if (json.length() <= 0) {
				return;
			}

			/* parse JSON */
			JsonParser jParser = new JsonParser();
			JsonElement jElement = jParser.parse(json);

			/* get tweet ID */
			String tweetId = jElement.getAsJsonObject().get("id_str")
					.getAsString();

			/* get time */
			String time = jElement.getAsJsonObject().get("created_at")
					.getAsString();

			/* transform time into the format in query 2 */
			Date date = null;
			try {
				date = new SimpleDateFormat("EEE MMM dd HH:mm:ss '+0000' yyyy")
						.parse(time);
			} catch (ParseException ex) {
				System.err.println("failed to parse date: " + time);
			}
			String q2Time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
					date).toString();

			/* get tweet text */
			String tweetText = jElement.getAsJsonObject().get("text")
					.getAsString();

			outputTime.set(q2Time);
			tweet.set(tweetId + ":" + tweetText);

			output.collect(outputTime, tweet);
		}
	}

	/**
	 * The reduce class: (1) remove duplicate tweets (2) sort tweets.
	 * 
	 * @author Yinsu Chu (yinsuc)
	 * 
	 */
	public static class Reduce extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {

		/* table attributes */
		private static final String TABLE_NAME = "tweettable";
		private static final String COLUMN_FAMILY = "tweet";
		private static final String COLUMN = "content";

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.hadoop.mapred.Reducer#reduce(java.lang.Object,
		 * java.util.Iterator, org.apache.hadoop.mapred.OutputCollector,
		 * org.apache.hadoop.mapred.Reporter)
		 */
		@Override
		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {

			/* the name space of tweet ID */
			HashSet<String> tweetIdSpace = new HashSet<String>();

			/* all unique tweets */
			ArrayList<String> tweets = new ArrayList<String>();

			while (values.hasNext()) {

				/* find colon in the value */
				String tweet = values.next().toString();
				int colonIndex = tweet.indexOf(":");
				if (colonIndex == -1) {
					System.err.println("format error: " + tweet);
				}

				/* get tweet ID to test duplication */
				String tweetId = tweet.substring(0, colonIndex);

				if (!tweetIdSpace.contains(tweetId)) {
					tweetIdSpace.add(tweetId);
					tweets.add(tweet);
				}
			}

			/* sort the tweets */
			Collections.sort(tweets);

			/* generate output to HBase */
			StringBuffer outputTweet = new StringBuffer();
			outputTweet.append(tweets.get(0));
			for (int i = 1; i < tweets.size(); i++) {
				outputTweet.append("\n" + tweets.get(i));
			}

			/* write to HBase table */
			Configuration config = HBaseConfiguration.create();
			HTable table = new HTable(config, TABLE_NAME);
			Put put = new Put(Bytes.toBytes(key.toString()));
			put.add(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(COLUMN),
					Bytes.toBytes(outputTweet.toString()));
			table.put(put);
			table.close();
		}
	}

	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(LoadHBase.class);
		conf.setJobName("LoadHBase");
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapperClass(Map.class);
		conf.setReducerClass(Reduce.class);
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		JobClient.runJob(conf);
	}
}
