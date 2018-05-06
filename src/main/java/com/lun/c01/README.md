# Java 8的新特性 #

### 1.Stream API ###

	// 顺序处理
	List<Apple> heavyApples3 = inventory.stream().filter((Apple a) -> a.getWeight() > 150)
			.collect(Collectors.toList());
	System.out.println(heavyApples3);

	// 并行处理
	List<Apple> heavyApples4 = inventory.parallelStream().filter((Apple a) -> a.getWeight() > 150)
			.collect(Collectors.toList());
	System.out.println(heavyApples4);


### 2.向方法传递代码的技巧 ###

类中定义下面的方法

	public static boolean isGreenApple(Apple apple) {
		return "green".equals(apple.getColor());
	}

	public static boolean isHeavyApple(Apple apple) {
		return apple.getWeight() > 150;
	}

	public static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (p.test(apple)) {
				result.add(apple);
			}
		}
		return result;
	}

运用

**使用方法型参数**

	// [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
	List<Apple> greenApples = filterApples(inventory, FilteringApples::isGreenApple);
	System.out.println(greenApples);

	// [Apple{color='green', weight=155}]
	List<Apple> heavyApples = filterApples(inventory, FilteringApples::isHeavyApple);
	System.out.println(heavyApples);

**使用Lambda表达式**

	// [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
	List<Apple> greenApples2 = filterApples(inventory, (Apple a) -> "green".equals(a.getColor()));
	System.out.println(greenApples2);

	// [Apple{color='green', weight=155}]
	List<Apple> heavyApples2 = filterApples(inventory, (Apple a) -> a.getWeight() > 150);
	System.out.println(heavyApples2);

	// []
	List<Apple> weirdApples = filterApples(inventory,
			(Apple a) -> a.getWeight() < 80 || "brown".equals(a.getColor()));
	System.out.println(weirdApples);


### 3.接口中的默认方法 ###

譬如java.util.List中的

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }
