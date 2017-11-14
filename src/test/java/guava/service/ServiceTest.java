package guava.service;

import com.google.common.truth.Truth;
import com.google.common.util.concurrent.Service;

import org.junit.Test;
import org.slf4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zeyu 2017/11/13
 */
@Slf4j
public class ServiceTest {
    Service threadService = new AbstractRunningService() {
        @Override
        protected Logger getLogger() {
            return log;
        }

        @Override
        protected void run() throws Exception {
            int a = 1 / 0;
        }

        @Override
        protected void shutDown() throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Executor executor() {
            return Executors.newCachedThreadPool();
        }
    };

    /**
     * 打印出Running失败的原因
     * 打印出shutDown失败的原因
     * @throws Exception
     */
    @Test
    public void testService() throws Exception {
        try {
            threadService.startAsync().awaitTerminated();
        } catch (Exception e) {
        }
        Truth.assertThat(threadService.state()).isEqualTo(Service.State.FAILED);
        log.error("",threadService.failureCause());
    }
}