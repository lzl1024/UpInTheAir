package io;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import api.Table;

public class Streamming {
    public static void main(String[] args) {
        // index map, key : userid, value : record index
        HashMap<Long, Integer> index = new HashMap<Long, Integer>();
        // table to store the records
        ArrayList<Table> table = new ArrayList<Table>();
        // max userid
        Long UserMax = 0L;
        // index streaming fileName
        String indexFN = "index.stream";
        String tableFN = "table.stream";
        String userFN = "user.stream";

        // read file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "output_total.csv"));
            System.out.println("Begin to Read");
            long total = 0;

            String line;

            while ((line = reader.readLine()) != null) {
                String[] element = line.split(",");
                Long userId = Long.parseLong(element[0]);
                total += Long.parseLong(element[1]);

                if (userId > UserMax) {
                    UserMax = userId;
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

                table.add(row);
                index.put(userId, table.size() - 1);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SerializeData(indexFN, index);
        SerializeData(tableFN, table);
        SerializeData(userFN, UserMax);
        System.out.println("end!");
    }
    
    public static void SerializeData(String filename, Serializable obj) {

        try {
            //write serialized object into file
            ObjectOutput s = new ObjectOutputStream(new FileOutputStream(filename));
            System.out.println("filename is" + filename);
            s.writeObject(obj);
            s.flush();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
