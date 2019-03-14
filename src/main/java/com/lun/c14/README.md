# 函数式编程的技巧 #

## 无处不在的函数 ##

术语“函数式编程”意指函数或者方法的行为应该像“数学函数”一样——
没有任何副作用。

对于使用函数式语言的程序员而言，这个术语的范畴更加宽泛，它还意味着函数可以像任何其他值一样随意使用：可以作为参数传递，可以作为返回值，还能存储在数据结构中。

能够像普通变量一样使用的函数称为**一等函数**（first-class function）。这是Java 8补充的全新内容：通过::操作符，你可以创建一个方法引用，像使用函数值一样使用方法，也能使用Lambda表达式（比如，(int x) -> x + 1）直接表示方法的值。Java 8中使用下面这样的方法引用将一个方法引用保存到一个变量是合理合法的：

	Function<String, Integer> strToInt = Integer::parseInt;

### 高阶函数 ###

目前为止，我们使用函数值属于一等这个事实只是为了将它们传递给Java 8的流处理操作），达到行为参数化的效果。

	Comparator<Apple> c = comparing(Apple::getWeight);

![](image/comparator.png)

	Function<String, String> transformationPipeline
		= addHeader.andThen(Letter::checkSpelling)
		.andThen(Letter::addFooter);

函数式编程的世界里，如果函数，比如Comparator.comparing，能满足下面任一要求就可以被称为**高阶函数**（higher-order function）：

- 接受至少一个函数作为参数
- 返回的结果是一个函数

这些都和Java 8直接相关。因为Java 8中，函数不仅可以作为参数传递，还可以作为结果返回，能赋值给本地变量，也可以插入到某个数据结构。比如，一个**袖珍计算器的程序**可能有这样的一个Map<String, Function<Double, Double&gt;&gt;，它将字符串sin映射到方法Function<Double,
Double&gt;，实现对Math::sin的方法引用。

**微积分示例** 接受一个函数作为参数（比如，
(Double x) -> x \* x），又返回一个函数作为结果（这个例子中返回值是(Double x) -> 2 * x），你可以用不同的方式实现类型定义，如下所示：

	Function<Function<Double,Double>, Function<Double,Double>>

我们把它定义成Function类型（最左边的Function），目的是想显式地向你确认可以将这个函数传递给另一个函数。但是，最好使用差异化的类型定义，函数签名如下：

	Function<Double,Double> differentiate(Function<Double,Double> func)

其实二者说的是同一件事。

### 副作用和高阶函数 ###

我们了解到传递给流操作的函数应该是无副作用的，否则会发生各种各样的问题（比如错误的结果，有时由于竞争条件甚至会产生我们无法预期的结果）。**这一原则在你使用高阶函数时也同样适用**。编写高阶函数或者方法时，你无法预知会接收什么样的参数——一旦传入的参数有某些副作用，我们将会一筹莫展！

如果作为参数传入的函数可能对你程序的状态产生某些无法预期的改变，一旦发生问题，你将很难理解程序中发生了什么；它们甚至会用某种难于调试的方式调用你的代码。因此，将所有你愿意接收的作为参数的函数可能带来的副作用以文档的方式记录下来是一个不错的设计原则，最理想的情况下你接收的函数参数应该没有任何副作用！

### 科里化 ###

Currying

>科里化的概念最早由俄国数学家Moses Schönfinkel引入，而后由著名的数理逻辑学家哈斯格尔·科里（Haskell Curry）丰富和发展，科里化由此得名。它表示一种将一个带有n元组参数的函数转换成n个一元函数链的方法。

它是一种可以帮助你模块化函数、提高代码重用性的技术。

应用程序通常都会有国际化的需求，将一套单位转换到另一套单位是经常碰到的问题。

单位转换通常都会涉及转换因子以及基线调整因子的问题。比如，将摄氏度转换到华氏度的
公式是CtoF(x) = x*9/5 + 32。

所有的单位转换几乎都遵守下面这种模式：

1. 乘以转换因子
2. 如果需要，进行基线调整

你可以使用下面这段通用代码表达这一模式：

	static double converter(double x, double f, double b) {
		return x * f + b;
	}

这里x是你希望转换的数量，f是转换因子，b是基线值。但是这个方法有些过于宽泛了。通常，你还需要在同一类单位之间进行转换，比如公里和英里。当然，你也可以在每次调用converter方法时都使用3个参数，但是每次都提供转换因子和基准比较繁琐，并且你还极有可能输入错误。

