package jdk8.stream;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static jdk8.stream.MyStream.filterIntegers;

/**
 * @author Administrator 2018/11/25/025
 */
public class MyStreamTest {
    /**
     * lambda表达式
     */
    @Test
    public void testLambda() {
        // filterIntegers声明参数为Predicate时，可以直接传入参数为定义类型，返回为Boolean的函数引用或lambda
        List<Integer> inventory = Lists.newArrayList(1, -2, 3);
        // 短的逻辑用lambda
        System.out.println(filterIntegers(inventory, (a) -> a > 0));
        // 长的逻辑用函数引用
        System.out.println(filterIntegers(inventory, MyStream::isBelow));
        Arrays.asList(1, 2, 3);
    }

    /**
     * 函数引用,有3种方式
     */
    @Test
    public void testMethodRef() {
        // 1. 静态函数引用
        //        (String s) -> Integer.parseInt(s);
        Function<String, Integer> stringToInteger = Integer::parseInt;
        // 2. 指 向 任 意 类 型 实 例 方 法 的 方 法 引 用
        // (list, element) -> list.contains(element);
        BiPredicate<List<String>, String> contains = List::contains;
        // 参数顺序不能颠倒
        //        BiPredicate<String, List<String>> contains1 = List::contains;
        String str = "";
        // 3. 闭包，直接对某一个外部对象设置函数引用
        Function<Integer, String> len = str::substring;
        // 构造函数函数引用
        Function<String, Integer> s = Integer::new;
    }

    /**
     * 组合
     */
    @Test
    public void testCombound() {
        Predicate<MyStream.Apple> redApple = (apple) -> apple.getColor().equals("red");
        // a and b or c = (a && b) || c,是按从左到右的顺序进行处理，是不是感觉和sql中的where条件比较像？
        Predicate<MyStream.Apple> redAndHeavyAppleOrGreen =
                redApple.and(a -> a.getWeight() > 150)
                        .or(a -> "green".equals(a.getColor()));
    }
}