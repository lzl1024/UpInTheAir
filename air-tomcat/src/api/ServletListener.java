package api;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.Constants;

public class ServletListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent arg0) {
        // read file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    Constants.FILE_LOC));
            System.out.println("Begin to Read");
            long total = 0;

            String line;

            while ((line = reader.readLine()) != null) {
                String[] element = line.split(",");
                Long userId = Long.parseLong(element[0]);
                total += Long.parseLong(element[1]);

                if (userId > NumberofTweets.UserMax) {
                    NumberofTweets.UserMax = userId;
                }

                Table row = new Table(total, null);

                // get retweet list
                if (element.length > 2) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < element.length; i++) {
                        builder.append(element[i] + "\n");
                    }

                    row.retweetList = builder.toString();
                }

                NumberofTweets.table.add(row);
                NumberofTweets.index.put(userId,
                        NumberofTweets.table.size() - 1);
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