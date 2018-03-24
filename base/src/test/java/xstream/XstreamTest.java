package xstream;

import com.google.common.truth.Truth;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.junit.Test;

/** @author cctv 2017/12/9 */
public class XstreamTest {
  @Test
  public void testFromXml() {
    XStream xstream = new XStream(new PureJavaReflectionProvider()); // .xStream;
    xstream.processAnnotations(Person.class);
    Person joe = new Person("Joe", "Walnes");
    joe.setType("All");
    joe.getPhones().add(joe.new PhoneNumber(123, "1234-456"));
    joe.getPhones().add(joe.new PhoneNumber(123, "9999-999"));
    // 里层的PhoneNumber对象有名字，所以不用起别名，最外层的Person没有名字，需要起别名，否则使用类名
    // fromXml时需要知道目标类，要么标签使用class全路径，要么设置alias
    xstream.alias("person", Person.class);
    xstream.alias("PhoneNumber", Person.PhoneNumber.class);
    // System.out.println(xstream.toXML(joe));
    // 标签名字和类属性名称需要对应
    String xml =
        "<person bio=\"All\"><firstname>Joe</firstname><lastname>Walnes</lastname><columns></columns></person>";
    System.out.println(xml);
    // Person.List<PhoneNumber>有默认值，但是映射之后会被置为null,设置@XStreamOmitField也不管用
    Person jack = (Person) xstream.fromXML(xml);
    System.out.println(jack);
    Truth.assertThat(jack.getPhones()).isNotNull();
  }
}
