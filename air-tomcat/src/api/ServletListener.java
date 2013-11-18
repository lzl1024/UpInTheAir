package api;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.Constants;

public class ServletListener implements ServletContextListener {

    @SuppressWarnings("unchecked")
    public void contextInitialized(ServletContextEvent arg0) {
        // read steaming file
        NumberofTweets.index = (TreeMap<Long, Integer>) DeserializeData(Constants.indexFN);
        NumberofTweets.table = (ArrayList<Table>) DeserializeData(Constants.tableFN);
        NumberofTweets.UserMax = (Long) DeserializeData(Constants.tableFN);    
    }

    // read serializable data from file
    public static Serializable DeserializeData(String filename) {
        ObjectInputStream in;
        
      System.out.println("Begin: "+ filename);
        Serializable obj = null;
        try {
            FileInputStream fileInput = new FileInputStream(filename);
            in = new ObjectInputStream(fileInput);
            obj = (Serializable) in.readObject();
            in.close();
            fileInput.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        System.out.println("End: "+ filename);
        return obj;
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

}