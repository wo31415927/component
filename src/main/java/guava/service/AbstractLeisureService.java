package guava.service;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import utils.concurrent.ExtraExecutors;

/**
 * Base class for services that do not need a thread while "running"
 * but may need one during startup and shutdown. Subclasses can
 * implement {@link #startUp} and {@link #shutDown} methods, each
 * which run in a executor which by default uses a separate thread
 * for each method.
 *
 * @author Chris Nokleberg zeyu
 * @since 1.0
 */
@Beta
public abstract class AbstractLeisureService implements Service {
    private final Supplier<String> threadNameSupplier = new Supplier<String>() {
        @Override
        public String get() {
            return serviceName() + " " + state();
        }
    };
    private final Service delegate = new AbstractService() {
        @Override
        protected final void doStart() {
            ExtraExecutors.renamingDecorator(executor(), threadNameSupplier)
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                startUp();
                                notifyStarted();
                            } catch (Throwable t) {
                                notifyFailed(t);
                                //OP 避免抛出未捕捉的异常,使日志混乱
                                //throw Throwables.propagate(t);
                            }
                        }
                    });
        }

        @Override
        protected final void doStop() {
            ExtraExecutors.renamingDecorator(executor(), threadNameSupplier)
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                shutDown();
                                notifyStopped();
                            } catch (Throwable t) {
                                notifyFailed(t);
                                //OP
                                //throw Throwables.propagate(t);
                            }
                        }
                    });
        }
    };

    /**
     * Constructor for use by subclasses.
     */
    protected AbstractLeisureService() {
    }

    /**
     * Start the service.
     */
    protected abstract void startUp() throws Exception;

    /**
     * Stop the service.
     */
    protected abstract void shutDown() throws Exception;

    /**
     * Returns the {@link Executor} that will be used to run this service.
     * Subclasses may override this method to use a custom {@link Executor}, which
     * may configure its worker thread with a specific name, thread group or
     * priority. The returned executor's {@link Executor#execute(Runnable)
     * execute()} method is called when this service is started and stopped,
     * and should return promptly.
     * <p>必须实现executor</p>
     */
    protected abstract Executor executor();

    @Override
    public String toString() {
        return serviceName() + " [" + state() + "]";
    }

    @Override
    public final boolean isRunning() {
        return delegate.isRunning();
    }

    @Override
    public final State state() {
        return delegate.state();
    }

    /**
     * @since 13.0
     */
    @Override
    public final void addListener(Listener listener, Executor executor) {
        delegate.addListener(listener, executor);
    }

    /**
     * @since 14.0
     */
    @Override
    public final Throwable failureCause() {
        return delegate.failureCause();
    }

    /**
     * @since 15.0
     */
    @Override
    public final Service startAsync() {
        delegate.startAsync();
        return this;
    }

    /**
     * @since 15.0
     */
    @Override
    public final Service stopAsync() {
        delegate.stopAsync();
        return this;
    }

    /**
     * @since 15.0
     */
    @Override
    public final void awaitRunning() {
        delegate.awaitRunning();
    }

    /**
     * @since 15.0
     */
    @Override
    public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
        delegate.awaitRunning(timeout, unit);
    }

    /**
     * @since 15.0
     */
    @Override
    public final void awaitTerminated() {
        delegate.awaitTerminated();
    }

    /**
     * @since 15.0
     */
    @Override
    public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
        delegate.awaitTerminated(timeout, unit);
    }

    /**
     * Returns the name of this service. {@link com.google.common.util.concurrent.AbstractIdleService} may include the name in debugging
     * output.
     *
     * @since 14.0
     */
    protected String serviceName() {
        return getClass().getSimpleName();
    }
}
