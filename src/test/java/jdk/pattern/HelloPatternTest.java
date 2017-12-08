package jdk.pattern;

import com.google.common.truth.Truth;

import org.junit.Test;

/**
 * @author zeyu 2017/11/28
 */
public class HelloPatternTest {
    @Test
    public void testPattern() throws Exception {
        Truth.assertThat("select * from a limit 100".matches(".*limit [\\d]+$")).isTrue();
        Truth.assertThat("select * from a where id < 1".matches(".*limit [\\d]+$")).isFalse();
    }
}