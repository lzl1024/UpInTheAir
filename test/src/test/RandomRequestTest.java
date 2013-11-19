package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RandomRequestTest {
	private static final String FILE_LOC = "/Users/yinsuchu/Downloads/output_total.csv";
	private static final int BUFFER_SIZE = 100;
	private static final long USER_MAX = 2148717488L;
	/* the real max user ID: 4503597479886598 */

	private static ArrayList<UserInfo> userTable;

	private static class UserInfo {
		private long userId;
		private long cumulativeTweets;

		public UserInfo(long userId, long cumulativeTweets, byte[] retweetList) {
			this.userId = userId;
			this.cumulativeTweets = cumulativeTweets;
		}
	}

	/**
	 * Generate q3 request URL.
	 * 
	 * @param userId1
	 *            The start user ID.
	 * @param userId2
	 *            The end user ID.
	 * @return Q3 request URL.
	 */
	private static String q3Request(long userId1, long userId2) {
		return "http://localhost:8080/air/q3?userid_min=" + userId1
				+ "&userid_max=" + userId2;
	}

	/**
	 * Generate q3 result based on local data. Must: userId1 <= userId2.
	 * 
	 * @param userId1
	 *            The start user ID.
	 * @param userId2
	 *            The end user ID.
	 * @return Q3 result.
	 */
	private static long q3Verify(long userId1, long userId2) {
		int i = userTable.size() - 1;
		int j = userTable.size() - 1;
		long number1 = 0L;
		long number2 = 0L;

		/* fine nearest user ID to userId1 */
		while (i >= 0 && userTable.get(i).userId > userId1) {
			i--;
		}
		if (i >= 0) {
			if (userTable.get(i).userId == userId1) {
				i--;
			}
			if (i >= 0) {
				number1 = userTable.get(i).cumulativeTweets;
				System.out.println("nearest to userId1: "
						+ userTable.get(i).userId);
			}
		}

		/* find nearest user ID to userId2 */
		while (j >= 0 && userTable.get(j).userId > userId2) {
			j--;
		}
		if (j != -1) {
			number2 = userTable.get(j).cumulativeTweets;
			System.out
					.println("nearest to userId2: " + userTable.get(j).userId);
		}

		return number2 - number1;
	}

	public static void main(String[] args) throws IOException {
		userTable = new ArrayList<UserInfo>();
		BufferedReader reader = new BufferedReader(new FileReader(FILE_LOC));
		System.out.println("loading file: " + FILE_LOC + "...");
		long total = 0;
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] element = line.split(",");
			long userId = Long.parseLong(element[0]);
			total += Long.parseLong(element[1]);
			StringBuilder builder = new StringBuilder();
			if (element.length > 2) {
				for (int i = 2; i < element.length; i++) {
					builder.append(element[i] + "\n");
				}
			}
			UserInfo userInfo = new UserInfo(userId, total, builder.toString()
					.getBytes());
			userTable.add(userInfo);
		}
		reader.close();
		System.out.println("file loaded\ntesting...");
		Random r = new Random(System.currentTimeMillis());
		while (true) {
			long userId1 = (Math.abs(r.nextLong()) % USER_MAX) + 1;
			long userId2 = (Math.abs(r.nextLong()) % USER_MAX) + 1;
			if (userId1 > userId2) {
				long temp = userId2;
				userId2 = userId1;
				userId1 = temp;
			}
			System.out.println("userId1 = " + userId1 + ", userId2 = "
					+ userId2);
			String q3Reply = httpDownload(q3Request(userId1, userId2));
			String[] parsedReply = q3Reply.split(System
					.getProperty("line.separator"));
			long replyValue = Long.parseLong(parsedReply[1]);
			long verify = q3Verify(userId1, userId2);
			System.out
					.println("reply = " + replyValue + ", verify = " + verify);
			if (replyValue != verify) {
				System.err.println("error");
			}
			System.out.println();
		}
	}

	public static String httpDownload(String urlAddr) throws IOException {
		URLConnection conn = new URL(urlAddr).openConnection();
		InputStream in = conn.getInputStream();
		int byteNum = 0;
		StringBuffer payload = new StringBuffer();
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((byteNum = in.read(buffer)) != -1) {
			if (byteNum != BUFFER_SIZE) {
				buffer = Arrays.copyOf(buffer, byteNum);
			}
			payload.append(new String(buffer, "UTF-8"));
		}
		return payload.toString();
	}
}
