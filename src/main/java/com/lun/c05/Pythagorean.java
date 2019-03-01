package com.lun.c05;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Pythagorean {
	
	public static void main(String[] args) {
		Stream<int[]> pythagoreanTriples =
				IntStream.rangeClosed(1, 100)
					.boxed()//flatMap用到泛型，所以不能使用基本类型
					.flatMap(a ->
						IntStream.rangeClosed(a, 100)
							.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
							.mapToObj(b ->new int[]{a, b, (int)Math.sqrt(a * a + b * b)})
					);
		
		
		pythagoreanTriples.limit(5)
			.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
		
		System.out.println("---");
		
		Stream<double[]> pythagoreanTriples2 =
				IntStream.rangeClosed(1, 100).boxed()
					.flatMap(a ->
						IntStream.rangeClosed(a, 100)
							.mapToObj(b -> new double[]{a, b, Math.sqrt(a*a + b*b)})
							.filter(t -> t[2] % 1 == 0));
		
		pythagoreanTriples2.limit(5)
			.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
	}
}


/*
3, 4, 5
5, 12, 13
6, 8, 10
7, 24, 25
8, 15, 17
---
3.0, 4.0, 5.0
5.0, 12.0, 13.0
6.0, 8.0, 10.0
7.0, 24.0, 25.0
8.0, 15.0, 17.0

*/