package jdk.base;

import com.google.common.truth.Truth;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zeyu 2017/11/28
 */
public class HelloPatternTest {
    @Test
    public void testPattern() throws Exception {
        Truth.assertThat("select * from a limit 100".matches(".*limit [\\d]+$")).isTrue();
        Truth.assertThat("select * from a where id < 1".matches(".*limit [\\d]+$")).isFalse();
    }

    @Test
    public void testSpecChar() throws Exception {
        String str = "wo31415927@\"_+A-,.$%&@163.com";
        String regex = "([0-9a-zA-Z_]+)@[-+,.\\u0024A-Z\\u0025a-z\\u0026_]+@([0-9a-zA-Z]+)\\u002e([a-zA-Z]+)";
        Matcher m1 = Pattern.compile(regex).matcher(str);
        System.out.println(m1.matches());
    }

    public String filter(String str) {
        String regex = "_(u[0-9a-fA-Z]{4})";
        Matcher m1 = Pattern.compile(regex).matcher(str);
        if (m1.find()) {
            return m1.replaceAll("\\\\$1");
        }
        return str;
    }

    /**
     * matches()是完全匹配
     * find()是部分匹配
     */
    @Test
    public void testEasyPattern() throws Exception {
        //[.]中的.含义为普通字符，变得无需转义了，而[]外的.则需要转义
        String r1 = filter("[._u005b_u005d]+@[0-9]+\\.[a-z]+");
        //非法的顺序，-符号变成了从.(0x2e)字符到$(0x24)字符的含义，而0x2e大于0x24
        //r1="[.-\\u0024]+";
        Matcher m1 = Pattern.compile(r1).matcher("..[]@163.com");
        System.out.println(m1.matches());
    }

    @Test
    public void testComplexPattern() throws Exception {
        String regex =
                "([0-9a-zA-Z_]+)@[\\u005c\\u007B\\u007D\\u002B\\u0022\\u002D\\u002C\\u002eA-Za-z_]+@([0-9a-zA-Z]+)\\.([a-zA-Z]+)";
        System.out.println(regex);
        String s = "wo31415927@\\{}\"_+A-,.$%&@163.com";
        Matcher matcher = Pattern.compile(regex).matcher(s);
        if (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                System.out.println("Group" + i + ":" + matcher.group(i));
            }
        }
        System.out.println("Replace:" + s.replaceAll(regex, ""));
    }
}