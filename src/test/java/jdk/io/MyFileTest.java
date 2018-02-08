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
    public void testLoadFilePath() throws Exception {
        //项目根目录获取
        String filePath1 = "target/classes/log4j2.xml";
        //从系统盘根目录获取
        String filePath2 = "/target/classes/log4j2.xml";
        //Path
        System.out.println(Paths.get(filePath1).toFile().getAbsolutePath());
        System.out.println(Paths.get(filePath2).toFile().getAbsolutePath());
        //同Path
        File f1 = new File(filePath1);
        File f2 = new File(filePath2);
        System.out.println(f1.getAbsolutePath());
        System.out.println(f2.getAbsolutePath());
        //同Path
        Files.walkFileTree(
                Paths.get("target/classes/"),
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
//                        System.out.println(file.toFile().getAbsolutePath());
                        return super.visitFile(file, attrs);
                    }
                });
        //同Path
        try (InputStream inputStream =
                     new FileInputStream(filePath1)) {
            System.out.println("inputStream1: " + inputStream);
        }
        try (InputStream inputStream =
                     new FileInputStream(filePath2)) {
            System.out.println("inputStream2: " + inputStream);
        }catch (Exception e){}
        //同Path
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath1))) {
            System.out.println("Channels.newInputStream1: " + inputStream);
        }catch (Exception e){}
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath2))) {
            System.out.println("Channels.newInputStream2: " + inputStream);
        }catch (Exception e){}
        //调用的是classLoader，所以无法使用绝对路径
        //Class.resolveName(name)
        // 1)未带/,则转化为当前class所在的package1/xx/xx.class
        // 2)带/,则去掉/
        //然后交给class对应的classLoader加载，AppClassLoader在classpath下寻址，classpath包含classes与test-classes
        try (InputStream inputStream =
                     MyFile.class.getResourceAsStream("MyScannerTest.class")) {
            System.out.println("class1: "+inputStream);
        }catch (Exception e){}
        try (InputStream inputStream =
                     MyFile.class.getResourceAsStream("/zookeeper/curator/Executor.class")) {
            System.out.println("class2: "+inputStream);
        }catch (Exception e){}
        //无法使用绝对路径
        //classloader在classpath下寻址，xx.xx.xx,这种java包寻址的方式不支持，必须是类似目录与文件的寻址方式
        try (InputStream inputStream =
                     this.getClass().getClassLoader().getResourceAsStream("child/file.xml")) {
            System.out.println("classLoader1: "+inputStream);
        }catch (Exception e){}

        try (InputStream inputStream =
                     this.getClass().getClassLoader().getResourceAsStream("org/apache/commons/io/FileUtils.class")) {
            System.out.println("classLoader2: "+inputStream);
        }catch (Exception e){}

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
