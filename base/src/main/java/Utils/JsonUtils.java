package Utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/** @author chenxiang */
public class JsonUtils {
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    // 关闭写入完成后自动关闭OutputStream，由用户控制
    OBJECT_MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    // 关闭读取完成后自动关闭InputStream，由用户控制
    OBJECT_MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    // 忽略未知的字段，default true
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 仅序列化非空字段
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  }
  /**
   * 将一个json字符串转换成一个Object对象
   *
   * @param jsonStr
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T fromJson(String jsonStr, Class<T> clazz) {
    if (jsonStr == null) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(jsonStr, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json反序列化失败", e);
    }
  }

  /**
   * 将一个对象转成json字符串
   *
   * @param object
   * @return
   */
  public static String toJsonStr(Object object) {
    if (null == object) {
      return null;
    }
    try {
      return OBJECT_MAPPER.writeValueAsString(object);
    } catch (IOException e) {
      throw new RuntimeException("json序列化失败", e);
    }
  }

  /**
   * 讲一个json字符串转成map对象
   *
   * @param jsonStr
   * @return
   */
  public static <K, V> Map<K, V> parseMap(String jsonStr, Class<K> clazzK, Class<V> clazzV) {
    try {
      return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<Map<K, V>>() {});
    } catch (IOException e) {
      throw new RuntimeException("json 序列化失败", e);
    }
  }

  /** 将一个json字符串转成list对象 */
  public static <T> T fromJson(String json, TypeReference<T> toValueTypeRef) {
    try {
      return OBJECT_MAPPER.readValue(json, toValueTypeRef);
    } catch (IOException e) {
      throw new RuntimeException("json 反序列化失败", e);
    }
  }
}
