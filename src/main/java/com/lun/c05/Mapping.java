package com.lun.c05;


import java.util.*;
import java.util.stream.Stream;

import com.lun.c04.Dish;

import static java.util.stream.Collectors.toList;

public class Mapping {

	public static void main(String... args) {

		// map
		List<String> dishNames = Dish.menu.stream()
				.map(Dish::getName)
				.collect(toList());
		System.out.println(dishNames);

		// map
		List<String> words = Arrays.asList("Hello", "World");
		List<Integer> wordLengths = words.stream()
				.map(String::length)
				.collect(toList());
		System.out.println(wordLengths);

		System.out.println("---");
		
		List<String[]> list = words.stream()
			.map(word -> word.split(""))
			.distinct()
			.collect(toList());
		
		System.out.println(list);//[[Ljava.lang.String;@6d03e736, [Ljava.lang.String;@568db2f2]
		
		System.out.println("---");
		
		List<Stream<String>> list2 = words.stream()
			.map(word -> word.split(""))
			.map(Arrays::stream)
			.distinct()
			.collect(toList());
		
		System.out.println("---");
		
		// flatMap
		words.stream()
			.flatMap((String line) -> Arrays.stream(line.split("")))
			.distinct()
			.forEach(System.out::println);

		// flatMap
		List<Integer> numbers1 = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> numbers2 = Arrays.asList(6, 7, 8);
		List<int[]> pairs = numbers1.stream()
				.flatMap((Integer i) -> numbers2.stream().map((Integer j) -> new int[] { i, j }))
				.filter(pair -> (pair[0] + pair[1]) % 3 == 0)
				.collect(toList());
		pairs.forEach(pair -> System.out.println("(" + pair[0] + ", " + pair[1] + ")"));
	}
}
