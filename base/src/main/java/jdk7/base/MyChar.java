package jdk7.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cctv 2018/2/3
 */
public class MyChar {
    static final Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

    /**
     * 将\\uxxxx\\uxxxx转换为对应的字符,正则匹配替换方式
     * @param s
     * @return
     */
    public static String decodeUnicode1(String s) {
        Matcher m = reUnicode.matcher(s);
        //整个替换逻辑就相当于s.replaceAll(xx)
        StringBuffer sb = new StringBuffer(s.length());
        while (m.find()) {
            //将匹配的字符串替换，并将最后一个匹配的字符串之前的内容写入sb
            m.appendReplacement(sb,
                    Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        //将最后一个匹配的字符串之后的内容写入sb
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将\\uxxxx\\uxxxx转换为对应的字符,逐字扫描方式
     * @param s
     * @return
     */
    public static String decodeUnicode2(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        //|=是多余的，每位数字直接<< 4即可，更优的写法参见Properties.loadConvert()
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    } else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }


}
