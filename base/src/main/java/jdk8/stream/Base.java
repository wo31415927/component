package jdk8.stream;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/** @author chenxiang 2018/11/20 */
public class Base {
  static List<Integer> filterIntegers(List<Integer> inventory, Predicate<Integer> p) {
    List<Integer> result = new ArrayList<>();
    for (Integer Integer : inventory) {
      if (p.test(Integer)) {
        result.add(Integer);
      }
    }
    return result;
  }

  public static boolean isBelow(Integer a) {
    return a < 0;
  }

  public static void main(String[] args) {
    // filterIntegers声明参数为Predicate时，可以直接传入参数为定义类型，返回为Boolean的函数引用或lambda
    List<Integer> inventory = Lists.newArrayList(1, -2, 3);
    // 短的逻辑用lambda
    System.out.println(filterIntegers(inventory, (a) -> a > 0));
    // 长的逻辑用函数引用
    System.out.println(filterIntegers(inventory, Base::isBelow));
    Arrays.asList(1,2,3);
  }
}
