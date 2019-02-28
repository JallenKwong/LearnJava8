package com.lun.c03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class FunctionalInterfaceExample {
	
	public static <T> List<T> filter(List<T> list, Predicate<T> p){
		List<T> result = new ArrayList<>();
		
		for(T obj : list) {
			if(p.test(obj)) {
				result.add(obj);
			}
		}
		
		return result;
	}
	
	public static <T> void forEach(List<T> list, Consumer<T> c) {
		for(T i : list) c.accept(i); 
	}
	
	
	public static <T,R> List<R> map(List<T> list, Function<T,R> f){
		List<R> result = new ArrayList<>();
		
		for(T s : list) {
			result.add(f.apply(s));
		}
		
		return result;
	}
	
	
	public static void main(String[] args) {
		Predicate<String> nonEmptyString = (String s) -> !s.isEmpty();
		List<String> list = filter(Arrays.asList("","1234","asd"), nonEmptyString);
		System.out.println(list);
		
		forEach(Arrays.asList(1,2,3,4,5,6,7), (Integer i)->System.out.println(i));
		
		List<Integer> list2 = map(Arrays.asList("","1234","asd"),(String s)->s.length());
		System.out.println(list2);
		
		List<String> str = Arrays.asList("a","b","A","B");
		str.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
		str.sort(String::compareToIgnoreCase);
		
		Supplier<String> c1 = String::new;
		String a1 = c1.get();
		
		Comparator<String> c = Comparator.comparing((String s) -> s.length());
	}
	
}
