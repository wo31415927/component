package zookeeper.CuratorStudy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/6/17/017 */
@Slf4j
public class CuratorUtils {
  private static CuratorFramework CURATOR_FRAMEWORK;
  private static String CONNECT_STR = "cent1:2181,cent2:2181,cent3:2181";
  private static int RETRY_SLEEP_TIME_MS = 1000;
  private static int RETRY_COUNT = 3;

  public static CuratorFramework getCuratorFramework() {
    if (null != CURATOR_FRAMEWORK) {
      return CURATOR_FRAMEWORK;
    }
    synchronized (CuratorUtils.class) {
      if (null != CURATOR_FRAMEWORK) {
        return CURATOR_FRAMEWORK;
      }
      CuratorFramework client =
          CuratorFrameworkFactory.builder()
              .connectString(CONNECT_STR)
              .retryPolicy(new ExponentialBackoffRetry(RETRY_SLEEP_TIME_MS, RETRY_COUNT))
              .build();
      client.start();
      CURATOR_FRAMEWORK = client;
      log.info("CURATOR_FRAMEWORK create success.connectUrl:{}", CONNECT_STR);
      return CURATOR_FRAMEWORK;
    }
  }
}
