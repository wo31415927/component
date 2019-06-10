package jdk.io;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;

/** @author cctv 2018/2/11 */
public class IOUtils {
  /**
   * resize scanner's charBuf size
   * @param scanner
   * @param size
   * @return
   */
  public static Scanner sizeScanner(Scanner scanner, int size) {
    if (size <= 1024) {
      return scanner;
    }
    Objects.requireNonNull(scanner);
    Preconditions.checkState(0 == (size & (size - 1)), "size is not a power of two");
    CharBuffer charBuf = CharBuffer.allocate(size);
    charBuf.limit(0);
    Matcher matcher = scanner.delimiter().matcher(charBuf);
    matcher.useTransparentBounds(true);
    matcher.useAnchoringBounds(false);
    try {
      Field buf = Scanner.class.getDeclaredField("buf");
      buf.setAccessible(true);
      buf.set(scanner, charBuf);
      Field mField = Scanner.class.getDeclaredField("matcher");
      mField.setAccessible(true);
      mField.set(scanner, matcher);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      Throwables.propagate(e);
    }
    return scanner;
  }

  /**
   * Manually check scanner has IOException
   * @param scanner
   * @throws IOException
   */
  public static boolean checkException(Scanner scanner) throws IOException {
    if (null != scanner.ioException()) {
      throw scanner.ioException();
    }
    return true;
  }
}
