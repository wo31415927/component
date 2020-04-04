import com.google.common.hash.Hashing;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.Cookie;

/** @author chenxiang 2019/6/23/023 */
public class TmpTest {
  @Test
  public void testConsistentHash() {
    SortedMap<Integer, String> map = new TreeMap<>();
    map.put(1, "");
    map.put(2, "");
    map.put(3, "");
    System.out.println(map.keySet());
    SortedMap<Integer, String> tailMap = map.tailMap(2);
    System.out.println(tailMap.keySet());

    // 一致性hash环
    TreeMap<Integer, String> ringMap = new TreeMap<>();
    List<String> serverList = Lists.newArrayList("server1", "server2");
    int VNODE_SIZE = 1024;
    // init ringMap
    for (int i = 0; i < VNODE_SIZE; i++) {
      for (String server : serverList) {
        // 对规律性较强的key，murmurHash分布更均匀
        //        int hashKey = (server + i).hashCode();
        int hashKey =
            Hashing.murmur3_128()
                .newHasher()
                .putString(server + i, Charset.defaultCharset())
                .hash()
                .asInt();
        ringMap.put(hashKey, server);
      }
    }
    // route
    String reqKey = "reqKey";
    Map.Entry<Integer, String> desEntry;
    // 找出大于等于reqKey的entry，若有，则返回；若无，则返回环中最小的entry
    if (null == (desEntry = ringMap.ceilingEntry(reqKey.hashCode()))) {
      System.out.println(ringMap.firstEntry().getValue());
    } else {
      System.out.println(desEntry.getValue());
    }
  }

  @Test
  public void name() {
    Cookie cookie = null;
    cookie.setMaxAge(-1);

  }

  public class Client {


  }



}
