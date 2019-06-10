package jdk8.stream;

import com.google.common.collect.Lists;
import com.google.common.truth.Truth;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jdk.stream.MyStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summarizingInt;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static jdk.stream.MyStream.filterIntegers;

/**
 * @author Administrator 2018/11/25/025
 */
@Slf4j
public class MyStreamTest {
    List<Dish> menu =
            Lists.newArrayList(
                    new Dish("pork", 100, Dish.Type.MEAT), new Dish("beef", 150, Dish.Type.MEAT));
    List<String> title = Arrays.asList("Java8", "In", "Action", "In");

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
                redApple.and(a -> a.getWeight() > 150).or(a -> "green".equals(a.getColor()));
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        // g(f(x)),如unix中的命令流
        System.out.println(f.andThen(g).apply(1));
        // f(g(x))
        System.out.println(f.compose(g).apply(1));
    }

    /**
     * map:映射 reduce:归约
     */
    @Test
    public void testMapReduce() {
        Stream<String> s = title.stream();
        s.forEach(System.out::println);
        // 只能被消费一次,想要再次消费需要重新打开
        // s.forEach(System.out::println);
        // 多次map
        s.map(String::toLowerCase).map(String::length).forEach(System.out::println);
        // 找出字典中不同的字符,word->String[]->Stream<String>->合并流->去重->计数
        // 字符串转字符只能这么做
        System.out.println(
                title.stream().map(word -> word.split("")).flatMap(Arrays::stream).distinct().count());
        // 给定两个数字列表，如何返回所有的数对
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);
        List<int[]> pairs =
                numbers1
                        .stream()
                        // 相当于二重循环遍历
                        .flatMap(i -> numbers2.stream().map(j -> new int[]{i, j}))
                        .collect(toList());
        System.out.println(pairs);
        // 查找Optional、findAny,找到任意一个满足条件的字符串
        // findFirst和findAny.findAny对并行更友好，如果符合条件，尽量使用findFirst
        Optional<String> dish = title.stream().filter(str -> str.length() > 0).findAny();
        System.out.println(dish.orElse(""));
        // 有初始值求和
        int sum = numbers1.stream().reduce(0, Integer::sum);
        // 无初始值求和，若流中无元素，则返回absent;
        Optional<Integer> opSum = numbers1.stream().reduce(Integer::sum);
        IntStream intStream =
                title
                        .stream()
                        // 特化为IntStream(还有DoubleStream/LongStream)，避免拆箱
                        .mapToInt(String::length);
        int calories =
                intStream
                        // IntStream专有的方法，Stream<T>则没有此方法
                        .sum();
        OptionalInt opInt = intStream.reduce(Integer::sum);
        // 没有sum，只能使用reduce，且会自动拆箱
        //    title.stream().map(String::length).reduce(0,Integer::sum);
        // 装箱转换回一般流
        Stream<Integer> integerStream = intStream.boxed();
        // max无法使用默认值0，因为需要区分没有元素的流和最大元素为0的流
        OptionalInt optionalInt = intStream.max();
    }

    @Test
    public void testStreamCreate() {
        // of流
        Stream<String> stream = Stream.of("Java 8 ", "Lambdas ", "In ", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);
        Stream<String> emptyStream = Stream.empty();
        // range创建流
        IntStream intStream = IntStream.range(1, 100);
        // 数组创建流
        int[] numbers = {2, 3, 5, 7, 11, 13};
        int sum = Arrays.stream(numbers).sum();
        // 文件流
    /*long uniqueWords = 0;
    try (Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
      // 计算文件中不同的单词数
      uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" "))).distinct().count();
    } catch (IOException e) {
    }*/
        // 无限流
        // 对seed反复执行UnaryOperator<T>(一元运算符，T和R数据类型一致的Function)
        Stream.iterate(0, n -> n + 2).limit(10).forEach(System.out::println);
        // 根据Supplier生成
        Stream.generate(Math::random).limit(5).forEach(System.out::println);
    }

    @Test
    public void testComparator() {
        System.out.println(menu);
        menu.sort(comparingInt(Dish::getCalories).reversed());
        System.out.println(menu);
    }

    @Test
    public void testCollect() {
        Comparator<Dish> dishCaloriesComparator = comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));
        mostCalorieDish = menu.stream().max(dishCaloriesComparator);
        int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
        // 统计对象，里面包含count、sum、avg等信息
        IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
        // 若插入null对象，则执行Dish::getName时会报空指针
        // 若插入name为null的对象，则joining会按null字符串处理
        menu.add(0, new Dish(null, -1, null));
        System.out.println(menu.stream().map(Dish::getName).collect(joining(",")));
        // join
        String shortMenu = menu.stream().map(Dish::getName).collect(joining());
        // join的reduce版本，可以替换为非collect的reduce
        shortMenu = menu.stream().map(Dish::getName).collect(reducing((s1, s2) -> s1 + s2)).get();
        // reduce支持在其内部map
        shortMenu = menu.stream().collect(reducing("", Dish::getName, (s1, s2) -> s1 + s2));
        menu.remove(0);
        // (u, v)->v 表示当遇到先后放入map的key相同时，保留后面一个key对应的value
        Map<Dish.Type, String> typeMap =
                menu.stream()
                        .collect(
                                Collectors.toMap(Dish::getType, Dish::getName, (u, v) -> v, LinkedHashMap::new));
        System.out.println(typeMap);
        // 空String收集到空List
        List<String> strList = new HashSet<String>().stream().collect(toList());
        Truth.assertThat(strList).isNotNull();
        Truth.assertThat(strList).isEmpty();
    }

    @Test
    public void testGroupBy() {
        // groupBy可以指定不同的收集器，以便分组后灵活转换收集的对象
        // 多级分组，先按Type分组，然后再按CaloricLevel进行分组
        menu.stream()
                .collect(
                        groupingBy(
                                Dish::getType,
                                groupingBy(
                                        dish -> {
                                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                            else return CaloricLevel.FAT;
                                        })));
        Map<Dish.Type, Long> typesCount = menu.stream().collect(groupingBy(Dish::getType, counting()));
        Map<Dish.Type, Dish> mostCaloricByType =
                menu.stream()
                        .collect(
                                groupingBy(
                                        Dish::getType,
                                        // 先进行收集然后进行转换
                                        collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));
        // 先进行收集然后进行转换
        mostCaloricByType =
                menu.stream()
                        .collect(
                                Collectors.toMap(
                                        Dish::getType,
                                        Function.identity(),
                                        BinaryOperator.maxBy(comparingInt(Dish::getCalories))));
        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
                menu.stream()
                        .collect(
                                groupingBy(
                                        Dish::getType,
                                        // 分组后对组内的每一个元素先进行转换然后收集
                                        mapping(
                                                dish -> {
                                                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                                    else return CaloricLevel.FAT;
                                                },
                                                // 可以toSet,也可以如下指定集合的类型
                                                toCollection(HashSet::new))));
        // 分区是boolean作为分组键的特殊分组
        Map<Boolean, List<Dish>> partitionedMenu =
                menu.stream().collect(partitioningBy(Dish::isVegetarian));
    }

    @Test
    public void testDefCollector() {
        menu.stream().collect(new ToListCollector<>());
        // 简易写法
        menu.stream().collect(ArrayList::new, List::add, List::addAll);
    }

    /**
     * 获取勾股数
     */
    @Test
    public void testPythagoreanTriple() {
        int max = 100;
        Stream<Integer> s1 = IntStream.range(1, max).boxed();
        Stream<double[]> resultStream =
                s1.flatMap(
                        i ->
                                IntStream.range(i, max)
                                        // .box()
                                        // IntStream map()默认返回的是int，所以需要box()或者使用mapToObj()也能达到同样的效果
                                        .mapToObj(j -> new double[]{i, j, Math.sqrt(i * i + j * j)}))
                        .filter(arr -> 0 == arr[2] % 1);
        resultStream.limit(10).forEach(arr -> System.out.println(Arrays.toString(arr)));
    }

    @Test
    public void testTmp() {
        double d = 3.14;
        System.out.println(0 == d % 1);
        System.out.println((int) d);
    }

    @ToString
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Dish {
        protected String name;
        protected int calories;
        protected Type type;

        public boolean isVegetarian() {
            return false;
        }

        public enum Type {
            MEAT,
            FISH,
            OTHER;
        }
    }

    public enum CaloricLevel {
        NORMAL,
        FAT,
        DIET
    }

    public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {
        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }

        /**
         * 标记为IDENTITY_FINISH时会忽略finisher逻辑
         */
        @Override
        public Function<List<T>, List<T>> finisher() {
            return Function.identity();
        }

        /**
         * 并行归约时，合并A,A->A
         */
        @Override
        public BinaryOperator<List<T>> combiner() {
            return (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
        }
    }

    public static boolean isPrime(List<Integer> primes, int candidate) {
        if (1 == candidate) {
            return false;
        }
        int candidateRoot = (int) Math.sqrt((double) candidate);
        // 会遍历全部质数集合，略差
        return primes.stream().noneMatch(p -> p <= candidateRoot && candidate % p == 0);
    }

    public class ToPrimeListCollector implements Collector<Integer, List<Integer>, List<Integer>> {
        @Override
        public Supplier<List<Integer>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<Integer>, Integer> accumulator() {
            return (ts, t) -> {
                // 累加器中能够取到过程数据（已判断是质数的部分集合）
                if (isPrime(ts, t)) {
                    ts.add(t);
                }
            };
        }

        /**
         * 标记为IDENTITY_FINISH时会忽略finisher逻辑
         */
        @Override
        public Function<List<Integer>, List<Integer>> finisher() {
            return Function.identity();
        }

        /**
         * 并行归约时，合并A,A->A
         */
        @Override
        public BinaryOperator<List<Integer>> combiner() {
            // 需要依赖中间结果，不能并行
            throw new UnsupportedOperationException();
      /*return (list1, list2) -> {
        list1.addAll(list2);
        return list1;
      };*/
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
        }
    }

    @Test
    public void testPrime() {
        // 不能并行
        System.out.println(IntStream.rangeClosed(1, 1000).boxed().collect(new ToPrimeListCollector()));
    }

    @Test
    public void testParallelStream() {
        // 对顺序流调用parallel方法并不意味着流本身有任何实际的变化。它在内部实际上就是设了一个boolean标志
        // 以最后一个设置的标志生效
        menu.stream().parallel().sequential();
        // 设置并行流使用的线程数，默认是Runtime.getRuntime().availableProcessors()，该设置为全局设置，无法针对某一个并行流设置并行度
        // N_threads = N_cpu * U_cpu * (1 + W / C),W/C为 单个行为等待的比例，如IO等待的时间越长，那么W/C越大，可以开更多的线程
        // System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","12");
        // 并行化过程本身需要对流做递归划分，把每个子流的归纳操作分配到不同的线程，然后把这些操作的结果合并成一个值。但在多个内核之间
        // 移动数据的代价也可能比你想的要大，所以很重要的一点是要保证在内核中并行执行工作的时间比在内核之间传输数据的时间长。
        // 可以把有序流转换为无序流
        menu.stream().unordered().parallel();
        // LinkedList\Stream.iterate()不适合拆分流，前者需要遍历，后者需要下一个结果生成依赖上一个结果
    }

    public static long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1).limit(n).parallel().reduce(0L, Long::sum);
    }

    @Test
    public void testPeek() {
        List<Integer> result =
                IntStream.rangeClosed(1, 20)
                        // 打印流中的过程值
                        .peek(x -> System.out.println("from stream: " + x))
                        .map(x -> x + 17)
                        .peek(x -> System.out.println("after map: " + x))
                        .filter(x -> x % 2 == 0)
                        .peek(x -> System.out.println("after filter: " + x))
                        .limit(3)
                        .peek(x -> System.out.println("after limit: " + x)).boxed()
                        .collect(toList());
        System.out.println(result);
    }
}
