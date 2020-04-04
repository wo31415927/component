package jdk.spi;

import org.junit.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ServiceLoader;

/** @author chenxiang 2019/8/6/006 */
public class MySpiTest {
  /** 模拟java.sql.Driver的spi加载过程，观察内部逻辑 */
  @Test
  public void testServiceLoad() {
    ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
    Iterator<Driver> driversIterator = loadedDrivers.iterator();
    while (driversIterator.hasNext()) {
      driversIterator.next();
    }
    driversIterator = loadedDrivers.iterator();
    while (driversIterator.hasNext()) {
      /**
       * 获取所有实现类的结果 com.mysql.jdbc.Driver@5204062d com.mysql.fabric.jdbc.FabricMySQLDriver@4fcd19b3
       * org.postgresql.Driver@376b4233
       */
      System.out.println(driversIterator.next().toString());
    }
  }

  /** 测试DriverManager中使用的spi加载 */
  @Test
  public void testDriverManagerSpiLoad() throws SQLException {
    String url = "jdbc:mysql://localhost:3306/test";
    Connection con = DriverManager.getConnection(url, "dev", "Dev_1234");
    System.out.println(con.getSchema());
  }
}
