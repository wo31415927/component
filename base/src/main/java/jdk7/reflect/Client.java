package jdk7.reflect;

import java.lang.reflect.Proxy;

/**
 * zeyu
 * 2017/11/9
 */
public class Client {
    public static void main(String[] args) {
        IPerson person = new Person("Jack", 0);
        IPerson proxyPerson = (IPerson) Proxy.newProxyInstance(person.getClass().getClassLoader(), person.getClass().getInterfaces(), new OwnerHandler
                (person));
        try {
            System.out.println(Proxy.isProxyClass(proxyPerson.getClass()));
            System.out.println(proxyPerson.getName());
            System.out.println(proxyPerson.getGender());
            proxyPerson.setFlower(1);
        }catch (Exception e){
            System.out.println(e.getCause().getMessage());
        }
    }
}
