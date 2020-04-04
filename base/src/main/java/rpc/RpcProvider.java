package rpc;

/** @author chenxiang 2019/7/9/009 */
public class RpcProvider {
  public static void main(String[] args) throws Exception {
    HelloService service = new HelloServiceImpl();
    RpcFramework.export(HelloService.class, service);
    RpcFramework.startProvider(1234);
  }
}
