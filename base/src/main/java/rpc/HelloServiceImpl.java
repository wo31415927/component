package rpc;

/** @author chenxiang 2019/7/9/009 */
public class HelloServiceImpl implements HelloService {
  @Override
  public String hello() {
    return "Hello-";
  }

  @Override
  public String hello(String name) {
    return "Hello-" + name;
  }
}
