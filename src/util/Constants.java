package util;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.core.UriBuilder;

public class Constants {
    public static DateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/")
            .port(8080).build();
    public static String ANS_TITLE = "Up In The Air, 3929-1038-4476\n";
}
