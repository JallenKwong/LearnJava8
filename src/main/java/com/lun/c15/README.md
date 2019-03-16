# 面向对象和函数式编程的混合：Java 8和Scala的比较 #

[1.Scala简介](#scala简介)

[1.1.HelloWorld](#helloworld)

[1.1.1.命令式Scala](#命令式scala)

[1.1.2.函数式Scala](#函数式scala)

[1.2.基础数据结构：List、Set、Map、Tuple、Stream以及Option](#基础数据结构listsetmaptuplestream以及option)

[1.2.1.创建集合](#创建集合)

[1.2.2.不可变与可变的比较](#不可变与可变的比较)

[1.2.3.使用集合](#使用集合)

[1.2.4.元组](#元组)

[1.2.5.Stream](#stream)

[1.2.6.Option](#option)

[2.函数](#函数)

[2.1.Scala中的一等函数](#scala中的一等函数)

[2.2.匿名函数和闭包](#匿名函数和闭包)

[2.3.科里化](#科里化)

[3.类和trait](#类和trait)

[3.1.更加简洁的Scala类](#更加简洁的scala类)

[3.2.Scala的trait与Java8的接口对比](#scala的trait与java8的接口对比)

[4.小结](#小结)

Scala是一种混合了面向对象和函数式编程的语言。它常常被看作Java的一种替代语言，程序员们希望在运行于JVM上的静态类型语言中使用函数式特性，同时又期望保持Java体验的一致性。和Java比较起来，Scala提供了更多的特性，包括更复杂的类型系统、类型推断、模式匹配、定义域语言的结构等。除此之外，你可以在Scala代码中直接使用任何一个Java类库。

## Scala简介 ##

### HelloWorld ###

#### 命令式Scala ####

	object Beer {
		def main(args: Array[String]){
			var n : Int = 2
			while( n <= 6 ){
				println(s"Hello ${n} bottles of beer")
				n += 1
			}
		}
	}

输出

	Hello 2 bottles of beer
	Hello 3 bottles of beer
	Hello 4 bottles of beer
	Hello 5 bottles of beer
	Hello 6 bottles of beer

#### 函数式Scala ####

Java 8以更加函数式的方式实现

	public class Foo {
		public static void main(String[] args) {
			IntStream.rangeClosed(2, 6)
			.forEach(n -> System.out.println("Hello " + n +
			" bottles of beer"));
		}
	}

Scala来实现

	object Beer {
		def main(args: Array[String]){
			2 to 6 foreach { n => println(s"Hello ${n} bottles of beer") }
		}
	}

### 基础数据结构：List、Set、Map、Tuple、Stream以及Option ###

#### 创建集合 ####

在Scala中创建集合是非常简单的

	val authorsToAge = Map("Raoul" -> 23, "Mario" -> 40, "Alan" -> 53)

Java中那样手工添加每一个元素：

	Map<String, Integer> authorsToAge = new HashMap<>();
	authorsToAge.put("Raoul", 23);
	authorsToAge.put("Mario", 40);
	authorsToAge.put("Alan", 53);

Scala轻松地创建List（一种单向链表）或者Set（不带冗余数据的集合）

	val authors = List("Raoul", "Mario", "Alan")
	val numbers = Set(1, 1, 2, 3, 5, 8)

Scala中，关键字val表明变量是只读的，并由此不能被赋值（就像Java中声明为final的变量一样）。而关键字var表明变量是可以读写的。

#### 不可变与可变的比较 ####

Scala的集合有一个重要的特质我们应该牢记在心，那就是我们之前创建的集合在默认情况下是只读的。这意味着它们从创建开始就不能修改。

更新一个Scala集合会生成一个新的集合

	val numbers = Set(2, 5, 3);
	val newNumbers = numbers + 8 //这里的操作符+会将8添加到Set中，创建并返回一个新的Set对象
	println(newNumbers)
	println(numbers)

Java中提供了多种方法创建不可修改的（unmodifiable）集合。下面的代码中，变量newNumbers是集合Set对象numbers的一个只读视图：

	Set<Integer> numbers = new HashSet<>();
	Set<Integer> newNumbers = Collections.unmodifiableSet(numbers);

这意味着你无法通过操作变量newNumbers向其中加入新的元素。不过，不可修改集合仅仅是对可变集合进行了一层封装。通过直接访问numbers变量，你还是能向其中加入元素。

与此相反，不可变（immutable）集合确保了该集合在任何时候都不会发生变化，无论有多少个变量同时指向它。

#### 使用集合 ####

	val fileLines = Source.fromFile("data.txt").getLines.toList()
	
	val linesLongUpper = fileLines.filter(l => l.length() > 10)
		.map(l => l.toUpperCase())

#### 元组 ####

Java目前还不支持元组

Scala提供了名为元组字面量

	val raoul = ("Raoul", "+ 44 887007007")
	val alan = ("Alan", "+44 883133700")

Scala支持任意大小的元组

	val book = (2014, "Java 8 in Action", "Manning")
	val numbers = (42, 1337, 0, 3, 14)

你可以依据它们的位置，通过存取器（accessor） _1、_2（从1开始的一个序列）访问元组中的元素，比如：

	println(book._1)
	println(numbers._4)

#### Stream ####

Scala也提供了对应的数据结构，它采用延迟方式计算数据结构，名称也叫Stream！不过Scala中的Stream提供了更加丰富的功能，让Java中的Stream有些黯然失色。Scala中的Stream可以记录它曾经计算出的值，所以之前的元素可以随时进行访问。

除此之外，Stream还进行了索引，所以Stream中的元素可以像List那样通过索引访问。注意，这种抉择也附带着开销，由于需要存储这些额外的属性，和Java 8中的Stream比起来，Scala版本的Stream内存的使用效率变低了，因为Scala中的Stream需要能够回溯之前的元素，这意味着之前访问过的元素都需要在内存“记录下来”（即进行缓存）。

#### Option ####

Java8的Optional

	public String getCarInsuranceName(Optional<Person> person, int minAge) {
		return person.filter(p -> p.getAge() >= minAge)
					.flatMap(Person::getCar)
					.flatMap(Car::getInsurance)
					.map(Insurance::getName)
					.orElse("Unknown");
	}

在Scala语言中，你可以使用Option使用Optional类似的方法实现该函数：

	def getCarInsuranceName(person: Option[Person], minAge: Int) = person.filter(_.getAge() >= minAge)
				.flatMap(_.getCar)
				.flatMap(_.getInsurance)
				.map(_.getName).getOrElse("Unknown")

## 函数 ##

### Scala中的一等函数 ###

	def isJavaMentioned(tweet: String) : Boolean = tweet.contains("Java")
	def isShortTweet(tweet: String) : Boolean = tweet.length() < 20

Scala语言中，你可以直接传递这两个方法给内嵌的filter，如下所示

	val tweets = List(
		"I love the new features in Java 8",
		"How's it going?",
		"An SQL query walks into a bar, sees two tables and says 'Can I join you?'"
	)
	tweets.filter(isJavaMentioned).foreach(println)
	tweets.filter(isShortTweet).foreach(println)

现在，让我们一起审视下内嵌方法filter的函数签名：

	def filter[T](p: (T) => Boolean): List[T]

### 匿名函数和闭包 ###

匿名函数

	val isLongTweet : String => Boolean
		= (tweet : String) => tweet.length() > 60

	val isLongTweet : String => Boolean
		= new Function1[String, Boolean] {
		def apply(tweet: String): Boolean = tweet.length() > 60
	}

	isLongTweet.apply("A very short tweet")

如果用Java，你可以采用下面的方式：

	Function<String, Boolean> isLongTweet = (String s) -> s.length() > 60;
	boolean long = isLongTweet.apply("A very short tweet");

	isLongTweet("A very short tweet")

---

**闭包**

**闭包是一个函数实例，它可以不受限制地访问该函数的非本地变量**。不过Java 8中的Lambda表达式自身带有一定的限制：它们不能修改定义Lambda表达式的函数中的本地变量值。这些变量必须隐式地声明为final。

Scala中的匿名函数可以取得自身的变量，但并非变量当前指向的变量值。

	def main(args: Array[String]) {
		var count = 0
		val inc = () => count+=1
		inc()
		println(count)
		inc()
		println(count)
	}

不过在Java中，下面的这段代码会遭遇编译错误，因为count隐式地被强制定义为final：

	public static void main(String[] args) {
		int count = 0;
		Runnable inc = () -> count+=1;//错误：count必须为final或者在效果上为final
		inc.run();
		System.out.println(count);
		inc.run();
	}

### 科里化 ###

Java的示例

	static int multiply(int x, int y) {
		return x * y;
	}
	int r = multiply(2, 10);

	static Function<Integer, Integer> multiplyCurry(int x) {
		return (Integer y) -> x * y;
	}

	Stream.of(1, 3, 5, 7)
		.map(multiplyCurry(2))
		.forEach(System.out::println);

Scala提供了一种特殊的语法可以自动完成这部分工作。

	def multiply(x : Int, y: Int) = x * y
	val r = multiply(2, 10);

该函数的科里化版本如下：

	def multiplyCurry(x :Int)(y : Int) = x * y
	val r = multiplyCurry(2)(10)

	val multiplyByTwo : Int => Int = multiplyCurry(2)
	val r = multiplyByTwo(10)

## 类和trait ##

### 更加简洁的Scala类 ###

由于Scala也是一门完全的面向对象语言，你可以创建类，并将其实例化生成对象。

	class Hello {
		def sayThankYou(){
			println("Thanks for reading our book")
		}
	}
	val h = new Hello()
	h.sayThankYou()

**getter方法和setter方法**

单纯只定义字段列表的Java类，你还需要声明一长串的getter方法、setter方法，以及恰当的构造器。多麻烦啊！

	public class Student {
		private String name;
		private int id;

		public Student(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

Scala语言中构造器、getter方法以及setter方法都能隐式地生成，从而大大降低你代码中的冗余：

	class Student(var name: String, var id: Int)
	val s = new Student("Raoul", 1)
	println(s.name)
	s.id = 1337
	println(s.id)

### Scala的trait与Java8的接口对比 ###

Scala还提供了另一个非常有助于抽象对象的特性，名称叫trait。它是Scala为实现Java中的接口而设计的替代品。trait中既可以定义抽象方法，也可以定义带有默认实现的方法。trait同时还支持Java中接口那样的多继承，所以你可以将它们看成与Java 8中接口类似的特性，它们都支持默认方法。trait中还可以包含像抽象类这样的字段，而Java 8的接口不支持这样的特性。

	trait Sized{
		var size : Int = 0
		def isEmpty() = size == 0
	}

	class Empty extends Sized//一个继承自trait Sized的类
	println(new Empty().isEmpty())//打印输出true

你可以创建一个Box类，动态地决定到底选择哪一个实例支持由trait Sized定义的操作

	class Box
	val b1 = new Box() with Sized //在对象实例化时构建trait
	println(b1.isEmpty()) //打印输出true
	val b2 = new Box()
	b2.isEmpty() //编译错误：因为Box类的声明并未继承Sized

## 小结 ##

- Java 8和Scala都是整合了面向对象编程和函数式编程特性的编程语言，它们都运行于JVM之上，在很多时候可以相互操作。

- Scala支持对集合的抽象，支持处理的对象包括List、Set、Map、Stream、Option，这些和Java 8非常类似。不过，除此之外Scala还支持元组。

- Scala为函数提供了更加丰富的特性，这方面比Java 8做得好，Scala支持：函数类型、可以不受限制地访问本地变量的闭包，以及内置的科里化表单。

- Scala中的类可以提供隐式的构造器、getter方法以及setter方法。

- Scala还支持trait，它是一种同时包含了字段和默认方法的接口。



