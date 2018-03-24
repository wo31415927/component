package jvm;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/** @author cctv 2018/2/25 */
public class MetaspaceTest {
  /**
   * -XX:MaxMetaspaceSize=16m
   * 触发Metaspace OOM
   * @throws Exception
   */
  @Test(expected = java.lang.OutOfMemoryError.class)
  public void testMetaOom() throws Exception {
    URL url = new File("target/test-classes").toURI().toURL();
    List<ClassLoader> classLoaderList = new ArrayList<>();
    URL[] urls = {url};
    while (true) {
      //设置parent为null，避免都从AppClassLoader加载class
      ClassLoader loader = new URLClassLoader(urls,null);
      classLoaderList.add(loader);
      loader.loadClass("jvm.StringPoolTest");
    }
  }
}
