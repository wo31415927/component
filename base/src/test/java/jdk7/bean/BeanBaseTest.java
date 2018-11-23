package jdk7.bean;

import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

/**
 * @author cctv 2017/12/1
 */
public class BeanBaseTest {
    @Test
    public void test() {
        // Introspector caches BeanInfo classes for better performance
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(TableInfo.class);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        System.out.println(beanInfo.getPropertyDescriptors());
    }
}