>PS.与初中学的一次函数y = kx + b类似。

当然，你也可以为每一个应用编写一个新方法，不过这样就无法对底层的逻辑进行复用。这里我们提供一种简单的解法，它既能充分利用已有的逻辑，又能让converter针对每个应用进行定制。

你可以定义一个“工厂”方法，它生产带一个参数的转换方法，我们希望借此来说明科里化。下面是这段代码：

	static DoubleUnaryOperator curriedConverter(double f, double b){
		return (double x) -> x * f + b;
	}

现在，你要做的只是向它传递转换因子和基准值（f和b），它会不辞辛劳地按照你的要求返回一个方法（使用参数x）。比如，你现在可以按照你的需求使用工厂方法产生你需要的任何converter：

	DoubleUnaryOperator convertCtoF = curriedConverter(9.0/5, 32);
	DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6, 0);
	DoubleUnaryOperator convertKmtoMi = curriedConverter(0.6214, 0);

由于DoubleUnaryOperator定义了方法applyAsDouble，你可以像下面这样使用你的converter：

	double gbp = convertUSDtoGBP.applyAsDouble(1000);

这样一来，你的代码就更加灵活了，同时它又复用了现有的转换逻辑！让我们一起回顾下你都做了哪些工作。你并没有一次性地向converter方法传递所有的参数x、f和b，相反，你只是使用了参数f和b并返回了另一个方法，这个方法会接收参数x，最终返回你期望的值x * f + b。通过这种方式，你复用了现有的转换逻辑，同时又为不同的转换因子创建了不同的转换方法。

### 科里化的理论定义 ###

科里化是一种将具备2个参数（比如，x和y）的函数f转化为使用一个参数的函数g，并且这个函数的返回值也是一个函数，它会作为新函数的一个参数。后者的返回值和初始函数的返回值相同，即f(x,y) = (g(x))(y)。

当然，我们可以由此推出：你可以将一个使用了6个参数的函数科里化成一个接受第2、4、6号参数，并返回一个接受5号参数的函数，这个函数又返回一个接受剩下的第1号和第3号参数的函数。

一个函数使用所有参数仅有部分被传递时，通常我们说这个函数是部分应用的（partially applied）。

## 持久化数据结构 ##

这一主题有各种名称，比如函数式数据结构、不可变数据结构，不过最常见的可能还要算持久化数据结构（不幸的是，这一术语和数据库中的持久化概念有一定的冲突，数据库中它代表的是“生命周期比程序的执行周期更长的数据”）。

应该注意的第一件事是，**函数式方法不允许修改任何全局数据结构或者任何作为参数传入的参数**。为什么呢？因为一旦对这些数据进行修改，两次相同的调用就很可能产生不同的结构——这违背了引用透明性原则，我们也就无法将方法简单地看作由参数到结果的映射。

### 破坏式更新和函数式更新的比较 ###

[PersistentTrainJourney](PersistentTrainJourney.java)

假设你需要使用一个可变类TrainJourney（利用一个简单的**单向链接**列表实现）表示从A地到B地的火车旅行，你使用了一个整型字段对旅程的一些细节进行建模，比如当前路途段的价格。旅途中你需要换乘火车，所以需要使用几个由onward字段串联在一起的TrainJourney对象；直达火车或者旅途最后一段对象的onward字段为null：

	class TrainJourney {
		public int price;
		public TrainJourney onward;
		public TrainJourney(int p, TrainJourney t) {
			price = p;
			onward = t;
		}
	}

假设你有几个相互分隔的TrainJourney对象分别代表从X到Y和从Y到Z的旅行。你希望创建一段新的旅行，它能将两个TrainJourney对象串接起来（即从X到Y再到Z）。

一种方式是采用简单的传统命令式的方法将这些火车旅行对象链接起来，

	static TrainJourney link(TrainJourney a, TrainJourney b){
		if (a==null) return b;
		TrainJourney t = a;
		while(t.onward != null){
			t = t.onward;
		}
		t.onward = b;
		return a;
	}

这就出现了一个问题：假设变量firstJourney包含了从X地到Y地的线路，另一个变量secondJourney包含了从Y地到Z地的线路。如果你调用link(firstJourney, secondJourney)方法， 这段代码会破坏性地更新firstJourney ， 结果secondJourney 也会加被入到firstJourney，最终请求从X地到Z地的用户会如其所愿地看到整合之后的旅程，不过从X地到Y地的旅程也被破坏性地更新了。

