package com.lun.c02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListFilter {
	public interface Predicate<T>{
		boolean test(T t);
	}
	
	public static <T> List<T> filter(List<T> list, Predicate<T> p){
		List<T> result = new ArrayList<>();
		
		for(T obj : list) {
			if(p.test(obj)) {
				result.add(obj);
			}
		}
		
		return result;
	}
	
	
	public static void main(String[] args) {
		//Integer[] array = new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		
		List<Integer> evenNumber = ListFilter.filter(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
				, (Integer i) -> i % 2 == 0);
		
		System.out.println(evenNumber);
		
	}
}
