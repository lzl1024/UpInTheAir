import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import util.Constants;
import api.NumberofTweets;
import api.Table;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class AirService {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting grizzly...");

        ResourceConfig config = new PackagesResourceConfig("api");
        GrizzlyServerFactory.createHttpServer(Constants.BASE_URI, config);

        startup();
        while (true)
            ;
    }
    
    public static void startup() {
        // load jdbc
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://localhost:3306/"+Constants.DBName;
        String username = "&";
        String password = "";
        try {
            Connection conn = DriverManager.getConnection(url, username,
                    password);

            try {
                Constants.st = conn.createStatement();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        // read file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    Constants.FILE_LOC));
            System.out.println("Begin to Read");
            long total = 0;

            int k = 0;
            int indexPointer = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] element = line.split(",");
                long userId = Long.parseLong(element[0]);
                total += Long.parseLong(element[1]);

                if (userId > NumberofTweets.UserMax) {
                    NumberofTweets.UserMax = userId;
                }

                Table row = new Table(total, null,
                        (short) (userId % Constants.divisor));

                // get retweet list
                if (element.length > 2) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < element.length; i++) {
                        builder.append(element[i] + "\n");
                    }

                    row.retweetList = builder.toString().getBytes();
                }

                if (k % 100000 == 0) {
                    System.out.println(k / 100000);
                }

                if (userId / Constants.divisor != indexPointer) {
                    int now;
                    if (userId / Constants.divisor > Constants.INDEX_SIZE) {
                        now = NumberofTweets.index.length - 1;
                        NumberofTweets.index[now] = k + 1;
                    } else {
                        now = (int) (userId / Constants.divisor);
                    }

                    for (int i = indexPointer; i < now; i++) {
                        NumberofTweets.index[i] = k;
                    }
                    indexPointer = now;
                }

                NumberofTweets.table[k] = row;
                k++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end!");

    }

}