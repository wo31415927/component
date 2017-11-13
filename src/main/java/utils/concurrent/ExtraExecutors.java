package utils.concurrent;

import com.google.common.base.Supplier;

import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

/** @author cctv 2017/11/13 */
@Slf4j
public class ExtraExecutors {
  public static Executor renamingDecorator(
      final Executor executor, final Supplier<String> nameSupplier) {
    checkNotNull(executor);
    checkNotNull(nameSupplier);
    return new Executor() {
      @Override
      public void execute(Runnable command) {
        executor.execute(Callables.threadRenaming(command, nameSupplier));
      }
    };
  }
}
