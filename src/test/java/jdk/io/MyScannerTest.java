package jdk.io;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zeyu 2018/2/7
 */
public class MyScannerTest {
    final int[] preIndex = {0};
    final long[] preSpace = {0};
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    int period = 5;
    String path = "k://big/queue.csv";
    String delimiter = "\n";
    int bufferSize = 1024 * 1024;
    int size1MB = 1024 * 1024;
    //rows
    AtomicInteger index = new AtomicInteger();
    //bytes
    AtomicLong space = new AtomicLong();
    Splitter splitter = Splitter.on("|");

    private void statics() {
        executorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        int curIndex = index.get();
                        long curSpace = space.get();
                        System.out.println(String.format("Speed:%s(rows/s); %s(M/s)", (curIndex - preIndex[0]) / (double) period, (curSpace -
                                preSpace[0]) /
                                1024.0 / 1024.0 / period));
                        preIndex[0] = curIndex;
                        preSpace[0] = curSpace;
                    }
                },
                0,
                period,
                TimeUnit.SECONDS);
    }

    private void processRow(String row) throws InterruptedException {
        if (index.incrementAndGet() <= 0) {
            return;
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        if (!Strings.isNullOrEmpty(row)) {
            splitter.splitToList(row);
            space.addAndGet(row.getBytes().length);
        }
    }

    @Test
    public void testScanner() throws Exception {
        statics();
        try (Scanner reader = new Scanner(Paths.get(path))) {
            /*Field buf = Scanner.class.getDeclaredField("buf");
            buf.setAccessible(true);
            buf.set(reader, CharBuffer.allocate(bufferSize));*/
            reader.useDelimiter(delimiter);
            String row;
            //略过空行
            //xx&xx&  第2个&后面reader.hasNext==false
            while (reader.hasNext() && !Strings.isNullOrEmpty(row = reader.next())) {
                processRow(row);
            }
        }
    }

    @Test
    public void testLineIterator() throws Exception {
        statics();
        LineIterator it = FileUtils.lineIterator(new File(path));
        String row;
        try {
            while (it.hasNext() && !Strings.isNullOrEmpty(row = it.nextLine())) {
                processRow(row);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    @Test
    public void testBufferReader() throws Exception {
        statics();
        String row;
        try (BufferedReader reader = new BufferedReader(new FileReader(path),1024*1024*36)) {
            while (-1 != reader.read()){
                //
            }
            /*while (!Strings.isNullOrEmpty(row = reader.readLine())) {
                processRow(row);
            }*/
        }
    }

    @Test
    public void testMappedBuffer() throws Exception {
        RandomAccessFile memoryFile = new RandomAccessFile("", "rw");
        MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, size1MB);
        for (int i = 0; i < size1MB; i++) {
            mappedByteBuffer.get(i);
        }
    }
}
