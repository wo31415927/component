package guava.service;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

import org.junit.Test;

/**
 * @author zeyu 2017/11/13
 */
public class ServiceTest {
    AbstractExecutionThreadService threadService = new AbstractExecutionThreadService() {
        @Override
        protected void run() throws Exception {
            int a = 1/0;
        }

        @Override
        protected void shutDown() throws Exception {
            throw new UnsupportedOperationException();
        }
    };

    @Test
    public void testService() throws Exception {
        //异常处理时糟糕的日志打印
        threadService.startAsync().awaitRunning();
    }
}