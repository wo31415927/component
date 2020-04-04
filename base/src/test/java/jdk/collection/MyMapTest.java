package jdk.collection;

import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;

import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author cctv 2018/1/5
 */
public class MyMapTest {
    Map<String, String> map;

    public void put() {
        map.put("apple", "苹果");
        map.put("watermelon", "西瓜");
        map.put("banana", "香蕉");
        map.put("peach", "桃子");
    }

    public void print() {
        Iterator iter = map.entrySet().iterator();
        System.out.println("---");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    @Test
    public void testHash() throws Exception {
        System.out.println(System.identityHashCode(null));
    }

    @Test
    public void testHashMap() throws Exception {
        map = new HashMap<String, String>();
        put();
        print();
    }

    @Test
    public void testConcurrentHashMap() throws Exception {
        map = new HashMap<String, String>();
        put();
        print();
    }

    @Test
    public void testLinkedHashMap() throws Exception {
        map = new LinkedHashMap<String, String>();
        put();
        print();
        map.get("apple");
        print();
    }

    @Test
    public void testLinkedHashMapAccessOrder() throws Exception {
        map = new LinkedHashMap<String, String>(16, 0.75F, true);
        put();
        print();
        //get之后，链表的顺序发生变化
        map.get("apple");
        print();
    }

    @Test
    public void testLinkedTreeMap() throws Exception {
        map = Maps.newTreeMap();
        put();
        print();
        map = new LinkedTreeMap<String, String>();
        put();
        print();
    }
}
