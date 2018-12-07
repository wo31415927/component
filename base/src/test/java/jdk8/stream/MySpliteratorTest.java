package jdk8.stream;

import org.junit.Test;

import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** @author chenxiang 2018/12/7 */
public class MySpliteratorTest {
  @Test
  public void testSpliterator() {
    String SENTENCE = "Hello World My Lady Gaga.";
    Stream<Character> stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
    MySpliterator.WordCounter wordCounter =
        stream
            // 使用默认的spliterator对字符序列进行拆分（没有按空格进行拆会影响统计结果）
            .parallel()
            .reduce(
                // 使用new WordCounter(0, true)来初始化，以便在遍历第一个字符时统计第一个word
                new MySpliterator.WordCounter(0, true),
                MySpliterator.WordCounter::accumulate,
                MySpliterator.WordCounter::combine);
    System.out.println(wordCounter.getCounter());
    // 使用自定义的Spliterator按空格进行拆分
    Spliterator<Character> spliterator = new MySpliterator.WordCounterSpliterator(SENTENCE);
    stream = StreamSupport.stream(spliterator, true);
    wordCounter =
        stream.reduce(
            // 使用new WordCounter(0, true)来初始化，以便在遍历第一个字符时统计第一个word
            new MySpliterator.WordCounter(0, true),
            MySpliterator.WordCounter::accumulate,
            MySpliterator.WordCounter::combine);
    System.out.println(wordCounter.getCounter());
  }
}
