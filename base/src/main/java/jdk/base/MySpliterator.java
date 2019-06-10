package jdk.base;

import java.util.Spliterator;
import java.util.function.Consumer;

/** @author chenxiang 2018/12/7 */
public class MySpliterator {
  static class WordCounter {
    private final int counter;
    private final boolean lastSpace;

    public WordCounter(int counter, boolean lastSpace) {
      this.counter = counter;
      this.lastSpace = lastSpace;
    }

    public WordCounter accumulate(Character c) {
      if (Character.isWhitespace(c)) {
        return lastSpace ? this : new WordCounter(counter, true);
      } else {
        return lastSpace ? new WordCounter(counter + 1, false) : this;
      }
    }

    public WordCounter combine(WordCounter wordCounter) {
      return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
    }

    public int getCounter() {
      return counter;
    }
  }

  static class WordCounterSpliterator implements Spliterator<Character> {
    private final String string;
    private int currentChar = 0;

    public WordCounterSpliterator(String string) {
      this.string = string;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
      action.accept(string.charAt(currentChar++));
      return currentChar < string.length();
    }

    @Override
    public Spliterator<Character> trySplit() {
      int currentSize = string.length() - currentChar;
      if (currentSize < 10) {
        return null;
      }
      for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
        if (Character.isWhitespace(string.charAt(splitPos))) {
          Spliterator<Character> spliterator =
              new WordCounterSpliterator(string.substring(currentChar, splitPos));
          currentChar = splitPos;
          return spliterator;
        }
      }
      return null;
    }

    @Override
    public long estimateSize() {
      return string.length() - currentChar;
    }

    @Override
    public int characteristics() {
      return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }
  }
}
