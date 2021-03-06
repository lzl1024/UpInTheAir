package util;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {
	public static DateFormat FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
	public static String time = null;

	public static String ANS_TITLE = "Up_in_the_Air,3929-1038-4476\n";
	public static int SIZE = 18186000;
	public static long MARK = 2148717489L;
	public static long INDEX_SIZE = 2148719;
	public static int divisor = 1000;
	// file location
	public static String FILE_LOC = "/usr/UpInTheAir/output.csv";
	
	// jdbc
	public static Connection conn;
	public static String queryPrefix = "SELECT tweet FROM time_tweet WHERE time=\"";
	public static String DBName = "cloud_project";
	public static String cellName = "tweet";
	public static String url = "jdbc:mysql://127.0.0.1:3306/cloud_project";
    public static String username = "root";
    public static String password = "password";
	
}
