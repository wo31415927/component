package jdk.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 # 可以在任意路径启动注册表服务
 # 必须带上,否则会报ClassNotFound
 start rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false
 # -cp和-Djava.rmi.server.codebase使用绝对路径
 start java -cp j:/rmi/jdk/rmi/des/ -Djava.rmi.server.codebase=file:j:/rmi/jdk/rmi/des/ -Djava.rmi.server.useCodebaseOnly=false Server
 # 成功调用
 java -cp ./ Client
 * zeyu
 * 2017/11/8
 */
public class Server implements Hello {
    protected Server() throws RemoteException {
    }

    public static void main(String[] args) {
        try {
            Server obj = new Server();
            Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Hello", stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello";
    }
}
