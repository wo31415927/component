package jdk7.base;

import org.junit.Test;

import lombok.AllArgsConstructor;

/** @author cctv 2017/11/24 */
public class CloneTest {
  /**
   * 测试基本类型的clone
   * 支持enum
   * clone的类必须声明Cloneable（不然会报不支持Cloneable的异常），虽然jdk7以后该接口中无任何方法
   *
   * @throws Exception
   */
  @Test
  public void testClone() throws Exception {
    DbConnection dbCon = new DbConnection("127.0.0.1", 80, "chen", "mig", "", DbType.DBPROXY);
    System.out.println(dbCon.clone());
  }

  @AllArgsConstructor
  private class DbConnection implements Cloneable {
    protected String host;
    protected int port;
    protected String username;
    protected String pwd;
    protected String schema;
    protected DbType dbType;

    @Override
    public DbConnection clone() {
      try {
        return (DbConnection) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public String toString() {
      return "DbConnection{"
          + "host='"
          + host
          + '\''
          + ", port="
          + port
          + ", username='"
          + username
          + '\''
          + ", pwd='"
          + pwd
          + '\''
          + ", schema='"
          + schema
          + '\''
          + ", dbType="
          + dbType
          + '}';
    }
  }

  private enum DbType {
    NONE,
    MYSQL,
    ORACLE,
    DBPROXY
  }
}
