package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/** @author chenxiang */
public class JsonUtils {
  public static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
  public static final JsonParser JSON_PARSER = new JsonParser();

  /**
   * 将一个json字符串转换成一个Object对象
   *
   * @param jsonStr
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T fromJson(String jsonStr, Class<T> clazz, ObjectMapper objectMapper) {
    if (jsonStr == null) {
      return null;
    }
    try {
      return objectMapper.readValue(jsonStr, clazz);
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
  public static String toJsonStr(Object object, ObjectMapper objectMapper) {
    if (null == object) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("json序列化失败", e);
    }
  }

  /**
   * 讲一个json字符串转成map对象
   *
   * @param jsonStr
   * @return
   */
  public static <K, V> Map<K, V> parseMap(
      String jsonStr, Class<K> clazzK, Class<V> clazzV, ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(jsonStr, new TypeReference<Map<K, V>>() {});
    } catch (IOException e) {
      throw new RuntimeException("json 序列化失败", e);
    }
  }

  /**
   * 将一个json字符串转成list对象
   */
  public static <T> T fromJson(String json, TypeReference<T> toValueTypeRef, ObjectMapper objectMapper) {
    try {
      return objectMapper.readValue(json, toValueTypeRef);
    } catch (IOException e) {
      throw new RuntimeException("json 反序列化失败", e);
    }
  }
}
