# 默认方法 #

[1.不断演进的API](#不断演进的api)

[1.1.初始版本的API](#初始版本的api)

[1.2.用户实现](#用户实现)

[1.3.第二版API](#第二版api)

[1.4.用户面临的窘境](#用户面临的窘境)

[1.5.不同类型的兼容性：二进制、源代码和函数行为](#不同类型的兼容性二进制源代码和函数行为)

[2.概述默认方法](#概述默认方法)

[2.1.Java 8中的抽象类和抽象接口](#java8中的抽象类和抽象接口)

[2.2.使用默认实现的例子——removeIf](#使用默认实现的例子removeif)

[3.默认方法的使用模式](#默认方法的使用模式)

[3.1.可选方法](#可选方法)

[3.2.行为的多继承](#行为的多继承)

[3.2.1.类型的多继承](#类型的多继承)

[3.2.2.利用正交方法的精简接口](#利用正交方法的精简接口)

[3.2.3.组合接口](#组合接口)

[3.2.4.继承不应该成为你一谈到代码复用就试图倚靠的万金油](#继承不应该成为你一谈到代码复用就试图倚靠的万金油)

[4.解决冲突的规则](#解决冲突的规则)

[4.1.解决问题的三条规则](#解决问题的三条规则)

[4.2.选择提供了最具体实现的默认方法的接口](#选择提供了最具体实现的默认方法的接口)

[4.3.冲突及如何显式地消除歧义](#冲突及如何显式地消除歧义)

[4.4.菱形继承问题](#菱形继承问题)

[5.小结](#小结)

**实现接口的类必须为接口中定义的每个方法提供一个实现，或者从父类中继承它的实现**。但是，一旦类库的设计者需要更新接口，向其中加入新的方法，这种方式就会出现问题。现实情况是，现存的实体类往往不在接口设计者的控制范围之内，这些实体类为了适配新的接口约定也需要进行修改。由于Java 8的API在现存的接口上引入了非常多的新方法，这种变化带来的问题也愈加严重，一个例子就是像Guava和Apache Commons这样的框架现在都需要修改实现了List接口的所有类，为其添加sort方法的实现。

Java 8为了解决这一问题引入了一种新的机制。Java 8中的接口现在支持在声明方法的同时提供实现！通过两种方式可以完成这种操作。

1. Java 8允许在接口内声明静态方法。
2. Java 8引入了一个新功能，叫默认方法，通过默认方法你可以指定接口方法的默认实现。

换句话说，**接口能提供方法的具体实现**。因此，实现接口的类如果不显式地提供该方法的具体实现，就会自动继承默认的实现。这种机制可以使你平滑地进行接口的优化和演进。实际上，到目前为止你已经使用了多个默认方法。

---

两个例子

List接口中的sort方法是Java 8中全新的方法，它的定义如下：

	default void sort(Comparator<? super E> c){
		Collections.sort(this, c);
	}

Collection中的stream方法的定义如下

	default Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

---

默认方法的引入就是为了以兼容的方式解决像Java API这样的类库的演进问题的

![](image/interface.png)

## 不断演进的API ##

为了理解为什么一旦API发布之后，它的演进就变得非常困难，

**假设你是一个流行Java绘图库的设计者**。

你的库中包含了一个Resizable接口，它定义了一个简单的可缩放形状必须支持的很多方法， 比如：setHeight、setWidth、getHeight、getWidth以及setAbsoluteSize。此外，你还提供了几个额外的实现（out-of-box implementation），如正方形、长方形。由于你的库非常流行，你的一些用户使用Resizable接口创建了他们自己感兴趣的实现，比如椭圆。

发布API几个月之后，你突然意识到Resizable接口**遗漏了一些功能**。比如，如果接口提供一个setRelativeSize方法，可以接受参数实现对形状的大小进行调整，那么接口的易用性会更好。

你会说这看起来很容易啊：为Resizable接口添加setRelativeSize方法，再更新Square和Rectangle的实现就好了。不过，事情并非如此简单！你要考虑已经使用了你接口的用户，他们已经按照自身的需求实现了Resizable接口，他们该**如何应对这样的变更呢**？

非常不幸，你无法访问，也无法改动他们实现了Resizable接口的类。这也是Java库的设计者需要改进Java API时面对的问题。

### 初始版本的API ###

	public interface Resizable extends Drawable{
		int getWidth();
		int getHeight();
		void setWidth(int width);
		void setHeight(int height);
		void setAbsoluteSize(int width, int height);
	}

### 用户实现 ###

用户根据自身的需求实现了Resizable接口，创建了Ellipse类：

	public class Ellipse implements Resizable {
	…
	}

他实现了一个处理各种Resizable形状（包括Ellipse）的游戏：

	public class Game{
		public static void main(String...args){
			List<Resizable> resizableShapes =
			Arrays.asList(new Square(), new Rectangle(), new Ellipse());
			Utils.paint(resizableShapes);
		}
	}
	public class Utils{
		public static void paint(List<Resizable> l){
			l.forEach(r -> {
				r.setAbsoluteSize(42, 42);
				r.draw();
			});
		}
	}

### 第二版API ###

库上线使用几个月之后，你收到很多请求，要求你更新Resizable的实现，让Square、Rectangle以及其他的形状都能支持setRelativeSize方法。为了满足这些新的需求，你发布了第二版API

	public interface Resizable {
		//...
		void setRelativeSize(int wFactor, int hFactor);
	}

![](image/resizable.png)


### 用户面临的窘境 ###

对Resizable接口的更新导致了一系列的问题。首先，接口现在要求它所有的实现类添加setRelativeSize方法的实现。但是用户最初实现的Ellipse类并未包含setRelativeSize方法。**向接口添加新方法是二进制兼容的，这意味着如果不重新编译该类，即使不实现新的方法，现有类的实现依旧可以运行**。不过，用户可能修改他的游戏，在他的Utils.paint方法中调用setRelativeSize方法，因为paint方法接受一个Resizable对象列表作为参数。如果传递的是一个Ellipse对象，程序就会抛出一个运行时错误，因为它并未实现setRelativeSize方法：

	Exception in thread "main" java.lang.AbstractMethodError:
		com.lun.c09.Ellipse.setRelativeSize(II)V

其次，如果用户试图重新编译整个应用（包括Ellipse类），他会遭遇下面的编译错误：

	com/lun/c09/Ellipse.java:6: error: Ellipse is not abstract and does
	not override abstract method setRelativeSize(int,int) in Resizable

最后，更新已发布API会导致后向兼容性问题。这就是为什么对现存API的演进，比如官方发布的Java Collection API，会给用户带来麻烦。当然，还有其他方式能够实现对API的改进，但是都不是明智的选择。比如，你可以为你的API创建不同的发布版本，同时维护老版本和新版本，但这是非常费时费力的，原因如下。

1. 这增加了你作为类库的设计者维护类库的复杂度。
2. 类库的用户不得不同时使用一套代码的两个版本，而这会增大内存的消耗，延长程序的载入时间，因为这种方式下项目使用的类文件数量更多了。


### 不同类型的兼容性：二进制、源代码和函数行为 ###

变更对Java程序的影响大体可以分成三种类型的兼容性，分别是：二进制级的兼容、源代码级的兼容，以及函数行为的兼容。

1. **二进制级的兼容性**表示现有的二进制执行文件能无缝持续链接（包括验证、准备和解析）和运行。比如，为接口添加一个方法就是二进制级的兼容，这种方式下，如果新添加的方法不被调用，接口已经实现的方法可以继续运行，不会出现错误。

2. **源代码级的兼容性**表示引入变化之后，现有的程序依然能成功编译通过。比如，向接口添加新的方法就不是源码级的兼容，因为遗留代码并没有实现新引入的方法，所以它们无法顺利通过编译。

3. **函数行为的兼容性**表示变更发生之后，程序接受同样的输入能得到同样的结果。比如，为接口添加新的方法就是函数行为兼容的，因为新添加的方法在程序中并未被调用（抑或该接口在实现中被覆盖了）。

## 概述默认方法 ##

默认方法由default修饰符修饰，并像类中声明的其他方法一样包含方法体。比如，你可以像下面这样在集合库中定义一个名为Sized的接口，在其中定义一个抽象方法size，以及一个默认方法isEmpty：

	public interface Sized {
		int size();
		default boolean isEmpty() {
			return size() == 0;
		}
	}

这样任何一个实现了Sized接口的类都会自动继承isEmpty的实现。因此，向提供了默认实现的接口添加方法就 不是 **源码兼容**的。

现在，我们回顾一下最初的例子，那个Java画图类库和你的游戏程序。具体来说，为了以兼容的方式改进这个库（即使用该库的用户不需要修改他们实现了Resizable的类），可以使用默认方法，提供setRelativeSize的默认实现：

	default void setRelativeSize(int wFactor, int hFactor){
		setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
	}

---

默认方法在Java 8的API中已经大量地使用了

Collection接口的stream方法就是默认方法。List接口的sort方法也是默认方法。

函数式接口，比如Predicate、Function以及Comparator也引入了新的默认方法，比如Predicate.and或者Function.andThen

### Java 8中的抽象类和抽象接口 ###

那么抽象类和抽象接口之间的区别是什么呢？它们不都能包含抽象方法和包含方法体的实现吗？

- 一个类只能继承一个抽象类，但是一个类可以实现多个接口。
- 一个抽象类可以通过实例变量（字段）保存一个通用状态，而接口是不能有实例变量的。

### 使用默认实现的例子——removeIf ###

这个测验里，假设你是Java语言和API的一个负责人。你收到了关于removeIf方法的很多请求，希望能为ArrayList、TreeSet、LinkedList以及其他集合类型添加removeIf方法。removeIf方法的功能是删除满足给定谓词的所有元素。你的任务是找到添加这个新方法、优化Collection API的最佳途径。

改进Collection API破坏性最大的方式是什么？你可以把removeIf的实现直接复制到Collection API的每个实体类中，但这种做法实际是在对Java界的犯罪。还有其他的方式吗？

所有的Collection类都实现了一个名为java.util.Collection的接口。太好了，那么我们可以在这里添加一个方法？是的！你只需要牢记，**默认方法是一种以源码兼容方式向接口内添加实现的方法**。这样实现Collction的所有类（包括并不隶属Collection API的用户扩展类）都能使用removeIf的默认实现。removeIf的代码实现如下（它实际就是Java 8 Collection API的实现）。

	default boolean removeIf(Predicate<? super E> filter) {
		boolean removed = false;
		Iterator<E> each = iterator();
		while(each.hasNext()) {
			if(filter.test(each.next())) {
				each.remove();
				removed = true;
			}
		}
		return removed;
	}

## 默认方法的使用模式 ##

### 可选方法 ###

类实现了接口，不过却刻意地将一些方法的实现留白。我们以Iterator接口为例来说。Iterator接口定义了hasNext、next，还定义了remove方法。Java 8之前，由于用户通常不会使用该方法，remove方法常被忽略。因此，实现Interator接口的类通常会为remove方法放置一个空的实现，这些都是些毫无用处的模板代码。

采用默认方法之后，你可以为这种类型的方法提供一个默认的实现，这样实体类就无需在自己的实现中显式地提供一个空方法。比如，在Java 8中，Iterator接口就为remove方法提供了一个默认实现，如下所示：

	interface Iterator<T> {

		boolean hasNext();

		T next();

		default void remove() {
			throw new UnsupportedOperationException();
		}
	}

通过这种方式，你可以减少无效的模板代码。实现Iterator接口的每一个类都不需要再声明一个空的remove方法了，因为它现在已经有一个默认的实现。

### 行为的多继承 ###

默认方法让之前无法想象的事儿以一种优雅的方式得以实现，即行为的多继承。这是一种让类从多个来源重用代码的能力

![](image/inheritance.png)

Java的类只能继承单一的类，但是一个类可以实现多接口。下面是Java API中对ArrayList类的定义：

	public class ArrayList<E> extends AbstractList<E>
		implements List<E>, RandomAccess, Cloneable,Serializable, Iterable<E>, Collection<E> {
	}

#### 类型的多继承 ####

这个例子中ArrayList继承了一个类，实现了六个接口。因此ArrayList实际是七个类型的直接子类，分别是：AbstractList、List、RandomAccess、Cloneable、Serializable、Iterable和Collection。所以，在某种程度上，我们早就有了类型的多继承。

由于Java 8中接口方法可以包含实现，类可以从多个接口中继承它们的行为（即实现的代码）。

保持接口的精致性和正交性能帮助你在现有的代码基上最大程度地实现代码复用和行为组合。

#### 利用正交方法的精简接口 ####

假设你需要为你正在创建的游戏定义多个具有不同特质的形状。有的形状需要调整大小，但是不需要有旋转的功能；有的需要能旋转和移动，但是不需要调整大小。

你可以定义一个单独的Rotatable接口，并提供两个抽象方法setRotationAngle和getRotationAngle，如下所示

	public interface Rotatable {
		void setRotationAngle(int angleInDegrees);
		int getRotationAngle();
		default void rotateBy(int angleInDegrees){
			setRotationAngle((getRotationAngle () + angle) % 360);
		}
	}

这种方式和**模板设计模式**有些相似，都是以其他方法需要实现的方法定义好框架算法。

现在，实现了Rotatable的所有类都需要提供setRotationAngle和getRotationAngle的实现，但与此同时它们也会天然地继承rotateBy的默认实现。

类似地，你可以定义之前看到的两个接口Moveable和Resizable。它们都包含了默认实现。下面是Moveable的代码：

	public interface Moveable {
		int getX();
		int getY();
		void setX(int x);
		void setY(int y);

		default void moveHorizontally(int distance){
			setX(getX() + distance);
		}

		default void moveVertically(int distance){
			setY(getY() + distance);
		}
	}

	public interface Resizable {
		int getWidth();
		int getHeight();
		void setWidth(int width);
		void setHeight(int height);
		void setAbsoluteSize(int width, int height);

		default void setRelativeSize(int wFactor, int hFactor){
			setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
		}
	}

#### 组合接口 ####

通过组合这些接口，你现在可以为你的游戏创建不同的实体类。

	public class Monster implements Rotatable, Moveable, Resizable {
	…
	}

	public class Sun implements Moveable, Rotatable {
	…
	}

![](image/inheritance2.png)

像你的游戏代码那样使用默认实现来定义简单的接口还有另一个好处。假设你需要修改moveVertically的实现，让它更高效地运行。你可以在Moveable接口内直接修改它的实现，所有实现该接口的类会自动继承新的代码

#### 继承不应该成为你一谈到代码复用就试图倚靠的万金油 ####

比如，从一个拥有100个方法及字段的类进行继承就不是个好主意，因为这其实会引入不必要的复杂性。你完全可以使用**代理**有效地规避这种窘境，即创建一个方法通过该类的成员变量直接调用该类的方法。

这就是为什么有的时候我们发现有些类被刻意地声明为final类型：声明为final的类不能被其他的类继承，避免发生这样的反模式，防止核心代码的功能被污染。注意，有的时候声明为final的类都会有其不同的原因，比如，String类被声明为final，因为我们不希望有人对这样的核心功能产生干扰。

这种思想同样也适用于使用默认方法的接口。通过精简的接口，你能获得最有效的组合，因为你可以只选择你需要的实现。

## 解决冲突的规则 ##

我们知道Java语言中一个类只能继承一个父类，但是一个类可以实现多个接口。随着默认方法在Java 8中引入，**有可能出现一个类继承了多个方法而它们使用的却是同样的函数签名**。

这种情况下，类会选择使用哪一个函数？在实际情况中，像这样的冲突可能极少发生，但是一旦发生这样的状况，必须要有一套规则来确定按照什么样的约定处理这些冲突。

	public interface A {
		default void hello() {
			System.out.println("Hello from A");
		}
	}
	public interface B extends A {
		default void hello() {
			System.out.println("Hello from B");
		}
	}
	public class C implements B, A {
		public static void main(String... args) {
			new C().hello();
		}
	}

### 解决问题的三条规则 ###

如果一个类使用相同的函数签名从多个地方（比如另一个类或接口）继承了方法，通过三条规则可以进行判断。

1. **类中的方法优先级最高**。类或父类中声明的方法的优先级高于任何声明为默认方法的优先级。

2. 如果无法依据第一条进行判断，那么子接口的优先级更高：**函数签名相同时，优先选择拥有最具体实现的默认方法的接口**，即如果B继承了A，那么B就比A更加具体。

3. 最后，如果还是无法判断，**继承了多个接口的类必须通过显式覆盖和调用期望的方法**。

### 选择提供了最具体实现的默认方法的接口 ###

例子中C类同时实现了B接口和A接口，而这两个接口恰巧又都定义了名为hello的默认方法。另外，B继承自A。

![](image/conflict.png)

编译器会使用声明的哪一个hello方法呢？按照规则(2)，应该选择的是提供了最具体实现的默认方法的接口。由于B比A更具体，所以应该选择B的hello方法。所以，程序会打印输出“Hello from B”。

---

如果C像下面这样继承自D

![](image/conflict2.png)

依据规则(1)，类中声明的方法具有更高的优先级。D并未覆盖hello方法，可是它实现了接口A。所以它就拥有了接口A的默认方法。

规则(2)说如果类或者父类没有对应的方法，那么就应该选择提供了最具体实现的接口中的方法。因此，编译器会在接口A和接口B的hello方法之间做选择。由于B更加具体，所以程序会再次打印输出“Hello from B”。

---

在这个测验中继续复用之前的例子，唯一的不同在于D现在显式地覆盖了从A接口中继承的hello方法。输出会是什么呢？

	public class D implements A{
		void hello(){
			System.out.println("Hello from D");
		}
	}

	public class C extends D implements B, A {
		public static void main(String... args) {
			new C().hello();
		}
	}

由于依据规则(1)，父类中声明的方法具有更高的优先级，所以程序会打印输出“Hello from D”。

注意，D的声明如下：

	public abstract class D implements A {
		public abstract void hello();
	}

这样的结果是，虽然在结构上，其他的地方已经声明了默认方法的实现，C还是必须提供自己的hello方法


### 冲突及如何显式地消除歧义 ###

假设B不再继承A

	public interface A {
		void hello() {
			System.out.println("Hello from A");
		}
	}
	public interface B {
		void hello() {
			System.out.println("Hello from B");
		}
	}
	public class C implements B, A { }

![](image/conflict3.png)

这时规则(2)就无法进行判断了，因为从编译器的角度看没有哪一个接口的实现更加具体，两个都差不多。A接口和B接口的hello方法都是有效的选项。所以，Java编译器这时就会抛出一个编译错误，因为**它无法判断哪一个方法更合适**：“Error: class C inherits unrelated defaults for hello()from types B and A.”

**冲突的解决**

解决这种两个可能的有效方法之间的冲突，没有太多方案；你只能显式地决定你希望在C中使用哪一个方法。

为了达到这个目的，你可以覆盖类C中的hello方法，在它的方法体内显式地调用你希望调用的方法。

Java 8中引入了一种新的语法X.super.m(…)，其中X是你希望调用的m方法所在的父接口。举例来说，如果你希望C使用来自于B的默认方法，它的调用方式看起来就如下所示：

	public class C implements B, A {
		void hello(){
			B.super.hello();
		}
	}

---

假设接口A和B的声明如下所示：

	public interface A{
		default Number getNumber(){
			return 10;
		}
	}
	public interface B{
		default Integer getNumber(){
			return 42;
		}
	}

类C的声明如下：

	public class C implements B, A {
		public static void main(String... args) {
			System.out.println(new C().getNumber());
		}
	}

输出什么呢？

答案：类C无法判断A或者B到底哪一个更加具体。这就是类C无法通过编译的原因。


### 菱形继承问题 ###

	public interface A{
		default void hello(){
			System.out.println("Hello from A");
		}
	}

	public interface B extends A { }

	public interface C extends A { }

	public class D implements B, C {
		public static void main(String... args) {
			new D().hello();
		}
	}

![](image/conflict4.png)

类D中的默认方法到底继承自什么地方 ——源自B的默认方法，还是源自C的默认方法？实际上只有一个方法声明可以选择。只有A声明了一个默认方法。由于这个接口是D的父接口，代码会打印输出“Hello from A”。

---

如果B中也提供了一个默认的hello方法，并且函数签名跟A中的方法也完全一致，

根据规则(2)，编译器会选择提供了更具体实现的接口中的方法。由于B比A更加具体，所以编译器会选择B中声明的默认方法。

如果B和C都使用相同的函数签名声明了hello方法，就会出现冲突，你需要显式地指定使用哪个方法。

---

如果你在C接口中添加一个抽象的hello方法（这次添加的不是一个默认方法），会发生什么情况呢？

	public interface C extends A {
		void hello();
	}

这个新添加到C接口中的抽象方法hello比由接口A继承而来的hello方法拥有更高的优先级，因为C接口更加具体。因此，类D现在需要为hello显式地添加实现，否则该程序无法通过编译。

## 小结 ##

- Java 8中的接口可以通过默认方法和静态方法提供方法的代码实现。
- 默认方法的开头以关键字default修饰，方法体与常规的类方法相同。
- 向发布的接口添加抽象方法不是**源码兼容**的。
- 默认方法的出现能帮助库的设计者以后向兼容的方式演进API。
- 默认方法可以用于创建可选方法和行为的多继承。
- 我们有办法解决由于一个类从多个接口中继承了拥有相同函数签名的方法而导致的冲突。
- 类或者父类中声明的方法的优先级高于任何默认方法。如果前一条无法解决冲突，那就选择同函数签名的方法中实现得最具体的那个接口的方法。
- 两个默认方法都同样具体时，你需要在类中覆盖该方法，显式地选择使用哪个接口中提供的默认方法。

