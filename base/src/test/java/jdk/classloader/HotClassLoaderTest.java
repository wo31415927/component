package jdk.classloader;

import com.google.common.truth.Truth;

import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author zeyu 2018/2/5
 */
public class HotClassLoaderTest {
    URLClassLoader hotClassLoader;

    /**
     * 不同的classLoader加载相同的class到不同的class实例
     * @throws Exception
     */
    @Test
    public void testLoadTwinClass() throws Exception {
        String className = this.getClass().getName();
        //file:/J:/GitWk/study/component/target/test-classes/
        URL url = this.getClass().getClassLoader().getResource("");
        hotClassLoader = new URLClassLoader(new URL[]{url},null);
        Class c1 = ClassLoader.getSystemClassLoader().loadClass(className);
        Class c2 = hotClassLoader.loadClass(className);
        System.out.println(c1.hashCode());
        System.out.println(c2.hashCode());
        Truth.assertThat(c1).isNotEqualTo(c2);
    }
}