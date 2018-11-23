package jdk7.base;

import com.google.common.collect.Lists;
import com.google.common.truth.Truth;

import org.junit.Test;

import java.util.Objects;

/**
 * @author zeyu 2017/12/14
 */
public class MyObjectsTest {
    @Test
    public void testEqual() throws Exception {
        //Objects.deepEquals不会递归地去比较集合中的集合
        Truth.assertThat(Objects.deepEquals(Lists.newArrayList("123".getBytes(),"123".getBytes()),Lists.newArrayList("123".getBytes(),"123"
                .getBytes()))).isFalse();
        Truth.assertThat(Objects.deepEquals("123".getBytes(), "123".getBytes())).isTrue();
    }
}