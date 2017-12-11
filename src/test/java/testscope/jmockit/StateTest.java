package testscope.jmockit;

import org.junit.Assert;
import org.junit.Test;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;

import static org.junit.Assert.assertSame;

/**
 * 基于状态的jmockit测试
 *
 * @author zeyu 2017/12/11
 */
public class StateTest {
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

        /**
         * 代理所有方法,实测不管用
         * @param invocation
         * @return
         */
        @Mock
        public Object $advice(Invocation invocation) {
            System.out.println("Before Fake all method!");
            invocation.proceed();
            return null;
        }
    };

    Runnable runnableMockUp = new MockUp<Runnable>() {
        @Mock
        public void run() {
            System.out.println("MockUp-Runnable");
        }
    }.getMockInstance();

    @Test
    public void testState() throws Exception {
        //代理该类的所有对象实例
        new DumpCmd(1).run();
        //仅代理一个实例
        runnableMockUp.run();
        //新建的实例不受影响
        new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable");
            }
        }.run();
        //$clint()忽略了原有类中的静态初始化
        Assert.assertNull(DumpCmd.CMD_NAME);
        //invocation拦截并替换了构造器参数
        assertSame(12, new DumpCmd(1).transfer(11));
    }

    public static class DumpCmd implements Runnable {
        public static String CMD_NAME = "DUMP";
        protected int index;

        public DumpCmd(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            System.out.println(CMD_NAME);
        }

        public int transfer(int code) {
            return code;
        }
    }
}