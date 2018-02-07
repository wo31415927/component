package jdk.io;

import com.google.common.base.Strings;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author zeyu 2018/2/7
 */
public class MyScannerTest {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Test
    public void testScanner() throws Exception {
        String path = "k://big/queue.csv";
        String delimiter = "\n";
//        executorService.scheduleAtFixedRate()
        try (Scanner reader = new Scanner(Paths.get(path))) {
            reader.useDelimiter(delimiter);
            String row;
            int index = 0;
            //略过空行
            //xx&xx&  第2个&后面reader.hasNext==false
            while (reader.hasNext() && !Strings.isNullOrEmpty(row = reader.next())) {
                if (++index <= 0) {
                    continue;
                }
                if(Thread.interrupted()){
                    throw  new InterruptedException();
                }
                if (!Strings.isNullOrEmpty(row)) {
                    //
                }
            }
        }
    }
}