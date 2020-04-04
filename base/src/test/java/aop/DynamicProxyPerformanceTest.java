package aop;

import org.junit.Test;

import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** @author chenxiang 2019/8/12/012 */
public class DynamicProxyPerformanceTest {
  /**
   * 输出jdk动态代理创建的代理class文件，便于使用javap -c或反编译工具研究其源码
   * @throws IOException
   */
  @Test
  public void testOutputJdkProxyClass() throws IOException {
    String classByteName = "$proxy0";
    byte[] classByte =
        ProxyGenerator.generateProxyClass(classByteName, new Class[] {CountService.class});
    File f = new File("D:/class/jdk/" + classByteName);
    if (f.exists()) {
      f.delete();
    }
    f.createNewFile();
    try (OutputStream out = new FileOutputStream(f)) {
      out.write(classByte);
      out.flush();
    }
  }

  /**
   * 结果随机性较高，没有明显的规律，总体来讲反射的性能在直接调用的2~4倍
   *
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  @Test
  public void testReflect()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    int times = 10;
    int count = 10000000;
    DynamicProxyPerformance.Cost cost1 = new DynamicProxyPerformance.Cost("directCall");
    DynamicProxyPerformance.Cost cost2 = new DynamicProxyPerformance.Cost("reflectCall");

    CountService countService1 = new CountServiceImpl();
    CountService countService2 = new CountServiceImpl();
    Method method = CountService.class.getMethod("count");
    // 第一次相当于warm up
    for (int j = 0; j < times; j++) {
      System.out.println("times[" + j + "]：");
      countService1.clear();
      countService2.clear();
      cost1.clear();
      cost2.clear();
      for (int i = 0; i < count; i++) {
        long time = System.currentTimeMillis();
        countService1.count();
        cost1.add(System.currentTimeMillis() - time);
      }
      for (int i = 0; i < count; i++) {
        long time = System.currentTimeMillis();
        method.invoke(countService2);
        cost2.add(System.currentTimeMillis() - time);
      }
      cost1.print();
      cost2.print();
      System.out.println("Ratio:" + cost2.getCost() / (cost1.getCost() + 0.0));
    }
  }
}
