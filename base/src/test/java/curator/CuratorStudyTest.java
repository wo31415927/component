package curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author zeyu 2018/1/30
 */
public class CuratorStudyTest {
    @Test
    public void testLock() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        latch.countDown();
        latch.await();
        CuratorFramework curatorFramework;
        DistributedAtomicInteger integer;
    }
}