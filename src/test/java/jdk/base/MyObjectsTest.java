package jdk.base;

import org.junit.Test;

import java.util.Objects;

/**
 * @author zeyu 2017/12/14
 */
public class MyObjectsTest {
    @Test
    public void testEqual() throws Exception {
        Objects.deepEquals("123".getBytes(),"123".getBytes());
    }
}