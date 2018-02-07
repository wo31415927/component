package jdk.io;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** @author zeyu 2018/2/7 */
public class MyScannerTest {
  ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

  @Test
  public void testScanner() throws Exception {
    int period = 5;
    String path = "k://big/queue.csv";
    String delimiter = "\n";
    AtomicInteger index = new AtomicInteger();
    final int[] preIndex = {0};
    Splitter splitter = Splitter.on("|");
    executorService.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            int curIndex = index.get();
            System.out.println("Speed(rows/s): " + (curIndex - preIndex[0]) / (double) period);
            preIndex[0] = curIndex;
          }
        },
        0,
        period,
        TimeUnit.SECONDS);
    try (Scanner reader = new Scanner(Paths.get(path))) {
      reader.useDelimiter(delimiter);
      String row;
      //略过空行
      //xx&xx&  第2个&后面reader.hasNext==false
      while (reader.hasNext() && !Strings.isNullOrEmpty(row = reader.next())) {
        if (index.incrementAndGet() <= 0) {
          continue;
        }
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        if (!Strings.isNullOrEmpty(row)) {
          splitter.splitToList(row);
        }
      }
    }
  }
}
