package com.lun.c05;

import com.lun.c04.Dish;
import java.util.*;

public class Finding {

	public static void main(String... args) {
		if (isVegetarianFriendlyMenu()) {
			System.out.println("Vegetarian friendly");
		}

		System.out.println(isHealthyMenu());
		System.out.println(isHealthyMenu2());

		Optional<Dish> dish = findVegetarianDish();
		dish.ifPresent(d -> System.out.println(d.getName()));
	}

	private static boolean isVegetarianFriendlyMenu() {
		return Dish.menu.stream().anyMatch(Dish::isVegetarian);
	}

	private static boolean isHealthyMenu() {
		return Dish.menu.stream().allMatch(d -> d.getCalories() < 1000);
	}

	private static boolean isHealthyMenu2() {
		return Dish.menu.stream().noneMatch(d -> d.getCalories() >= 1000);
	}

	private static Optional<Dish> findVegetarianDish() {
		return Dish.menu.stream().filter(Dish::isVegetarian).findAny();
	}

}
