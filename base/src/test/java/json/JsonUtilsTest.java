package json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.truth.Truth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class JsonUtilsTest {
  public static ObjectMapper objectMapper = new ObjectMapper();

  static {
    // 序列化时设置type信息，和原来的属性放在一起，但不会对使用了usingKey的class生成type信息
    /*objectMapper.enableDefaultTyping(
    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);*/
    // 将特殊的序列化和反序列化类添加到 objectMapper
    SimpleModule simpleModule = new SimpleModule("TableInfo", Version.unknownVersion());
    simpleModule.addKeySerializer(TableInfo.class, new TableInfoKeySerializer());
    simpleModule.addKeyDeserializer(TableInfo.class, new TableInfoKeyDeSerializer());
    objectMapper.registerModule(simpleModule);
  }

  @Test
  public void parseMap() throws IOException {
    TableInfo t1 = new TableInfo("s1", "t1");
    TableInfo t2 = new TableInfo("s2", "t2");
    // 代码段中定义的map，非class.field(才能用@JsonSerializer注解usingKey)，只能注册Module解决
    Map<TableInfo, TableInfo> map1 = Maps.newConcurrentMap();
    map1.put(t1, t1);
    map1.put(t2, t2);
    // 即便打开了defaultTyping，但不会对使用了usingKey的class生成type信息
    String json = objectMapper.writeValueAsString(map1);
    System.out.println(json);
    Map<TableInfo, TableInfo> map2 =
        objectMapper.readValue(json, new TypeReference<Map<TableInfo, TableInfo>>() {});
    System.out.println(map2);
    Truth.assertThat(map1).isEqualTo(map2);
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class TableInfo {
    protected String schemaName;
    protected String name;
  }

  public static class TableInfoKeySerializer extends StdSerializer<TableInfo> {
    public TableInfoKeySerializer() {
      this(null);
    }

    public TableInfoKeySerializer(Class<TableInfo> t) {
      super(t);
    }

    @Override
    public void serialize(TableInfo value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeFieldName(value.getSchemaName() + "." + value.getName());
    }
  }

  public static class TableInfoKeyDeSerializer extends KeyDeserializer {
    @Override
    public TableInfo deserializeKey(String key, DeserializationContext ctxt) {
      String[] arr = key.split("\\.");
      Preconditions.checkArgument(2 == arr.length);
      return new TableInfo(arr[0], arr[1]);
    }
  }
}
