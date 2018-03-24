package guava.service;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;

import org.slf4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import concurrent.ExtraExecutors;

/**
 * 对AbstractRunningService做了一些改造
 *
 * @author zeyu 2017/11/13
 */
public abstract class AbstractRunningService implements Service {
  protected Supplier<String> supplier =
      new Supplier<String>() {
        @Override
        public String get() {
          return serviceName();
        }
      };
  private final Service delegate =
      new AbstractService() {
        @Override
        protected final void doStart() {
          //OP 使用自定义的ExtraExecutors
          Executor executor = ExtraExecutors.renamingDecorator(executor(), supplier);
          executor.execute(
              new Runnable() {
                @Override
                public void run() {
                  try {
                    startUp();
                    notifyStarted();
                    if (isRunning()) {
                      try {
                        AbstractRunningService.this.run();
                      } catch (Throwable t) {
                        try {
                          shutDown();
                        } catch (Exception ignored) {
                          //OP
                          getLogger().warn(
                              "Error while attempting to shut down the service" + " after failure.",
                              ignored);
                        }
                        throw t;
                      }
                    }
                    shutDown();
                    notifyStopped();
                  } catch (Throwable t) {
                    notifyFailed(t);
                    //OP
                    //去除了多余的抛出异常
                  }
                }
              });
        }

        @Override
        protected void doStop() {
          triggerShutdown();
        }
      };

  /**
   * 从实现类返回logger
   * @return
   */
  protected abstract Logger getLogger();
  /** Constructor for use by subclasses. */
  protected AbstractRunningService() {
  }

  /**
   * Start the service. This method is invoked on the execution thread.
   *
   * <p>By default this method does nothing.
   */
  protected void startUp() throws Exception {}

  /**
   * Run the service. This method is invoked on the execution thread. Implementations must respond
   * to stop requests. You could poll for lifecycle changes in a work loop:
   *
   * <pre>
   *   public void run() {
   *     while ({@link #isRunning()}) {
   *       // perform a unit of work
   *     }
   *   }
   * </pre>
   *
   * ...or you could respond to stop requests by implementing {@link #triggerShutdown()}, which
   * should cause {@link #run()} to return.
   */
  protected abstract void run() throws Exception;

  /**
   * Stop the service. This method is invoked on the execution thread.
   *
   * <p>By default this method does nothing.
   */
  protected void shutDown() throws Exception {}

  /**
   * Invoked to request the service to stop.
   *
   * <p>By default this method does nothing.
   */
  protected void triggerShutdown() {}

  /**
   * Returns the {@link Executor} that will be used to run this service. Subclasses may override
   * this method to use a custom {@link Executor}, which may configure its worker thread with a
   * specific name, thread group or priority. The returned executor's {@link
   * Executor#execute(Runnable) execute()} method is called when this service is started, and should
   * return promptly.
   *
   * <p>The default implementation returns a new {@link Executor} that sets the name of its threads
   * to the string returned by {@link #serviceName}
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

  /** @since 13.0 */
  @Override
  public final void addListener(Listener listener, Executor executor) {
    delegate.addListener(listener, executor);
  }

  /** @since 14.0 */
  @Override
  public final Throwable failureCause() {
    return delegate.failureCause();
  }

  /** @since 15.0 */
  @Override
  public final Service startAsync() {
    delegate.startAsync();
    return this;
  }

  /** @since 15.0 */
  @Override
  public final Service stopAsync() {
    delegate.stopAsync();
    return this;
  }

  /** @since 15.0 */
  @Override
  public final void awaitRunning() {
    delegate.awaitRunning();
  }

  /** @since 15.0 */
  @Override
  public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
    delegate.awaitRunning(timeout, unit);
  }

  /** @since 15.0 */
  @Override
  public final void awaitTerminated() {
    delegate.awaitTerminated();
  }

  /** @since 15.0 */
  @Override
  public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
    delegate.awaitTerminated(timeout, unit);
  }

  /**
   * Returns the name of this service. {@link AbstractRunningService} may include the name in
   * debugging output.
   *
   * <p>Subclasses may override this method.
   * If use anonymous class,this method'll return empty str</p>
   *
   * @since 14.0 (present in 10.0 as getServiceName)
   */
  protected String serviceName() {
    return getClass().getSimpleName();
  }
}
