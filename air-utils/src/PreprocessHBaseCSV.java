import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.csvreader.CsvReader;

/**
 * Process CSV files into key-value pairs. Seems no longer needed since we are
 * loading CSV files on S3 directly into HBase.
 * 
 * @author Yinsu Chu (yinsuc)
 * 
 */
public class PreprocessHBaseCSV {
	private static final String CSV_FILE_NAME = "/Users/yinsuchu/Downloads/time_text_total.csv";
	private static final String[] HEADER = { "time", "tweet" };
	private static final String OUTPUT_FILE_NAME = "key-value-time-tweet-unmerged";

	public static void main(String[] args) throws IOException {
		CsvReader reader = new CsvReader(new InputStreamReader(
				new FileInputStream(CSV_FILE_NAME)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				OUTPUT_FILE_NAME)));
		reader.setHeaders(HEADER);
		while (reader.readRecord()) {
			String rawTime = reader.get("time");
			String time = URLEncoder.encode(rawTime, "UTF-8");
			String verifyTime = URLDecoder.decode(time, "UTF-8");
			String rawTweet = reader.get("tweet");
			String tweet = URLEncoder.encode(rawTweet, "UTF-8");
			String verifyTweet = URLDecoder.decode(tweet, "UTF-8");
			String line = time + "\t" + tweet + "\n";
			bw.write(line);
			System.out.println(line);
		}
		bw.close();
		reader.close();
	}
}
