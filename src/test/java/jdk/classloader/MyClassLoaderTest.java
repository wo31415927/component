package jdk.classloader;

import com.google.common.truth.Truth;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author zeyu 2018/2/5
 */
public class MyClassLoaderTest {
    @Test
    public void testAppClassLoader() throws Exception {
        ClassLoader cl1 = ClassLoader.getSystemClassLoader();
        ClassLoader cl2 = this.getClass().getClassLoader();
        ClassLoader cl3 = Thread.currentThread().getContextClassLoader();
        Truth.assertThat(cl1).isEqualTo(cl2);
        Truth.assertThat(cl1).isEqualTo(cl3);
    }

    @Test
    public void testClassLoaderResource() throws Exception {
        ClassLoader cl1 = ClassLoader.getSystemClassLoader();
        //file:/J:/GitWk/study/component/target/test-classes/
        System.out.println(cl1.getResource(""));
        //找不到
        System.out.println(cl1.getResources(""));
        //
        System.out.println(IOUtils.readLines(cl1.getResourceAsStream("")));
    }
}