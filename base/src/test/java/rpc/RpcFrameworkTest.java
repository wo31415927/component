package rpc;

import com.google.common.base.Preconditions;
import com.google.common.truth.Truth;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/7/13/013 */
@Slf4j
public class RpcFrameworkTest {
  @Test
  public void testInetAddress() {
    InetSocketAddress inetSocketAddress1 = new InetSocketAddress("127.0.0.1", 1234);
    InetSocketAddress inetSocketAddress2 = new InetSocketAddress("127.0.0.1", 1234);
    Truth.assertThat(inetSocketAddress1).isEqualTo(inetSocketAddress2);
  }

  @Test
  public void testInvocation()
      throws InvocationTargetException, IllegalAccessException {
    HelloService helloService = new HelloServiceImpl();
    Method[] methods = HelloServiceImpl.class.getMethods();
    Method desMethod = null;
    for (Method method : methods) {
      if (method.getName().equals("hello")) {
        desMethod = method;
        break;
      }
    }
    Preconditions.checkNotNull(desMethod);
    RpcFramework.RpcInvocation rpcObject = new RpcFramework.RpcInvocation();
    // 参数数量不匹配报wrong number of arguments
    rpcObject.setParams(new Object[] {"World"});
    log.info(desMethod.invoke(helloService, rpcObject.getParams()).toString());
  }
}
