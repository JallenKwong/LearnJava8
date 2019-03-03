package com.lun.c06;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToListCollectorTest {

	public static void main(String[] args) {
		List<Dish> dishes = Dish.menu.stream().collect(new ToListCollector<Dish>());
		
		List<Dish> dishes2 = Dish.menu.stream().collect(Collectors.toList());
		
		List<Dish> dishes3 = Dish.menu.stream().collect(ArrayList::new, List::add, List::addAll);
	}
	
}
