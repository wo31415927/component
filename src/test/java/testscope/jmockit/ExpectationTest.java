package testscope.jmockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import mockit.VerificationsInOrder;
import mockit.integration.junit4.JMockit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/** @author cctv 2017/12/9 */
@RunWith(JMockit.class)
public class ExpectationTest {
  @Test
  public void testExpectation(
      @Mocked Dependency1 d1, @Injectable Dependency2 d2, @Mocked Dependency3 d3) {
    /*
    final ITimer timer = new CntTimer();
    //此处声明class，表示对Expectations中声明的方法mock，其他的方法调用原来的逻辑
    new Expectations(CntTimer.class) {
      {
        timer.addConsumeCnt(anyInt);
        result = new SQLException("Mock");
      }
    };*/
    //可以在此处指定具体的实例或class来实现部分Mock
    new Expectations() {
      {
        d1.mockMethod();
        returns("1", "2", "3");
        Dependency1.mockStatic();
        result = "mockStatic";
        d2.mockMethod();
        result = "another";
        Dependency2.mockStatic();
        result = "mockStatic";
        Dependency3 dependency3 = new Dependency3("Hello");
        dependency3.mockMethod();
        result = "MockHello";
        //null需要和any或with联用,真实的null，使用withNull()
        dependency3.param(anyString, withNotEqual("abc"), null, "yes");
        result = "paramString";
      }
    };

    //会被mock
    //静态函数可以被mock
    assertEquals("mockStatic", Dependency1.mockStatic());
    //returns
    //@Mock影响该类的所有对象
    assertEquals("1", new Dependency1().mockMethod());
    assertEquals("2", new Dependency1().mockMethod());
    assertEquals("3", new Dependency1().mockMethod());
    //超出returns的对象取最后一个值
    assertEquals("3", new Dependency1().mockMethod());
    //Expectations未record，会被Jmockit处理并返回null或类型初始值
    assertNull(null, d1.random());
    //只会mock @Injectable声明的对象
    assertEquals("another", d2.mockMethod());
    assertEquals("real", new Dependency2().mockMethod());
    //@Injectable static函数不会被Jmockit处理
    assertEquals("static", Dependency2.mockStatic());
    //mock特定constructor产生的实例
    assertEquals("MockHello", new Dependency3("Hello").mockMethod());
    assertEquals("MockHello", new Dependency3("Hello").mockMethod());
    assertEquals("MockHello", new Dependency3("Hello").mockMethod());
    assertNull("", new Dependency3("Hello1").mockMethod());
    //
    assertEquals("paramString", new Dependency3("Hello").param("Haha", "", "", "yes"));
    assertEquals(null, new Dependency3("Hello").param("Haha", "abc", "", ""));
    //initPerson()在Expectations中未定义，会返会一个@Injectable的mock对象
    assertEquals(0, new Dependency3("Hello").initPerson1().getAge());
    //相同方法返回的是同一个@Injectable对象
    assertSame(d3.initPerson1(), d3.initPerson1());
    //不同方法返回的是不同的@Injectable对象,若声明@Person，则返回的是同一个@Mock对象
    assertNotSame(d3.initPerson1(), d3.initPerson2());
  }

  @Test
  public void testVerify(@Mocked Dependency1 d1, @Mocked Dependency2 d2) {
    d1.mockMethod();
    d1.random();
    d2.mockMethod();

    //验证调用顺序
    new VerificationsInOrder() {
      {
        // 顺序不一致时会触发以下异常
        // Unexpected invocation:未期待的调用

        // 成功
        d1.mockMethod();
        //对不需要验证的方法占位
        unverifiedInvocations();
        d2.mockMethod();
      }
    };

    //验证方法的调用
    new Verifications() {
      {
        d1.random();
      }
    };
  }

  /**
   * 使用@Capturing标注基类/接口，所有实现类会被mock
   * @Capturing是出现在参数列表中的，如果是作为field声明的，maxInstances会失效，@Capturing退化为@Mock。
   *
   * @param service
   */
  @Test
  public void mockingImplementationClassesFromAGivenBaseType(@Capturing Service service) {
    new Expectations() {
      {
        service.doSomething();
        result = 3;
        //returns至少需要返回2个值
        //      returns(3,4);
      }
    };

    int result = new TestedUnit().businessOperation();
    assertEquals(3, result);
  }

  public static class Dependency1 {
    public String mockMethod() {
      return "real";
    }

    public static String mockStatic() {
      return "static";
    }

    public String random() {
      return "random";
    }
  }

  public static class Dependency2 {
    public String mockMethod() {
      return "real";
    }

    public static String mockStatic() {
      return "static";
    }
  }

  @RequiredArgsConstructor
  public static class Dependency3 {
    protected final String msg;

    public String mockMethod() {
      return msg;
    }

    public String param(String s1, String s2, String s3, String s4) {
      return s1.concat(s2).concat(s3).concat(s4);
    }

    public Person initPerson1() {
      return null;
    }

    public Person initPerson2() {
      return null;
    }
  }

  @AllArgsConstructor
  @Getter
  public class Person {
    protected String name;
    protected int age;
  }

  public interface Service {
    int doSomething();
  }

  public final class TestedUnit {

    private final Service service =
        new Service() {
          public int doSomething() {
            return 2;
          }
        };

    public int businessOperation() {
      return service.doSomething();
    }
  }
}
