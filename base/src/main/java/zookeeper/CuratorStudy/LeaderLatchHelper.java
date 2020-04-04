package zookeeper.CuratorStudy;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

import java.net.InetAddress;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/6/17/017 */
@Slf4j
public class LeaderLatchHelper {
  private String namespace = "/test";

  private static final String LOCK_ROOT = "/lock";

  private static final String BIZ_STAGE = "/leaderLatch";

  /** key：主从选举使用键，value：自定义主从选举类 */
  private final Map<String, CustomLeaderLatch> leaderLatchMap = Maps.newHashMap();

  /**
   * 是否为leader节点
   *
   * @apiNote 初次选举会有延迟，此时均无主节点
   * @param key 主从选举使用的键
   * @return true：是，false：不是
   */
  public boolean isLeader(String key) throws Exception {
    Preconditions.checkArgument(StringUtils.isNotBlank(key), "key not allowed to be blank");
    if (leaderLatchMap.containsKey(key)) {
      return leaderLatchMap.get(key).isLeader();
    }

    synchronized (LeaderLatchHelper.class) {
      if (leaderLatchMap.containsKey(key)) {
        return leaderLatchMap.get(key).isLeader();
      }
      try {
        CustomLeaderLatch leaderLatch =
            new CustomLeaderLatch().buildLeaderLatch(null, namespace, key, "");
        leaderLatchMap.put(key, leaderLatch);
        log.info("create leader latch, key={}", key);
        return leaderLatch.isLeader();
      } catch (Exception e) {
        log.error("fail to create leader latch, key={}, {}", key, e.toString(), e);
        throw e;
      }
    }
  }

  /** 自定义leader选举类 */
  public static class CustomLeaderLatch {
    /** 是否为leader节点标志位 */
    private volatile Boolean isLeader = false;

    /**
     * 根据主从选举键获取在zk上的节点路径
     *
     * @param namespace 命名空间
     * @param key 主从选举使用的键
     * @return zk上的路径
     */
    private StringBuilder getLockPath(String namespace, String key) {
      Preconditions.checkArgument(StringUtils.isNotBlank(key), "key not allowed to be blank");
      StringBuilder sb = new StringBuilder();
      sb.append("/").append(namespace).append(LOCK_ROOT).append(BIZ_STAGE).append("/").append(key);
      return sb;
    }

    /**
     * 构建leader锁
     *
     * @param namespace 命名空间
     * @param key 主从选举使用的键
     * @return 自定义leader选举类
     * @throws Exception 异常
     */
    CustomLeaderLatch buildLeaderLatch(
        CuratorFramework curatorFramework, String namespace, String key, String reqId)
        throws Exception {
      Preconditions.checkArgument(StringUtils.isNotBlank(key), "key not allowed to be blank");

      final String path = getLockPath(namespace, key).toString();
      final String hostName = InetAddress.getLocalHost().getHostName();
      LeaderLatch leaderLatch =
          new LeaderLatch(curatorFramework, path, hostName, LeaderLatch.CloseMode.NOTIFY_LEADER);
      leaderLatch.addListener(
          new LeaderLatchListener() {
            @Override
            public void isLeader() {
              log.info("reqId:{},{} is selected as leader, key is {}", reqId, hostName, key);
              isLeader = true;
            }

            @Override
            public void notLeader() {
              log.info("reqId:{},{} is lost of leader, key is {}", reqId, hostName, key);
              isLeader = false;
            }
          });
      leaderLatch.start();
      return this;
    }

    /**
     * 是否为leader节点
     *
     * @return true：是，false：不是
     */
    public boolean isLeader() {
      return isLeader;
    }
  }
}
