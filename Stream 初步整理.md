# Stream 学习整理
目录
- [引言](#1)
    - [应用](#1.1) 
    - [特性](#1.2) 
- [流的生成 Stream Source](#2)
    - [方式](#2.1) 
    - [Stream 的三种包装类](#2.1)
- [流的使用](#3)
    - [使用流的一般步骤](#3.1) 
    - [Stream 与其它数据结构的转换](#3.1)
    - [Stream 的操作](#3.1)
- [参考文献](#4)
------

## <span id = "1">1. 引言</span>
### <span id = "1.1">1.1 应用</span>
- 对集合（Collection）对象功能的增强，提供串行和并行两种模式进行聚合操作（aggregate operation）；
- 借助 Lambda 进行大批量数据操作(bulk data operation)  ； 
- 补充：
    - 并行模式通过 fork/join 框架实现， 即 Stream API 实现了高性能的并发程序的封装；
    - import：java.util.stream；
### <span id = "1.2">1.2 特性</span>
- Stream 非集合元素，非数据结构，用操作管道从 source（Collection、数组、generator function、IO channel）抓取数据且数据量不限；【生成方法和IO channel 之后学习】
- 不修改其所封装的底层数据结构的数据，流操作会产生新 Stream；
- 所有 Stream 的操作必须以 lambda 表达式为参数；
- 不支持索引访问；
- 容易实现向其他数据结构，如 List、Set、Array 等的转换；
- 高级版本的 Iterator：
    - 单向，不可往复，数据只能遍历一次；
    - Iterator 只能显示地通过命令式执行串行化操作，而 Stream 可并行化地隐式执行数据转换和计算；
- ==总结==：便利、简洁、高效、强大；

## <span id = "2">2. 流的生成 Stream Source</span>
### <span id = "2.1">2.1 方式</span>
- from Collection 和数组
    - Collection.stream()
    - Collection.parallelStream()
    - Arrays.stream(T array) or Stream.of()
- BufferedReader
    - java.io.BufferedReader.lines()
- 通过 Stream 接口的静态工厂方法
    - java.util.stream.IntStream.range()
    - java.nio.file.Files.walk()
    > - Stream.of() : 有两个overload方法，一个接受变长参数，一个接口单一值
    > - Stream.generate(Math::random) : 生成一个无限长度的Stream，其元素的生成是通过给定的Supplier（这个接口可以看成一个对象的工厂，每次调用返回一个给定类型的对象）;  
    > - Stream.iterate(1, item -> item + 1).limit(10).forEach(System.out::println) : 生成无限长度的Stream，和generator不同的是，其元素的生成是重复对给定的种子值(seed)调用用户指定函数来生成的。其中包含的元素可以认为是：seed，f(seed),f(f(seed))无限循环;
- 自定义构建
    java.util.Spliterator
- 其它
    - Random.ints()
    - BitSet.stream()
    - Pattern.splitAsStream(java.lang.CharSequence)
    - JarFile.stream()

### <span id = "2.2">2.2 Stream 的三种包装类</span>
- 为三种基本数值型提供了对应的 Stream：IntStream、LongStream、DoubleStream；  
补充：也可用 Stream<Integer>、Stream<Long>、Stream<Double>，但 boxing 和 unboxing 会很耗时；
## <span id = "3">3. 流的使用</span>

### <span id = "3.1">3.1 使用流的一般步骤</span>
- 获取一个数据源（source）→ 数据转换 → 执行操作获取目标结果；  
补充：每次转换原有 Stream 对象不改变，返回一个新的 Stream ；  
![Stream](https://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/img001.png)
![通用语法](http://img04.taobaocdn.com/imgextra/i4/90219132/T2ycFgXQ8XXXXXXXXX_!!90219132.jpg)

### <span id = "3.2">3.2 Stream 与其它数据结构的转换</span>
-  Array  
    String[] strArray1 = stream.toArray(String[]::new);
- Collection  
    List<String> list1 = stream.collect(Collectors.toList());  
    List<String> list2 = stream.collect(Collectors.toCollection(ArrayList::new));  
    Set set1 = stream.collect(Collectors.toSet());  
    Stack stack1 = stream.collect(Collectors.toCollection(Stack::new));  
- String  
    String str = stream.collect(Collectors.joining()).toString();

### <span id = "3.3">3.3 Stream 的操作</span>

- 流的操作类型 
    - **Intermediate**：一个流后可有零个或多个 intermediate 操作；
        - 目的：打开流，做出某种程度的数据映射/过滤，然后返回一个新的流，交给下一个操作使用；
        - 具有惰性化（lazy），仅实现方法调用，并未真正开始流的遍历；
        - 包括方法：map (mapToInt, flatMap 等)、 filter、 distinct、 sorted、 peek、 limit、 skip、 parallel、 sequential、 unordered
        - 补充：flatMap 和 map类似，不同的是其每个元素转换得到的是Stream对象，会把子Stream中的元素压缩到父集合中；
    - **Terminal**：一个流只能有一个 terminal 操作，为流的最后一个操作；
        - 真正开始流的遍历，并且会生成一个结果（eg：count方法会有一个统计结果），或者一个 side effect（eg：forEach）；
        - 包括方法：forEach、 forEachOrdered、 toArray、 reduce、 collect、 min、 max、 count、 anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 iterator
    - **short-circuiting**：
        - 对于一个 intermediate 操作，若接受的是一个无限大（infinite/unbounded）的 Stream，可返回一个有限的新 Stream；
        - 对于一个 terminal 操作，若接受的是一个无限大的 Stream，可在有限时间计算出结果；
        - 当操作一个无限大的 Stream 且需在有限时间内完成操作，则在管道内拥有一个 short-circuiting 操作是必要非充分条件；
        - 包括方法：anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 limit
    - 补充： 一个 Stream 的多次转换操作 (Intermediate 操作) 只会在 Terminal 操作的时候融合起来，一次循环完成，可理解为 Stream 里有个操作函数的集合，每次转换操作就是把转换函数放入这个集合中，在 Terminal 操作的时候循环 Stream 对应的集合，然后对每个元素执行所有的函数。

- 典型操作 [实现](https://github.com/littleYuting/testLambda/blob/master/src/testContainers.java)

4. <span id = "4">参考文献</span>

- [Java 8 中的 Streams API 详解](https://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/index.html)  
- [Java8初体验（二）Stream语法详解](http://ifeve.com/stream/)  
- [进阶学习
](https://www.zybuluo.com/changedi/note/622375)
