package rxjava2;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import utils.concurrent.ExtraExecutors;

/**
 * @author cctv 2017/11/20
 */
@Slf4j
public class HelloTest {
    /**
     * HelloWorld
     * Consumer
     * Subscriber
     * disposal开关
     */
    @Test
    public void test() throws Exception {
        Flowable.just("Hello world")
                .subscribe(s -> System.out.println(s));
        //Consumer用的是LambdaSubscriber
        //synchronously invoke onNext event
        //Consumer<? super T>,表示Consumer中声明的泛型必须是FlowableEmitter声明的父类
        Flowable.fromArray("Hello").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("Hello " + s + "!");
            }
        });
        Observable.create(new ObservableOnSubscribe<Integer>() {
            /*
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
            /*
             * @param d disposable, 可以切断Observable和Observer之间的开关，切断之后，Observer将不再接收任何事件
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

            /*
             * @param e 收到Error事件后，将不再接收后面的事件，虽然Observable还是会继续发
             */
            @Override
            public void onError(Throwable e) {
                log.info("error");
            }

            /*
            同Error事件
             */
            @Override
            public void onComplete() {
                log.info("complete");
            }
        });
    }

    /**
     * 骨架
     * etl
     * 线程调度 deamon线程
     */
    @Test
    public void test1() throws Exception {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> flowableEmitter) throws Exception {
                for (int i = 0; !flowableEmitter.isCancelled() && i < 100; i++) {
                    flowableEmitter.onNext(String.valueOf(i));
                }
                flowableEmitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                //指定Observable使用的线程,只能指定一次
                .subscribeOn(Schedulers.newThread()).skip(10).take(5)
                //指定map动作使用的线程
                .observeOn(Schedulers.io()).map(s ->
                "0x" + s)
                //可以多次指定,都在main线程中执行,以最后一个指定的Scheduler为准
                .observeOn(Schedulers.single())
                .observeOn(Schedulers.from(ExtraExecutors.renamingDecorator(Executors.newSingleThreadExecutor(), () -> "Extra"))).
                //RxJava使用的都是deamon线程，所以main线程不能提前结束
                //阻塞main线程
                        subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        log.info(s);
                    }
                });
        Thread.sleep(100000);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        Flowable.range(1, 10)
                .observeOn(Schedulers.computation())
                .map(v ->
                        v * v)
                .blockingSubscribe(v ->
                        log.info(v.toString()));
        log.info("FlatMap: ");
        Flowable.range(1, 10)
                .flatMap(v ->
                        //并行做的结果是乱序
                        Flowable.just(v)
                                .subscribeOn(Schedulers.computation())
                                .map(w -> w * w)
                )
                .blockingSubscribe(w -> log.info(w.toString()));
        log.info("Parallel: ");
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v ->
                        v * v)
                //顺序,但结果还是乱序
                .sequential()
                .blockingSubscribe(v -> log.info(v.toString()));
    }

    /**
     * 背压:是一种Flow Control策略，具体是生产太快,消费太慢时,由下游观察者通知上游的被观察者发送事件
     * Hot Observables: 订阅之后开始发送事件的Observable
     * Cold Observables: 创建之后开始发送事件的Observable
     * Observable/Observer:这种模型不支持背压,当发生生产过快的场景时,event会堆积直到OOM
     */
    @Test
    public void testBackPressure1() throws Exception {
        //被观察者在主线程中，每1ms发送一个事件
        Observable.interval(1, TimeUnit.MILLISECONDS)
                //将观察者的工作放在新线程环境中
                .observeOn(Schedulers.newThread())
                //观察者处理每1000ms才处理一个事件
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        log.info(String.valueOf(aLong));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        Thread.sleep(1000000);
    }

    /**
     * Flowable/Subscriber:这种模型支持背压,需要手动发出请求才能取到数据
     * @throws Exception
     */
    @Test
    public void testBackPressure2() throws Exception {
        //如果使用create()需要制定BackpressureStrategy
        //异步线程才需要使用blockingSubscribe
        Flowable.range(0, 100).observeOn(Schedulers.newThread()).blockingSubscribe(new Subscriber<Integer>() {
            Subscription subscription;
            /**
             *
             * @param subscription 用于request or cancel
             */
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("Start!");
                this.subscription = subscription;
                //建议在request之前完成初始化工作
                subscription.request(1);
                log.info("End!");
            }

            @Override
            public void onNext(Integer aInteger) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(String.valueOf(aInteger));
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                log.info("Complete!");
            }
        });
    }

    @Test
    public void testCancel() throws Exception {
        //take触发doOnCancel
        Flowable.just(1, 2, 3)
                .doOnCancel(() -> System.out.println("Cancelled!"))
                .take(2)
                .subscribe(System.out::println);
    }

    @Test
    public void testTmp() throws Exception {
    }
}