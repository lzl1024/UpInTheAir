import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * Helper functions for external sort
 * 
 */
public class ExternHelper {
    public static String user_tweets = "user_tweets.csv";
    public static String user_retweet = "user_retweet.csv";
    // prefix of userid sort output
    public static String SORT_USER_FN = "SORT_USERID_";

    // write 19 files
    public static int fileNum = 0;
    public static long lineNum = 0;
    public static CsvWriter writer2;

    /**
     * 
     * Key-value pair store in the heap
     * 
     */
    public static class KVPair {
        Record record;
        int file;

        public KVPair(Record record, int file) {
            super();
            this.record = record;
            this.file = file;
        }

        @Override
        public String toString() {
            return "KVPair [record=" + record + ", file=" + file + "]";
        }
    }

    /**
     * External sort for experiment 1
     * 
     * @param source
     * @return
     * @throws Exception
     */
    public static ArrayList<String> Externalexp1Sort(ArrayList<String> source)
            throws Exception {
        // file
        // ArrayList<Long> k = new ArrayList<Long>();
        // k.add(0L);
        // k.add(0L);
        // k.add(0L);
        // Long repeat = 0L;

        PriorityQueue<KVPair> heap = new PriorityQueue<KVPair>(20,
                new UserPrio());
        ArrayList<CsvReader> readers = new ArrayList<CsvReader>();
        // user_tweets output
        PrintWriter writer = new PrintWriter(new FileWriter(user_tweets));

        writer2 = new CsvWriter(new OutputStreamWriter(new FileOutputStream(
                SORT_USER_FN + fileNum + ".csv"), "UTF-8"), ',');

        // open files
        for (String fileName : source) {
            // get read csv files
            CsvReader reader = new CsvReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"));
            reader.setHeaders(Merge.fields);
            // read a line and put into heap
            if (reader.readRecord()) {
                heap.offer(new KVPair(Merge.readRecord(reader), readers.size()));
            }
            readers.add(reader);
        }

        // get same key
        KVPair last = null;
        HashMap<Long, Record> sameRecord = new HashMap<Long, Record>();

        while (heap.size() > 0) {
            KVPair tmp = heap.poll();

            if (last != null && last.record.userid != tmp.record.userid) {
                writer.println(last.record.userid + "," + sameRecord.size());

                // print records to file
                writeRecords(sameRecord.values());

                sameRecord = new HashMap<Long, Record>();

            }

            // if (sameRecord.containsKey(tmp.record.tweetid)) {
            // repeat ++;
            // System.out.println(tmp.record.tweetid +" : " + repeat);
            // }

            sameRecord.put(tmp.record.tweetid, tmp.record);

            // read next record in the same file
            if (readers.get(tmp.file).readRecord()) {

                // k.set(tmp.file, k.get(tmp.file)+1);
                // System.out.println(k);

                heap.offer(new KVPair(Merge.readRecord(readers.get(tmp.file)),
                        tmp.file));
            } else {
                readers.get(tmp.file).close();
            }
            last = tmp;
        }

        // last part of external sort
        if (sameRecord.size() != 0) {
            writer.println(last.record.userid + "," + sameRecord.size());

            // print records to file
            writeRecords(sameRecord.values());
        }

        writer.close();

        writer2.flush();
        writer2.close();

        return source;
    }

    /**
     * write records to files
     * 
     * @param values
     * @throws IOException
     */
    private static void writeRecords(Collection<Record> values)
            throws IOException {
        for (Record record : values) {
            writer2.writeRecord(record.makeup());
            // over write, update writer
            // System.out.println(lineNum);
            if (++lineNum > Merge.lineNumbers.get(fileNum)) {
                writer2.flush();
                writer2.close();

                fileNum++;
                lineNum = 0;
                writer2 = new CsvWriter(new OutputStreamWriter(
                        new FileOutputStream(SORT_USER_FN + fileNum + ".csv"),
                        "UTF-8"), ',');
            }
        }

    }

    /**
     * Read the file and out put the userid, retweet list
     * 
     * @param source
     * @throws IOException
     */
    public static void Externalexp2Sort(ArrayList<String> source)
            throws IOException {
        PriorityQueue<KVPair> heap = new PriorityQueue<KVPair>(20,
                new UserPrio());
        ArrayList<CsvReader> readers = new ArrayList<CsvReader>();
        // user_tweets output
        PrintWriter writer = new PrintWriter(new FileWriter(user_retweet));

        // open files
        for (String fileName : source) {
            // get read csv files
            CsvReader reader = new CsvReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"));
            reader.setHeaders(Merge.fields);
            // read a line and put into heap
            if (reader.readRecord()) {
                heap.offer(new KVPair(Merge.readRecord(reader), readers.size()));
            }
            readers.add(reader);
        }

        // get same key
        KVPair last = null;
        HashSet<Long> sameRecord = new HashSet<Long>();

        while (heap.size() > 0) {
            KVPair tmp = heap.poll();

            // no retweet directly read the next
            if (tmp.record.retweet != -1) {
                if (last != null && last.record.retweet != -1
                        && last.record.retweet != tmp.record.retweet) {
                    StringBuilder builder = new StringBuilder(
                            last.record.retweet + ",");

                    for (Long userid : sameRecord) {
                        builder.append(userid);
                        builder.append(" ");
                    }
                    writer.println(builder);
                    sameRecord = new HashSet<Long>();
                }
                sameRecord.add(tmp.record.userid);
            }

            // read next record in the same file
            if (readers.get(tmp.file).readRecord()) {
                heap.offer(new KVPair(Merge.readRecord(readers.get(tmp.file)),
                        tmp.file));
            } else {
                readers.get(tmp.file).close();
            }
            last = tmp;
        }

        // last part of external sort
        if (sameRecord.size() != 0) {
            StringBuilder builder = new StringBuilder(last.record.retweet + ",");

            for (Long userid : sameRecord) {
                builder.append(userid);
                builder.append(" ");
            }
            writer.println(builder);
        }

        writer.close();
    }

    /**
     * 
     * Comparator to sort as userid
     * 
     */
    public static class UserPrio implements Comparator<KVPair> {

        @Override
        public int compare(KVPair rec1, KVPair rec2) {
            if (rec1.record.userid > rec2.record.userid) {
                return 1;
            } else if (rec1.record.userid < rec2.record.userid) {
                return -1;
            }
            return 0;
        }

    }

    /**
     * 
     * Comparator to sort as retweet id
     * 
     */
    public static class RetweetPrio implements Comparator<KVPair> {

        @Override
        public int compare(KVPair rec1, KVPair rec2) {
            if (rec1.record.retweet > rec2.record.retweet) {
                return 1;
            } else if (rec1.record.retweet < rec2.record.retweet) {
                return -1;
            }
            return 0;
        }

    }
}
