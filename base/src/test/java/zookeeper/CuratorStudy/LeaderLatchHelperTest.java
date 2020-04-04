package zookeeper.CuratorStudy;

import com.google.common.collect.Lists;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import Utils.Utils;
import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/6/17/017 */
@Slf4j
public class LeaderLatchHelperTest {
  String zkUrl = "cent1:2181,cent2:2181,cent3:2181";
  String namespace = "test";
  int PARTICIPANT_QTY = 2;

  @Test
  public void testBase() throws Exception {
    String reqId = Utils.getUUID();
    List<LeaderLatchHelper.CustomLeaderLatch> customLeaderLatchList = Lists.newArrayList();
    try (CuratorFramework client =
        CuratorFrameworkFactory.newClient(zkUrl, new RetryOneTime(100))) {
      client.start();
      TimeUnit.SECONDS.sleep(3);
      String key = "task0";
      for (int i = 0; i < PARTICIPANT_QTY; i++) {
        LeaderLatchHelper.CustomLeaderLatch leaderLatch =
            new LeaderLatchHelper.CustomLeaderLatch()
                .buildLeaderLatch(client, namespace, key, reqId);
        customLeaderLatchList.add(leaderLatch);
        log.info("reqId:{},Latch[{}],isLeader:{}", reqId, i, leaderLatch.isLeader());
      }
      TimeUnit.SECONDS.sleep(1);
      for (LeaderLatchHelper.CustomLeaderLatch customLeaderLatch : customLeaderLatchList) {
        log.info("reqId:{},final result:{}", reqId, customLeaderLatch.isLeader());
      }
    }
  }
}
