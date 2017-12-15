package jdk.base;

import com.google.common.truth.Truth;

import org.junit.Test;

/**
 * @author zeyu 2017/12/15
 */
public class TypeTest {

    @Test
    public void testDouble() throws Exception {
        double d1 = 4503599627370496.00;
        //包含科学计数法的字符串不影响double类型的转换
        double d2 = Double.parseDouble("4.503599627370496E15");
        Truth.assertThat(d1).isEqualTo(d2);
    }
}