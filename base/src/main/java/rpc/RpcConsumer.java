package rpc;

import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/7/9/009 */
@Slf4j
public class RpcConsumer {

  public static void main(String[] args) throws Exception {
    HelloService service = (HelloService) RpcFramework.refer(HelloService.class, "127.0.0.1", 1234);
    for (int i = 0; i < 5; i++) {
      String hello = service.hello("World" + i);
      log.info("rpcResult:{}", hello);
      Thread.sleep(1000);
    }
  }
}
