import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
 
public class MainClass {
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(8080).build();
 
    public static void main(String[] args) throws IOException {
        System.out.println("Starting grizzly...");
        
        ResourceConfig config = new PackagesResourceConfig("api");
        GrizzlyServerFactory.createHttpServer(BASE_URI, config);

        while(true);
    }
}