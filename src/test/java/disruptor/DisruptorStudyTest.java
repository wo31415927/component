package disruptor;

import com.google.common.util.concurrent.MoreExecutors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** @author cctv 2018/1/9 */
@Slf4j
public class DisruptorStudyTest {
  int workCnt;
  Disruptor<Event> disruptor;

  @Getter
  @Setter
  class Event {
    String name;
  }

  @Before
  public void setUp() throws Exception {
    workCnt = 2;
    disruptor =
        new Disruptor<>(
            new EventFactory<Event>() {
              @Override
              public Event newInstance() {
                return new Event();
              }
            },
            workCnt,
            MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(workCnt)),
            /*new ThreadFactory() {
                @Override
                public Thread newThread(@NotNull Runnable r) {
                    return null;
                }
            },*/
            ProducerType.SINGLE,
            new BlockingWaitStrategy());
    disruptor.handleEventsWithWorkerPool(initWorkHandler(workCnt));
    disruptor.start();
  }

  @After
  public void tearDown() throws Exception {
    disruptor.shutdown();
  }

  public WorkHandler[] initWorkHandler(int cnt) {
    WorkHandler[] handlers = new WorkHandler[cnt];
    for (int i = 0; i < cnt; i++) {
      handlers[i] =
          new WorkHandler<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
              System.out.println(Thread.currentThread().getName() + ": " + event.name);
            }
          };
    }
    return handlers;
  }

  public void publish(String name) {
    RingBuffer<Event> ringBuffer = disruptor.getRingBuffer();
    long sequence = ringBuffer.next();
    try {
      Event change = ringBuffer.get(sequence);
      change.setName(name);
    } finally {
      ringBuffer.publish(sequence);
    }
  }

  @Test
  public void testSingle() throws Exception {
    publish("1");
    publish("2");
    publish("3");
  }
}
