package util;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.core.UriBuilder;

public class Constants {
    public static DateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");
    public static int port = 8080;
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/")
            .port(port).build();
    public static String ANS_TITLE = "Up_in_the_Air,3929-1038-4476\n";
    public static int SIZE = 18186000;
    public static long INDEX_SIZE = 2148719;
    public static int divisor = 1000;
    // file location
    public static String FILE_LOC = "/C:/Users/Lenovo/Desktop/UpInTheAir/air-tomcat/output_total.csv";
    
}
