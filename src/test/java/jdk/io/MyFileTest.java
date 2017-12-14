package jdk.io;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * @author zeyu 2017/12/6
 */
public class MyFileTest {
    @Test
    public void testFile() throws Exception {
        System.out.println(File.separator);
        System.out.println(File.pathSeparator);
    }

    @Test
    public void testCreateDir() throws Exception {
        String filePath = "k:\\udaldump\\dir\\dir\\";
        Files.createDirectories(Paths.get(filePath));
    }

    @Test
    public void testOpenFile() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        byteBuffer.put("HelloWorld2".getBytes());
        String filePath = "k:\\udaldump\\1.txt";
        //CREATE:当文件不存在时创建,存在时啥也不干
        //WRITE:从头开始写,会覆盖内容
        //APPEND:从尾部追加
        //CREATE和APPEND控制不同维度的功能,不会冲突,先后顺序无关
        //CREATE仅能创建文件,当上级目录缺失时会触发异常
        try (FileChannel channel = FileChannel.open(Paths.get(filePath), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                channel.write(byteBuffer);
            }
            byteBuffer.clear();
        }
    }

    @Test
    public void testLoadFile() throws Exception {
    /* String s = new String(CipherUtils.hex2ASCIIByte("0x05".substring(2).getBytes()));
    String des = s+"Hello"+s+"World"+s;
    System.out.println(s);
    System.out.println(Splitter.on(s).splitToList(des));
    System.out.println(des.startsWith(s) && des.endsWith(s));*/
        //竟然无法使用绝对路径
        try (InputStream inputStream =
                     MyFile.class.getResourceAsStream("D:\\tmp\\mytmp\\apple1\\testcolumn\\testcolumn.txt")) {
            System.out.println(inputStream);
        }
        try (InputStream inputStream =
                     new FileInputStream("D:\\tmp\\mytmp\\apple1\\testcolumn\\testcolumn.txt")) {
            System.out.println(inputStream);
        }
    }

    /**
     * 两种数组的格式是不一样
     */
    @Test
    public void testArray() throws Exception {
        Object[][] arr1 = {{1, 2}, {3, 4}};
        Object[] arr2 = new Object[2];
        arr2[0] = new Object[]{1, 2};
        arr2[1] = new Object[]{3, 4};
        System.out.println(Arrays.equals(arr1, arr2));
    }
}
