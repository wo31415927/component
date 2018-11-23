package jdk7.base;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author zeyu 2017/12/8
 */
public class UnsafeTest {
    protected static final sun.misc.Unsafe unsafe;
    protected static final Exception exception;

    static {
        sun.misc.Unsafe u = null;
        Exception ex = null;
        try {
            Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            u = (sun.misc.Unsafe) unsafeField.get(null);
        } catch (SecurityException e) {
            ex = e;
        } catch (NoSuchFieldException e) {
            ex = e;
        } catch (IllegalArgumentException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        }
        exception = ex;
        unsafe = u;
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws InstantiationException {
        /*Column c = (Column) unsafe.allocateInstance(Column.class);
        System.out.println(c);*/

    }

}