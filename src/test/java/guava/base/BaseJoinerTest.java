package guava.base;

import com.google.common.base.Joiner;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author cctv 2017/12/1
 */
public class BaseJoinerTest {
    public static final Joiner joinerComma = Joiner.on(",");

    @Test
    public void testBuffer() throws IOException, InterruptedException {
        Object byteBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println(System.identityHashCode(byteBuffer));
    }

    @Test
    public void testJoinByte() throws IOException, InterruptedException {
        byte[] bs1 = "HelloWorld".getBytes();
        byte[] bs2 = "JackRose".getBytes();
        System.out.println(joinerComma.join(bs1, bs2));
    }
}