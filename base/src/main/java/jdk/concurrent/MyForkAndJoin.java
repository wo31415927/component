package jdk.concurrent;

/**
 * @author chenxiang
 * 2018/12/7
 */
public class MyForkAndJoin {
    public static class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long> {
        private final long[] numbers;
        private final int start;
        private final int end;
        public static final long THRESHOLD = 10_000;

        public ForkJoinSumCalculator(long[] numbers) {
            this(numbers, 0, numbers.length);
        }

        private ForkJoinSumCalculator(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                return computeSequentially();
            }
            ForkJoinSumCalculator leftTask =
                    new ForkJoinSumCalculator(numbers, start, start + length / 2);
            // 开启新线程计算子任务
            leftTask.fork();
            ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
            // rightTask必须使用compute()而非join()，使用join()会浪费很多线程等待，而compute()则保证开启的每个线程都会进行computeSequentially()计算
            Long rightResult = rightTask.compute();
            Long leftResult = leftTask.join();
            return leftResult + rightResult;
        }

        private long computeSequentially() {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += numbers[i];
            }
            return sum;
        }
    }
}
