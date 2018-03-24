package curator;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zeyu 2018/2/1
 */
public class ZookeeperStudyTest {
    String host = "vm252:2181";
    String znode = "/lock/r";
    String filename = "k://zookeeper/logs/test.log";
    String processName = "zkWatch";

    Executor executor;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testWatcher() throws Exception {
        Executor.main(host,znode,filename,processName);
    }
}