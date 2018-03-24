package jmockit;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;

/**
 * @author zeyu 2017/12/11
 */
@RunWith(Suite.class)
//所有test方法共享Mock对象,貌似不管用
@Suite.SuiteClasses({StateTest.class})
public class MockContext {
    @BeforeClass
    public static void setUp() throws Exception {
        MockUp<StateTest.DumpCmd> cmd = new MockUp<StateTest.DumpCmd>() {
            //$clinit使mock实例忽略原有类中的static属性或逻辑初始化
            @Mock
            void $clinit() {
            }

            @Mock
            void $init(Invocation invocation, int index) {
                StateTest.DumpCmd cmd = invocation.getInvokedInstance();
                //构造函数行为不会替代
                //invocation.proceed(11);
            }

            @Mock
            int transfer(Invocation invocation, int code) {
                //覆写了原有的行为
                return invocation.proceed(code + 1);
            }

            @Mock
            public void run() {
                System.out.println("MockUp-DumpCmd");
            }

            @Mock
            public Object $advice(Invocation invocation) {
                System.out.println("Before Fake all method!");
                invocation.proceed();
                return null;
            }
        };
    }
}
