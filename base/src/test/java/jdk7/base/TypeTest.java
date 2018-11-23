package jdk7.base;

import com.google.common.truth.Truth;

import org.junit.Test;

import java.math.BigInteger;

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

    @Test
    public void testBigInteger() throws Exception {
        BigInteger bi1 = new BigInteger("100");
        //2进制，1100100
        System.out.println(bi1.toString(2));
    }

    @Test
    public void testTrimString() throws Exception {
        String s = "\na\r";
        //2进制，1100100
        System.out.println("0" + s.trim() + "1");
    }

    @Test
    public void testReplaceString() throws Exception {
        String s = "\na\r";
        //2进制，1100100
        System.out.println("0" + s.trim() + "1");
    }
}