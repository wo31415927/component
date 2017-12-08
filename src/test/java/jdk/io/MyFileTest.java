package jdk.io;

import org.junit.Test;

import java.io.File;

/**
 * @author zeyu 2017/12/6
 */
public class MyFileTest {
    @Test
    public void testFile() throws Exception {
        System.out.println(File.separator);
        System.out.println(File.pathSeparator);
    }
}