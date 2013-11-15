import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * 
 * This small project is to merge and get the useful information for 619 group
 * project. The input argument is the file going to merge.
 * 
 */
public class Merge {
    // line Numbers of each file
    static ArrayList<Long> lineNumbers;
    static String[] fields = { "userid", "time", "tweet", "retweet" };
    static long lineNum = 0;

    // key input: experiment 0 means sort by userid and get
    // out file 1 : userid, number of tweets --- output to a file -- hand to
    // ZHUOLIN
    // out file 2 : userid, time, tweetid:tweet, retweet target (19 files) --
    // hand to QIAN
    //
    // experiment 1 means sort by retweetid and get
    // out file 3 : userid, retweet list --- output to one file
    static public void main(String[] args) throws Exception {
        // input arguments
        ArrayList<String> input_exp1 = new ArrayList<String>(Arrays.asList(
                "result_split_fileList_3.csv", "result_split_fileList_8.csv",
                "result_split_fileList_13.csv"));

        ArrayList<String> exp1Results = ExternHelper
                .Externalexp1Sort(InternalSort(true, input_exp1));

        ExternHelper.Externalexp2Sort(InternalSort(false, exp1Results));
    }

    /**
     * Internal Sort the file
     * 
     * @param isUseridSort
     * @param inputs
     * 
     * @return inter file names
     * @throws IOException
     */
    private static ArrayList<String> InternalSort(boolean isUseridSort,
            ArrayList<String> inputs) throws IOException {
        ArrayList<String> interFiles = new ArrayList<String>();
        lineNumbers = new ArrayList<Long>();

        // go through each file
        for (int i = 0; i < inputs.size(); i++) {
            ArrayList<Record> records = new ArrayList<Record>();
            String fileName = inputs.get(i);

            // get new Files
            String interFile = isUseridSort ? "INTER_1_" + i + ".csv"
                    : "INTER_2_" + i + ".csv";
            interFiles.add(interFile);

            // read csv files
            CsvReader reader = new CsvReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"));
            reader.setHeaders(fields);
            // read one record
            while (reader.readRecord()) {
                lineNum++;
                records.add(readRecord(reader));
            }
            // add the line number and update the current line number
            lineNumbers.add(lineNum);
            lineNum = 0;

            // sort the result
            if (isUseridSort) {
                Collections.sort(records, new Record.UserPrio());
            } else {
                Collections.sort(records, new Record.RetweetPrio());
            }

            // output sorted records
            CsvWriter writer = new CsvWriter(new OutputStreamWriter(
                    new FileOutputStream(interFile), "UTF-8"), ',');
            for (Record record : records) {
                writer.writeRecord(record.makeup());
            }
 //System.out.println(records.size());
            writer.flush();
            writer.close();
            reader.close();

        }
        
        
        return interFiles;
    }

    public static Record readRecord(CsvReader reader) throws IOException {
        String[] line = new String[4];
        for (int j = 0; j <= 3; j++) {
            line[j] = reader.get(fields[j]);
        }
        
        return parseRecord(line);
    }

    /**
     * read a record across the line
     * 
     * @param line
     * @return
     */
    private static Record parseRecord(String[] line) {
        int index = line[2].indexOf(":");
        int begin = line[2].charAt(0) == '\"' ? 1 : 0;

        Record record = new Record();
        record.userid = Long.parseLong(line[0]);
        record.time = line[1];
        record.tweetid = Long.parseLong(line[2].substring(begin, index));
        record.tweet = line[2];
        record.retweet = Long.parseLong(line[3]);

        return record;
    }

}
