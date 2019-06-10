package jdk.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenxiang 2018/11/20
 */
public class MyStream {
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

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Apple {
        protected String color;
        protected int weight;
    }
}
