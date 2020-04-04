package tmp;

/** @author chenxiang 2019/6/24/024 */
public class TmpBase {
  /**
   * 测试继承，静态代码的执行顺序
   *
   * @param args
   */
  public static void main(String[] args) {
    Father father = new Chlid();
  }

  public static class Father {
    {
      System.out.println("Father's block");
    }

    static {
      System.out.println("Father's static block");
    }
  }

  public static class Chlid extends Father {
    {
      System.out.println("Chlid's block");
    }

    static {
      System.out.println("Chlid's static block");
    }
  }
}
