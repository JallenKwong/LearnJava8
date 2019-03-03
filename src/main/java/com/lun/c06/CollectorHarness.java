package com.lun.c06;

import java.util.function.*;

public class CollectorHarness {

    public static void main(String[] args) {
        System.out.println("1.Partitioning done in: " + execute(PartitionPrimeNumbers::partitionPrimes) + " msecs");
        System.out.println("2.Partitioning done in: " + execute(PartitionPrimeNumbers::partitionPrimesWithCustomCollector) + " msecs" );
    }

    private static long execute(Consumer<Integer> primePartitioner) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            primePartitioner.accept(1_000_000);
            long duration = (System.nanoTime() - start) / 1_000_000;
            if (duration < fastest) fastest = duration;
            System.out.println("done in " + duration);
        }
        return fastest;
    }
}

/*
done in 1430
done in 1382
done in 1306
done in 1061
done in 1132
done in 1049
done in 1027
done in 1033
done in 1274
done in 1901
1.Partitioning done in: 1027 msecs
done in 1020
done in 860
done in 983
done in 977
done in 967
done in 957
done in 877
done in 900
done in 801
done in 850
2.Partitioning done in: 801 msecs
*/