**这之后，变量firstJourney就不再代表从X到Y的旅程，而是一个新的从X到Z的旅程了**！ 这一改动会导致依赖原先的firstJourney 代码失效！ 假设firstJourney表示的是清晨从伦敦到布鲁塞尔的火车，这趟车上后一段的乘客本来打算要去布鲁塞尔，可是发生这样的改动之后他们莫名地多走了一站，最终可能跑到了科隆。现在你大致了解了数据结构修改的可见性会导致怎样的问题了，作为程序员，我们一直在与这种缺陷作斗争。

**函数式编程解决这一问题的方法是禁止使用带有副作用的方法**。如果你需要使用表示计算结果的数据结果，那么请创建它的一个副本而不要直接修改现存的数据结构。这一最佳实践也适用于标准的面向对象程序设计。

不过，对这一原则，也存在着一些异议，比较常见的是认为这样做会导致过度的对象复制，有些程序员会说“我会记住那些有副作用的方法”或者“我会将这些写入文档”。但这些都不能解决问题，这些坑都留给了接受代码维护工作的程序员。采用函数式编程方案的代码如下：

	static TrainJourney append(TrainJourney a, TrainJourney b){
		return a==null ? b : new TrainJourney(a.price, append(a.onward, b));
	}

很明显，这段代码是函数式的（它没有做任何修改，即使是本地的修改），它没有改动任何现存的数据结构。**不过，也请特别注意**，这段代码有一个特别的地方，它并未创建整个新TrainJourney对象的副本——如果a是n个元素的序列，b是m个元素的序列，那么调用这个函数后，它返回的是一个由n+m个元素组成的序列，这个序列的前n个元素是新创建的，而后m个元素和TrainJourney对象b是共享的。**另外，也请注意**，用户需要确保不对append操作的结果进行修改，因为一旦这样做了，作为参数传入的TrainJourney对象序列b就可能被破坏。下图解释说明了破坏式append和函数式append之间的区别。

![](image/append.png)

### 另一个使用Tree的例子 ###

[PersistentTree](PersistentTree.java)

我们想讨论的对象是二叉查找树，它也是HashMap实现类似接口的方式。我们的设计中Tree包含了String类型的键，以及int类型的键值，它可能是名字或者年龄：

	class Tree {
		private String key;
		private int val;
		private Tree left, right;
		public Tree(String k, int v, Tree l, Tree r) {
			key = k; val = v; left = l; right = r;
		}
	}

	class TreeProcessor {
		public static int lookup(String k, int defaultval, Tree t) {
			if (t == null) return defaultval;
			if (k.equals(t.key)) return t.val;
			return lookup(k, defaultval,
				k.compareTo(t.key) < 0 ? t.left : t.right);
		}
		// 处理Tree的其他方法
	}

你希望通过二叉查找树找到String值对应的整型数。现在，我们想想你该如何更新与某个键对应的值（**简化起见，我们假设键已经存在于这个树中了**）：

	public static void update(String k, int newval, Tree t) {
		if (t == null) { /* 应增加一个新的节点 */ }
		else if (k.equals(t.key)) t.val = newval;
		else update(k, newval, k.compareTo(t.key) < 0 ? t.left : t.right);
	}

对这个例子，增加一个新的节点会复杂很多；最简单的方法是让update直接返回它刚遍历的树（除非你需要加入一个新的节点，否则返回的树结构是不变的）。现在，这段代码看起来已经有些臃肿了（因为update试图对树进行原地更新，它返回的是跟传入的参数同样的树，但是如果最初的树为空，那么新的节点会作为结果返回）。

	public static Tree update(String k, int newval, Tree t) {
		if (t == null)
			t = new Tree(k, newval, null, null);
		else if (k.equals(t.key))
			t.val = newval;
		else if (k.compareTo(t.key) < 0)
			t.left = update(k, newval, t.left);
		else
			t.right = update(k, newval, t.right);
		return t;
	}

注意，这两个版本的update都会对现有的树进行修改，这意味着使用树存放映射关系的所有用户都会感知到这些修改。

### 采用函数式的方法 ###

