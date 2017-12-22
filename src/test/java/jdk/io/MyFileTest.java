package jdk.io;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
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
        Path path = Paths.get("target/classes/log4j2.xml");
        //使用决对路径或者从项目根目录获取
        System.out.println(path.toFile().getAbsolutePath());
        try (InputStream inputStream = Files.newInputStream(path)) {
            System.out.println(inputStream);
        }
        //竟然无法使用绝对路径
        try (InputStream inputStream =
                     MyFile.class.getResourceAsStream("D:\\tmp\\mytmp\\apple1\\testcolumn\\testcolumn.txt")) {
            System.out.println(inputStream);
        }
        try (InputStream inputStream =
                     new FileInputStream("D:\\tmp\\mytmp\\apple1\\testcolumn\\testcolumn.txt")) {
            System.out.println(inputStream);
        }
        //同File
        Files.walkFileTree(
//                Paths.get("target/classes/"),
                Paths.get("D:\\tmp\\mytmp\\apple1\\testcolumn"),
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        System.out.println(file.toFile().getAbsolutePath());
                        return super.visitFile(file, attrs);
                    }
                });
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

    @Test
    public void testTmpDir() throws Exception {
        Path supplyPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("udaldump1").resolve("supply");
        //当父目录不存在时，自动创建父目录
        Files.createDirectories(supplyPath);
    }
}
