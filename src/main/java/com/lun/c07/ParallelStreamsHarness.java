package com.lun.c07;

import java.util.concurrent.*;
import java.util.function.*;

public class ParallelStreamsHarness {

    public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();

    public static void main(String[] args) {
//    	System.out.println("Sequential Sum done in: " + measurePerf(ParallelStreams::sequentialSum, 10_000_000L) + " msecs\\n");
//        System.out.println("Iterative Sum done in: " + measurePerf(ParallelStreams::iterativeSum, 10_000_000L) + " msecs\\n");
//        System.out.println("Parallel Sum done in: " + measurePerf(ParallelStreams::parallelSum, 10_000_000L) + " msecs\\n" );
//        System.out.println("Range Sum done in: " + measurePerf(ParallelStreams::rangedSum, 10_000_000L) + " msecs\\n");
//        System.out.println("Parallel range Sum done in: " + measurePerf(ParallelStreams::parallelRangedSum, 10_000_000L) + " msecs\\n" );
        System.out.println("ForkJoin sum done in: " + measurePerf(ForkJoinSumCalculator::forkJoinSum, 10_000_000L) + " msecs\\n" );
//        System.out.println("SideEffect sum done in: " + measurePerf(ParallelStreams::sideEffectSum, 10_000_000L) + " msecs\\n" );
//        System.out.println("SideEffect prallel sum done in: " + measurePerf(ParallelStreams::sideEffectParallelSum, 10_000_000L) + " msecs\\n" );
//        System.out.println("SideEffect Sequential sum done in: " + measurePerf(ParallelStreams::sideEffectSum, 10_000_000L) + " msecs\\n" );
    }

    public static <T, R> long measurePerf(Function<T, R> f, T input) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            R result = f.apply(input);
            long duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Result: " + result);
            if (duration < fastest) fastest = duration;
        }
        return fastest;
    }
}
