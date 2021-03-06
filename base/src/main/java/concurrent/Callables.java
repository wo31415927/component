package concurrent;

import com.google.common.base.Supplier;

import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 取自guava,修改threadRenaming为public
 * @author cctv 2017/11/13
 */
public class Callables {
    /**
     * Wraps the given callable such that for the duration of {@link Callable#call} the thread that is
     * running will have the given name.
     *
     *
     * @param callable The callable to wrap
     * @param nameSupplier The supplier of thread names, {@link Supplier#get get} will be called once
     *     for each invocation of the wrapped callable.
     */
    public static <T> Callable<T> threadRenaming(final Callable<T> callable,
                                          final Supplier<String> nameSupplier) {
        checkNotNull(nameSupplier);
        checkNotNull(callable);
        return new Callable<T>() {
            @Override public T call() throws Exception {
                Thread currentThread = Thread.currentThread();
                String oldName = currentThread.getName();
                boolean restoreName = trySetName(nameSupplier.get(), currentThread);
                try {
                    return callable.call();
                } finally {
                    if (restoreName) {
                        trySetName(oldName, currentThread);
                    }
                }
            }
        };
    }

    /**
     * Wraps the given runnable such that for the duration of {@link Runnable#run} the thread that is
     * running with have the given name.
     *
     *
     * @param task The Runnable to wrap
     * @param nameSupplier The supplier of thread names, {@link Supplier#get get} will be called once
     *     for each invocation of the wrapped callable.
     */
    public static Runnable threadRenaming(final Runnable task, final Supplier<String> nameSupplier) {
        checkNotNull(nameSupplier);
        checkNotNull(task);
        return new Runnable() {
            @Override public void run() {
                Thread currentThread = Thread.currentThread();
                String oldName = currentThread.getName();
                boolean restoreName = trySetName(nameSupplier.get(), currentThread);
                try {
                    task.run();
                } finally {
                    if (restoreName) {
                        trySetName(oldName, currentThread);
                    }
                }
            }
        };
    }

    /** Tries to set name of the given {@link Thread}, returns true if successful. */
    private static boolean trySetName(final String threadName, Thread currentThread) {
        // In AppEngine this will always fail, should we testscope for that explicitly using
        // MoreExecutors.isAppEngine.  More generally, is there a way to see if we have the modifyThread
        // permission without catching an exception?
        try {
            currentThread.setName(threadName);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
}