如何通过函数式的方法解决呢？你需要为新的键-值对创建一个新的节点，除此之外你还需要创建从树的根节点到新节点的路径上的所有节点。通常而言，这种操作的代价并不太大，如果树的深度为d，并且保持一定的平衡性，那么这棵树的节点总数是2^d，这样你就只需要重新创建树的一小部分节点了。


    public static Tree fupdate(String k, int newval, Tree t) {
        return (t == null) ?
            new Tree(k, newval, null, null) :
             k.equals(t.key) ?
               new Tree(k, newval, t.left, t.right) :
          k.compareTo(t.key) < 0 ?
            new Tree(t.key, t.val, fupdate(k,newval, t.left), t.right) :
            new Tree(t.key, t.val, t.left, fupdate(k,newval, t.right));
    }

这段代码中，我们通过一行语句进行的条件判断，没有采用if-then-else这种方式，目的是希望强调一个思想，那就是该函数体仅包含一条语句，**没有任何副作用**。不过你也可以按照自己的习惯，使用if-then-else这种方式，在每一个判断结束处使用return返回。

那么，update 和fupdate之间的区别到底是什么呢？

我们注意到，前文中方法update有这样一种假设，即每一个update的用户都希望共享同一份数据结构，也希望能了解程序任何部分所做的更新。因此，无论任何时候，只要你使用非函数式代码向树中添加某种形式的数据结构，请立刻创建它的一份副本，因为谁也不知道将来的某一天，某个人会突然对它进行修改，这一点非常重要（不过也经常被忽视）。

与之相反，fupdate是纯函数式的。它会创建一个新的树，并将其作为结果返回，通过参数的方式实现共享。下图对这一思想进行了阐释。你使用了一个树结构，树的每个节点包含了person对象的姓名和年龄。调用fupdate不会修改现存的树，它会在原有树的一侧创建新的节点，同时保证不损坏现有的数据结构。

![](image/tree.png)

这种函数式数据结构通常被称为持久化的——数据结构的值始终保持一致，不受其他部分变化的影响——这样，作为程序员的你才能确保fupdate不会对作为参数传入的数据结构进行修改。不过要达到这一效果还有一个附加条件：这个约定的另一面是，**所有使用持久化数据结构的用户都必须遵守这一“不修改”原则**。如果不这样，忽视这一原则的程序员很有可能修改fupdate的结果（比如，修改Emily的年纪为20岁）。这会成为一个例外（也是我们不期望发生的）事件，为所有使用该结构的方法感知，并在之后修改作为参数传递给fupdate的数据结构。

通过这些介绍，我们了解到fupdate可能有更加高效的方式：基于“不对现存结构进行修改”规则，对仅有细微差别的数据结构（比如，用户A看到的树结构与用户B看到的就相差不多），我们可以考虑对这些通用数据结构使用共享存储。你可以凭借编译器，将Tree类的字段key、val、left以及right声明为final执行，“**禁止对现存数据结构的修改**”这一规则；不过我们也需要注意final只能应用于类的字段，无法应用于它指向的对象，如果你想要对对象进行保护，你需要将其中的字段声明为final，以此类推。

---

你可能会说：“我希望对树结构的更新对某些用户可见（当然，这句话的潜台词是其他人看不到这些更新）。”那么，要实现这一目标，你可以通过两种方式：

1. 第一种是典型的Java解决方案（对对象进行更新时，你需要特别小心，慎重地考虑是否需要在改动之前保存对象的一份副本）。
2. 另一种是函数式的解决方案：逻辑上，你在做任何改动之前都会创建一份新的数据结构（这样一来就不会有任何的对象发生变更），只要确保按照用户的需求传递给他正确版本的数据结构就好了。
 
这一想法甚至还可以通过API直接强制实施。如果数据结构的某些用户需要进行可见性的改动，它们应该调用API，返回最新版的数据结构。对于另一些客户应用，它们不希望发生任何可见的改动（比如，需要长时间运行的统计分析程序），就直接使用它们保存的备份，因为它知道这些数据不会被其他程序修改。

有些人可能会说这个过程很像更新刻录光盘上的文件，刻录光盘时，一个文件只能被激光写入一次，该文件的各个版本分别被存储在光盘的各个位置（智能光盘编辑软件甚至会共享多个不同版本之间的相同部分），你可以通过传递文件起始位置对应的块地址（或者名字中编码了版本信息的文件名）选择你希望使用哪个版本的文件。Java中，情况甚至比刻录光盘还好很多，不再使用的老旧数据结构会被Java虚拟机自动垃圾回收掉。

## Stream的延迟计算 ##





