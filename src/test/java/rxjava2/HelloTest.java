package rxjava2;

import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cctv 2017/11/20
 */
@Slf4j
public class HelloTest {
  @Test
  public void test() throws Exception {
      Flowable.just("Hello world")
              .subscribe(s -> System.out.println(s));
  }

    @Test
  public void test1() throws Exception {
      Observable.create(new ObservableOnSubscribe<Integer>() {
          /**
           * @param emitter 发射器，可以发射Next/Complete/Error三种事件
           * @throws Exception
           */
          @Override
          public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
              emitter.onNext(1);
              emitter.onNext(2);
              emitter.onNext(3);
              emitter.onComplete();
          }
      }).subscribe(new Observer<Integer>() {
          /**
           * @param d disposable,可以切断Observable和Observer之间的开关，切断之后，Observer将不再接收任何事件
           * Observable要想不再发送事件，可以判断disposable.isDisposable()
           */
          @Override
          public void onSubscribe(Disposable d) {
              log.info("subscribe");
          }

          @Override
          public void onNext(Integer value) {
              log.info("" + value);
          }

          /**
           * 收到Error事件后，将不再接收后面的事件，虽然Observable还是会继续发
           * @param e
           */
          @Override
          public void onError(Throwable e) {
              log.info("error");
          }

          /**
           * 同Error事件
           */
          @Override
          public void onComplete() {
              log.info("complete");
          }
      });
  }

  @Test
  public void test2() throws Exception {
    Flowable.fromCallable(
            () -> {
              Thread.sleep(1000); //  imitate expensive computation
              return "Done";
            })
        //Observable线程
        .subscribeOn(Schedulers.io())
        //Observer线程
        .observeOn(Schedulers.single())
        //Consumer
        .subscribe(
            new Consumer<String>() {
              @Override
              public void accept(String s) throws Exception {
                System.out.println(s);
              }
            },
            Throwable::printStackTrace);
      //RxJava使用的都是deamon线程，所以main线程不能提前结束
      Thread.sleep(2000);
  }

  @Test
  public void test3() throws Exception {
      Flowable.range(1, 10)
              .observeOn(Schedulers.computation())
              .map(v -> v * v)
              .blockingSubscribe(System.out::println);

      Flowable.range(1, 10)
              .flatMap(v ->
                      Flowable.just(v)
                              .subscribeOn(Schedulers.computation())
                              .map(w -> w * w)
              )
              .blockingSubscribe(System.out::println);

      Flowable.range(1, 10)
              .parallel()
              .runOn(Schedulers.computation())
              .map(v -> v * v)
              .sequential()
              .blockingSubscribe(System.out::println);
  }
}