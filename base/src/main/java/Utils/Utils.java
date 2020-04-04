package Utils;

import com.google.common.base.Joiner;

import java.util.UUID;

import static com.google.common.base.Ascii.SOH;

/** @author chenxiang 2019/6/17/017 */
public class Utils {
  public static final Joiner JOINER_COMMA = Joiner.on(",");
  public static final Joiner JOINER_UNDER_LINE = Joiner.on("_");
  /** ASCII SOH */
  public static final Joiner JOINER_ASCII_SOH = Joiner.on((char) SOH).skipNulls();

  /** @return uuid v4 random uuid */
  public static String getUUID() {
    String uuid = UUID.randomUUID().toString();
    uuid = uuid.replace("-", "");
    return uuid;
  }

  /**
   * 传入相同的str,每次得到的结果都一样
   *
   * @return uuid v4 random uuid
   */
  public static String getUniqueUUID(String str) {
    String uuid = UUID.nameUUIDFromBytes(str.getBytes()).toString();
    uuid = uuid.replace("-", "");
    return uuid;
  }

}
