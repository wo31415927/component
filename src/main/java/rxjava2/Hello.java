package rxjava2;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zeyu 2017/11/20
 */
public class Hello {
    public static void main(String[] args) throws InterruptedException {
        Flowable.just("Hello world")
                .subscribe(s -> System.out.println(s));
        Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Done";
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(System.out::println, Throwable::printStackTrace);
        Thread.sleep(2000); // <---

        Flowable.range(1, 10)
                .observeOn(Schedulers.computation())
                .map(v -> v * v)
                .blockingSubscribe(System.out::println);
    }
}
