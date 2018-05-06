# 通过行为（方法）参数传递代码 #

通过筛选苹果阐述通过行为（方法）参数传递代码

### 1、初试牛刀 ###

	public static List<Apple> filterGreenApples(List<Apple> inventory){
		List<Apple> result = new ArrayList<>();
		for(Apple apple: inventory){
			if("green".equals(apple.getColor())){
				result.add(apple);
			}
		}
		return result;
	}

### 2、把颜色作为参数 ###

	public static List<Apple> filterApplesByColor(List<Apple> inventory, String color){
		List<Apple> result = new ArrayList<>();
		for(Apple apple: inventory){
			if(apple.getColor().equals(color)){
				result.add(apple);
			}
		}
		return result;
	}

运用

	// [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
	List<Apple> greenApples = filterApplesByColor(inventory, "green");
	System.out.println(greenApples);

	// [Apple{color='red', weight=120}]
	List<Apple> redApples = filterApplesByColor(inventory, "red");
	System.out.println(redApples);

现在又想对重量过滤

	public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight){
		List<Apple> result = new ArrayList<>();
		for(Apple apple: inventory){
			if(apple.getWeight() > weight){
				result.add(apple);
			}
		}
		return result;
	}

发现有重复代码，打破Don't Repeat Yourself原则

### 3、对你想到的每个属性做筛选 ###

	public static List<Apple> filterApples(List<Apple> inventory, int weight, String color){
	...
	}

这样做并不聪明


### 4、行为参数化 ###

这里有 **策略模式** 的影子

策略模式：定义一系列的算法,把它们一个个封装起来, 并且使它们可相互替换。

	interface ApplePredicate{
		public boolean test(Apple a);
	}

	static class AppleWeightPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return apple.getWeight() > 150; 
		}
	}
	static class AppleColorPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return "green".equals(apple.getColor());
		}
	}

	static class AppleRedAndHeavyPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return "red".equals(apple.getColor()) 
					&& apple.getWeight() > 150; 
		}
	}

	public static List<Apple> filter(List<Apple> inventory, ApplePredicate p){
		List<Apple> result = new ArrayList<>();
		for(Apple apple : inventory){
			if(p.test(apple)){
				result.add(apple);
			}
		}
		return result;
	}

运用

	// [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
	List<Apple> greenApples2 = filter(inventory, new AppleColorPredicate());
	System.out.println(greenApples2);

	// [Apple{color='green', weight=155}]
	List<Apple> heavyApples = filter(inventory, new AppleWeightPredicate());
	System.out.println(heavyApples);

	// []
	List<Apple> redAndHeavyApples = filter(inventory, new AppleRedAndHeavyPredicate());
	System.out.println(redAndHeavyApples);

### 5、使用匿名类 ###

	// [Apple{color='red', weight=120}]
	List<Apple> redApples2 = filter(inventory, new ApplePredicate() {
		public boolean test(Apple a){
			return a.getColor().equals("red"); 
		}
	});
	System.out.println(redApples2);

### 6、使用Lambda表达式 ###

4、5的代码太麻烦了

使用Lambda表达式会显得简单优雅

	List<Apple> greenApples2 = filterApples(inventory, (Apple a) -> "green".equals(a.getColor()));
	System.out.println(greenApples2);

### 7、将List类型抽象化 ###

让filter不单用于Apple

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


### 更多行为参数化的例子 ###

#### 用Comparator来排序 ####

	inventory.sort(new Comparator<Apple>() {
		@Override
		public int compare(Apple o1, Apple o2) {
			return o1.getWeight().compareTo(o2.getWeight());
		}
	});

	inventory.sort((Apple a1, Apple a2) 
			-> a1.getWeight().compareTo(a2.getWeight()));

#### 用Runnable执行代码块 ####

	Thread t = new Thread(new Runnable() {	
		@Override
		public void run() {
			System.out.println("Hello, World!");
		}
	});
	
	t = new Thread(()->System.out.println("Hello, World!")) ;

看，使用Lambda表达式减少代码量。