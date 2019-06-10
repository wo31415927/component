package jdk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * zeyu
 * 2017/11/8
 */
public interface Hello extends Remote {
    String sayHello() throws RemoteException;
}
