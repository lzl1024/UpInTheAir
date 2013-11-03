import java.io.IOException;

import util.Constants;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class AirService {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting grizzly...");

        ResourceConfig config = new PackagesResourceConfig("api");
        GrizzlyServerFactory.createHttpServer(Constants.BASE_URI, config);

        while (true)
            ;
    }
}