package Utils;

import com.google.common.truth.Truth;

import org.junit.Test;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/6/17/017 */
@Slf4j
public class UtilsTest {
  @Test
  public void testGetRandomUUID() {
    UUID uuid = UUID.randomUUID();
    //    log.info("timestamp:{}",uuid.timestamp());
    log.info("version:{}", uuid.version());
    log.info("variant:{}", uuid.variant());
    //    log.info("clockSequence:{}",uuid.clockSequence());
    log.info("uuid:{}", uuid);
  }

  @Test
  public void testGetUUIDFromStr() {
    String name = "c675ea8c-b502-43ba-9d90-d35e5506ada0";
    log.info("name:{}", name);
    UUID uuid = UUID.fromString(name);
    log.info("uuid:{}", uuid);
    Truth.assertThat(uuid.toString()).isEqualTo(name);
  }

  @Test
  public void testGetUniqueUUID() {
    String name = "c675ea8c-b502-43ba-9d90-d35e5506ada0";
    String uuid = UUID.nameUUIDFromBytes(name.getBytes()).toString();
    log.info("uuid:{}", uuid);
  }

}
