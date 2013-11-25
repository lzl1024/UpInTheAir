package api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.Constants;

public class ServletListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent arg0) {
        // load jdbc
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://127.0.0.1:3306/"+Constants.DBName;
        String username = "root";
        String password = "password";
        try {
            Constants.conn = DriverManager.getConnection(url, username,
                    password);
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

    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
