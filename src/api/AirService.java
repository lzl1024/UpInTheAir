package api;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
 

@Path("q1")
public class AirService {

    public static String hearbeat = "Up In The Air, 3929-1038-4476\n";
    public static DateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
/**
 * Heartbeat request
 * @return
 */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String heartBeat() {
        return hearbeat+format.format(new Date());
    }
}