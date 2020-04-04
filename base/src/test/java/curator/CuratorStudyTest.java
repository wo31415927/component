package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/** @author zeyu 2018/1/30 */
public class CuratorStudyTest {
  public static String zkConStr = "cent1:2181,cent2:2181,cent3:2181";
  public static String lockPath = "/lock";
  public static String leaderPath = "/leader";
  public static String dbLockPath = lockPath + "/db";
  public static String taskLeaderPath = leaderPath + "/task";
  public static CuratorFramework client;
  public static int MAX_LOCK_WAIT = 3;

  @BeforeClass
  public static void setUpClass() throws Exception {
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    client = CuratorFrameworkFactory.newClient(zkConStr, retryPolicy);
    client.start();
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    if (null != client) {
      client.close();
    }
  }
  /**
   * 测试分布式锁
   *
   * @throws Exception
   */
  @Test
  public void testLock() throws Exception {
    client.create().forPath("/my/path", "HelloWorld".getBytes());
    InterProcessMutex lock = new InterProcessMutex(client, dbLockPath);
    if (lock.acquire(MAX_LOCK_WAIT, TimeUnit.SECONDS)) {
      try {
        // do some work inside of the critical section here
      } finally {
        lock.release();
      }
    }
  }

  /** 测试选举 */
  @Test
  public void testLeaderShip() {
    LeaderSelectorListener listener =
        new LeaderSelectorListenerAdapter() {
          @Override
          public void takeLeadership(CuratorFramework client) throws Exception {
            // this callback will get called when you are the leader
            // do whatever leader work you need to and only exit
            // this method when you want to relinquish leadership
          }
        };

    LeaderSelector selector = new LeaderSelector(client, taskLeaderPath, listener);
    selector.autoRequeue(); // not required, but this is behavior that you will probably expect
    selector.start();
  }
}
