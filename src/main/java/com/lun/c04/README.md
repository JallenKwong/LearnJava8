# 引入流 #

[1.流是什么](#流是什么)

[2.流简介](#流简介)

[3.流与集合](#流与集合)

[3.1.只能遍历一次](#只能遍历一次)

[3.2.外部迭代与内部迭代](#外部迭代与内部迭代)

[4.流操作](#流操作)

[4.1.中间操作](#中间操作)

[4.2.终端操作](#终端操作)

[4.3.使用流](#使用流)

[5.小结](#小结)

几乎每个Java应用程序都会制造和处理集合。

尽管集合对于几乎任何一个Java应用都是不可或缺的，但集合操作却远远算不上完美。

- 很多业务逻辑都涉及类似于数据库的操作，比如对几道菜按照类别进行分组 （比如全素菜肴），或查找出最贵的菜。你自己用迭代器重新实现过这些操作多少遍？大部分数据库都允许你声明式地指定这些操作。比如，以下SQL查询语句就可以选出热量较低的菜肴名称：SELECT name FROM dishes WHERE calorie < 400。你看，你不需要实现如何根据菜肴的属性进行筛选（比如利用迭代器和累加器），你只需要表达你想要什么。这个基本的思路意味着，你用不着担心怎么去显式地实现这些查询语句——都替你办好了！怎么到了集合这里就不能这样了呢？

- 要是要处理大量元素又该怎么办呢？为了提高性能，你需要并行处理，并利用多核架构。但写并行代码比用迭代器还要复杂，而且调试起来也够受的！

## 流是什么 ##

流是Java API的新成员，**它允许你以声明性方式处理数据集合**（通过查询语句来表达，而不是临时编写一个实现）。就现在来说，你可以把它们看成遍历数据集的高级迭代器。

此外，流还可以透明地并行处理，你无需写任何多线程代码了！

---

[StreamBasic](StreamBasic.java)

Java8之前：

	List<Dish> lowCaloricDishes = new ArrayList<>();
	for(Dish d: menu){
		if(d.getCalories() < 400){
			lowCaloricDishes.add(d);
		}
	}

	Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
		public int compare(Dish d1, Dish d2){
			return Integer.compare(d1.getCalories(), d2.getCalories());
		}
	});

	List<String> lowCaloricDishesName = new ArrayList<>();
	
	for(Dish d: lowCaloricDishes){
		lowCaloricDishesName.add(d.getName());
	}


Java8之后：

	import static java.util.Comparator.comparing;
	import static java.util.stream.Collectors.toList;
		List<String> lowCaloricDishesName = menu.stream()
					.filter(d -> d.getCalories() < 400)
					.sorted(comparing(Dish::getCalories))
					.map(Dish::getName)
					.collect(toList());

为了利用多核架构并行执行这段代码，你只需要把stream()换成parallelStream()：

	List<String> lowCaloricDishesName = menu.parallelStream()
					.filter(d -> d.getCalories() < 400)
					.sorted(comparing(Dishes::getCalories))
					.map(Dish::getName)
					.collect(toList());

---

新的方法有几个显而易见的好处。

- **代码是以声明性方式写的**：说明想要完成什么（筛选热量低的菜肴）而不是说明如何实现一个操作（利用循环和if条件等控制流语句）。这种方法加上**行为参数化**让你可以轻松应对变化的需求：你很容易再创建一个代码版本，利用Lambda表达式来筛选高卡路里的菜肴，而用不着去复制粘贴代码。

- 你可以把几个基础操作链接起来，来表达复杂的数据处理流水线（在filter后面接上sorted、map和collect操作，如下图），同时保持代码清晰可读。filter的结果被传给了sorted方法，再传给map方法，最后传给collect方法。

因为filter、sorted、map和collect等操作是与具体线程模型无关的高层次构件，所以它们的内部实现可以是单线程的，也可能透明地充分利用你的多核架构！在实践中，这意味着你用不着为了让某些数据处理任务并行而去操心线程和锁了，Stream API都替你做好了！

![](image/stream.png)

---

	Map<Dish.Type, List<Dish>> dishesByType =
		menu.stream().collect(groupingBy(Dish::getType));

按照Map里面的类别对菜肴进行分组

	{FISH=[prawns, salmon],
	OTHER=[french fries, rice, season fruit, pizza],
	MEAT=[pork, beef, chicken]}

---

>其他库：Guava、Apache和lambdaj
>
>为了给Java程序员提供更好的库操作集合，前人已经做过了很多尝试。

总结一下，Java 8中的Stream API可以让你写出这样的代码：
- 声明性——更简洁，更易读
- 可复合——更灵活
- 可并行——性能更好

[数据准备Dish](Dish.java)


## 流简介 ##

**流定义——从支持数据处理操作的源生成的元素序列**

- **元素序列**——就像集合一样，流也提供了一个接口，可以访问特定元素类型的一组有序值。因为集合是数据结构，所以它的主要目的是以特定的时间/空间复杂度存储和访问元素（如ArrayList 与 LinkedList）。但流的目的在于表达计算，比如你前面见到的filter、 sorted和map。集合讲的是数据，流讲的是计算。

- **源**——流会使用一个提供数据的源，如集合、数组或输入/输出资源。 请注意，从有序集合生成流时会保留原有的顺序。由列表生成的流，其元素顺序与列表一致。

- **数据处理操作**——流的数据处理功能支持类似于数据库的操作，以及函数式编程语言中的常用操作，如filter、 map、 reduce、 find、 match、 sort等。流操作可以顺序执行，也可并行执行。


**此外，流操作有两个重要的特点。**

- 流水线——很多流操作本身会返回一个流，这样多个操作就可以链接起来，形成一个大的流水线。这让我们下一章中的一些优化成为可能，如延迟和短路。流水线的操作可以看作对数据源进行数据库式查询。
- 内部迭代——与使用迭代器显式迭代的集合不同，流的迭代操作是在背后进行的。

---

	import static java.util.stream.Collectors.toList;

	List<String> threeHighCaloricDishNames = menu.stream()
					.filter(d -> d.getCalories() > 300)
					.map(Dish::getName)
					.limit(3)
					.collect(toList());

	System.out.println(threeHighCaloricDishNames);

流程图

![](image/flow.png)

## 流与集合 ##

![](image/streamAndCollection.png)

### 只能遍历一次 ###

请注意，和迭代器类似，流只能遍历一次。遍历完之后，我们就说这个流已经被消费掉了。你可以从原始数据源那里再获得一个新的流来重新遍历一遍

	List<String> title = Arrays.asList("Java8", "In", "Action");
	Stream<String> s = title.stream();
	s.forEach(System.out::println);
	s.forEach(System.out::println);//java.lang.IllegalStateException:流已被操作或关闭

### 外部迭代与内部迭代 ###

外部迭代

	List<String> names = new ArrayList<>();
	
	for(Dish d: menu){
		names.add(d.getName());
	}

	//使用迭代器模式
	List<String> names = new ArrayList<>();
	Iterator<String> iterator = menu.iterator();
	
	while(iterator.hasNext()) {
		Dish d = iterator.next();
		names.add(d.getName());
	}

内部迭代

	List<String> names = menu.stream().map(Dish::getName).collect(toList());


内部迭代时，项目可以透明地并行处理，或者用更优化的顺序进行处理。

要是用Java过去的那种外部迭代方法，这些优化都是很困难的。这似乎有点儿鸡蛋里挑骨头，但这差不多就是Java 8引入流的理由了——Streams库的内部迭代可以自动选择一种适合你硬件的数据表示和并行实现。

![](image/iterator.png)


外部迭代一个集合，显式地取出每个项目再加以处理。

内部迭代时，项目可以透明地并行处理，或者用更优化的顺
序进行处理。

流利用了内部迭代：替你把迭代做了。但是，只有你已经预先定义好了能够隐藏迭代的操作列表，例如filter或map，这个才有用。

## 流操作 ##

	List<String> names = menu.stream()
		.filter(d -> d.getCalories() > 300)
		.map(Dish::getName)
		.limit(3)
		.collect(toList())

- **中间操作** filter、 map和limit可以连成一条流水线；
- **终端操作** collect触发流水线执行并关闭它。

![](image/stream2.png)

### 中间操作 ###

为了搞清楚流水线中到底发生了什么

	List<String> names =
	menu.stream()
		.filter(d -> {
			System.out.println("filtering" + d.getName());
			return d.getCalories() > 300;
		})
		.map(d -> {
			System.out.println("mapping" + d.getName());
			return d.getName();
		})
		.limit(3)
		.collect(toList());
	System.out.println(names);

此代码执行时将打印：

	filtering pork
	mapping pork
	filtering beef
	mapping beef
	filtering chicken
	mapping chicken
	[pork, beef, chicken]

会发现，有好几种优化利用了流的延迟性质。

1. 尽管很多菜的热量都高于300卡路里，但只选出了前三个！这是因为limit操作和一种称为**短路**的技巧。

2. 尽管filter和map是两个独立的操作，但它们**合并到同一次遍历中**了。

### 终端操作 ###

终端操作会从流的流水线生成结果。其结果是任何不是流的值，比如List、Integer，甚至void。

	menu.stream().forEach(System.out::println);

### 使用流 ###

总而言之，流的使用一般包括三件事：

- 一个数据源（如集合）来执行一个查询；
- 一个中间操作链，形成一条流的流水线；
- 一个终端操作，执行流水线，并能生成结果。

流的流水线背后的理念类似于构建器模式Builder。

** 中间操作 **

<table>

<tr>
<td>操作</td>
<td>类型</td>
<td>返回类型</td>
<td>操作参数</td>
<td>函数描述符</td>
</tr>

<tr>
<td>map</td>
<td rowspan='5'>中间</td>
<td>Stream&lt;R&gt;</td>
<td>Function&lt;T, R&gt;</td>
<td>T -> R</td>
</tr>

<tr>
<td>filter</td>
<td rowspan='4'>Stream&lt;T&gt;</td>
<td>Predicate&lt;T&gt;</td>
<td>T -> boolean</td>
</tr>

<tr>
<td>limit</td>
<td></td>
<td></td>
</tr>

<tr>
<td>sorted</td>
<td>Comparator&lt;T&gt;</td>
<td>(T, T) -> int</td>
</tr>

<tr>
<td>distinct</td>
<td></td>
<td></td>
</tr>

</table>

**终端操作**

<table>

<tr>
<td>操作</td>
<td>类型</td>
<td>目的</td>
</tr>

<tr>
<td>forEach</td>
<td rowspan='3'>终端</td>
<td>消费流中的每个元素并对其应用 Lambda。这一操作返回 void</td>
</tr>

<tr>
<td>count</td>
<td>返回流中元素的个数。这一操作返回 long</td>
</tr>

<tr>
<td>collect</td>
<td>把流归约成一个集合，比如 List、 Map 甚至是 Integer。</td>
</tr>

</table>

## 小结 ##

- 流是“从支持数据处理操作的源生成的一系列元素”。
- 流利用内部迭代：迭代通过filter、map、sorted等操作被抽象掉了。
- 流操作有两类：中间操作和终端操作。
- filter和map等中间操作会返回一个流，并可以链接在一起。可以用它们来设置一条流
水线，但并不会生成任何结果。
- forEach和count等终端操作会返回一个非流的值，并处理流水线以返回结果。
- 流中的元素是按需计算的。
