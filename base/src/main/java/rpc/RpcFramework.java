package rpc;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static Utils.JsonUtils.OBJECT_MAPPER;

/** @author chenxiang 2019/7/9/009 */
@Slf4j
public class RpcFramework {
  public static ConcurrentMap<String, Object> providerRpcContextMap = Maps.newConcurrentMap();
  public static ConcurrentMap<Pair<String, Integer>, Socket> clientSocketMap =
      Maps.newConcurrentMap();
  public static ServerSocket serverSocket;

  public static synchronized void startProvider(int port) throws IOException {
    Preconditions.checkState(port > 0);
    Preconditions.checkState(null == serverSocket, "dup init.");
    ServerSocket serverSocket = new ServerSocket(port);
    while (true) {
      Socket socket = serverSocket.accept();
      CompletableFuture.runAsync(
          () -> {
            while (true) {
              try {
                RpcInvocation rpcObject =
                    OBJECT_MAPPER.readValue(socket.getInputStream(), RpcInvocation.class);
                Object invocationObj;
                if (null == (invocationObj = providerRpcContextMap.get(rpcObject.getClassName()))) {
                  throw new RuntimeException("Not found rpc impl.");
                }
                Preconditions.checkNotNull(rpcObject.getMethodName());
                Object[] params =
                    null == rpcObject.getParams() ? new Object[0] : rpcObject.getParams();
                Class[] paramTypes = new Class[params.length];
                for (int i = 0; i < params.length; i++) {
                  paramTypes[i] = params[i].getClass();
                }
                Method method =
                    invocationObj.getClass().getMethod(rpcObject.getMethodName(), paramTypes);
                if (null == method) {
                  throw new RpcException("Not found rel method.");
                }
                OBJECT_MAPPER.writeValue(
                    socket.getOutputStream(),
                    new RpcResult(method.invoke(invocationObj, rpcObject.getParams()), null));
              } catch (SocketException e) {
                // 发现连接中断时退出
                log.warn("Socket error,break.", e);
                break;
              } catch (Exception e) {
                log.warn("rpc call error.", e);
                try {
                  OBJECT_MAPPER.writeValue(
                      socket.getOutputStream(),
                      new RpcResult(null, new RpcException(e.getMessage())));
                } catch (Exception e1) {
                  log.warn("Socket error,break.", e1);
                  break;
                }
              }
            }
          });
    }
  }

  public static void export(Class if0, Object obj) {
    // if0为接口且obj实现if0
    Preconditions.checkState(
        null != if0
            && if0.isInterface()
            && null != obj
            && Lists.newArrayList(obj.getClass().getInterfaces()).contains(if0));
    providerRpcContextMap.put(if0.getName(), obj);
  }

  public static Object refer(Class c, String host, int port) {
    Preconditions.checkState(c.isInterface(), "must refer interface.");
    return Proxy.newProxyInstance(
        RpcFramework.class.getClassLoader(),
        new Class[] {c},
        (proxy, method, args) -> {
          log.debug("begin remote invoke.");
          for (Method interfaceMethod : c.getMethods()) {
            if (!method.getName().equals(interfaceMethod.getName())) {
              // 排除动态代理生成的接口外的方法(如toString(),idea需要打印service内容会自动调用toString())
              log.debug("omit method:{}.{}", c.getName(), method.getName());
              return null;
            }
          }
          Pair<String, Integer> endPoint = new ImmutablePair<>(host, port);
          Socket socket = getSocket(endPoint);
          RpcInvocation rpcInvocation = new RpcInvocation(c.getName(), method.getName(), args);
          OBJECT_MAPPER.writeValue(socket.getOutputStream(), rpcInvocation);
          log.info("call rpcInvocation:{}.", rpcInvocation);
          RpcResult rpcResult = OBJECT_MAPPER.readValue(socket.getInputStream(), RpcResult.class);
          if (null != rpcResult.getException()) {
            log.error("", rpcResult.getException());
            return null;
          }
          return rpcResult.getResult();
        });
  }

  @NotNull
  private static Socket getSocket(Pair<String, Integer> endPoint) throws IOException {
    Socket socket = clientSocketMap.get(endPoint);
    if (null == socket || socket.isClosed()) {
      socket = new Socket();
      // readTimeout
      socket.setSoTimeout(5000);
      // 定时心跳检测，探测周期与os有关
      socket.setKeepAlive(true);
      // new InetSocketAddress()包含校验host非空和port范围
      socket.connect(new InetSocketAddress(endPoint.getKey(), endPoint.getValue()), 5000);
      clientSocketMap.put(endPoint, socket);
    }
    return socket;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class RpcInvocation {
    String className;
    String methodName;
    Object[] params;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class RpcResult {
    Object result;
    RpcException exception;
  }

  public static class RpcException extends RuntimeException {
    public RpcException(String msg) {
      super(msg);
    }
  }
}
