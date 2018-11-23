package jdk7.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

/**
 * zeyu
 * 2017/11/9
 */
@RequiredArgsConstructor
public class OwnerHandler implements InvocationHandler {
    protected final IPerson person;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().indexOf("setEgg") > -1 || method.getName().indexOf("setFlower") > -1){
            throw new IllegalAccessException("Owner should not modify his own flower or egg!");
        }
        return method.invoke(person,args);
    }
}
