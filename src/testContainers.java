import javax.jws.Oneway;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public class testContainers {
        public static void main(String[] args){
            ArrayList<String> list = new ArrayList<String>(Arrays.asList("af","bcws","cfw","daw","b","f","c"));
            ArrayList<Integer> list_stream = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,6,6));

//        testListAndLambda(list);
//            testStream(list_stream);
//            testMapAndLambda();
            testOptional();
        }

        public static void testListAndLambda(ArrayList<String> list){
            //foreach
            // 签名 void forEach(Consumer<? super E> action) 对容器中的每个元素执行 action 指定的动作【传统方法：for】
            list.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if (s.equals("b")) { System.out.println("find b~");}
                }
            });
            list.forEach(str -> {if (str.equals("b")) { System.out.println("find b~"); }});
            // removeIf
            // 签名 boolean removeIf (Predicate<? super E> filter) 删除容器中所有满足 filter 指定条件的元素【传统方法：Iterator】
            list.removeIf(new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return s.equals("c");
                }
            });
            list.removeIf(s -> s.equals("c"));//对于已经删除的元素，在调用该方法的时候不报错
            System.out.println(list);
            // replaceAll
            // 签名 void replaceAll(UnaryOperator<E> operator) 对每个元素执行 operator 指定的操作
            list.replaceAll(new UnaryOperator<String>() {
                @Override
                public String apply(String s) {
                    return s.toUpperCase();
                }
            });
            System.out.println(list);
            list.replaceAll(s -> s.toLowerCase());
            System.out.println(list);
            // sort
            // 签名 void sort(Comparator<? super E> c)
            list.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.length() - o2.length();
                }
            });
            list.sort((s1,s2) -> s1.length() - s2.length());
            System.out.println(list);
        }

        public static void testMapAndLambda(){
            HashMap<Integer,String> map = new HashMap<>();
            map.put(1,"cyt");
            map.put(2,"ly");
            map.put(3,"lrx");
            //foreach
            // 签名 void forEach(Biconsumer<? super K,? super V> action) 对容器中的每个元素执行 action 指定的动作【传统方法：for + map.entry】
            map.forEach(new BiConsumer<Integer, String>() {
                @Override
                public void accept(Integer integer, String s) {
                    System.out.println(integer+"="+s);
                }
            });
            map.forEach((k,v)->System.out.println(k+"="+v));
            // replaceAll
            map.replaceAll((k,v)->v.toUpperCase());
            // merge
            map.merge(1,"jjj",(v1,v2)->v1+v2);
            map.forEach((k,v)->System.out.println(k+"="+v));// 1=CYTjjj、2=LY、3=LRX
            // compute
            map.compute(1,(k,v)->v!=null?"new_cyt":v);
            map.forEach((k,v)->System.out.println(k+"="+v));// 1=new_cyt、2=LY、3=LRX

        }

        public static void testStream(ArrayList<Integer> list_stream){
            // ———————————通过List创建Stream——————————————
            // 所有 javaCollection 都有 stream() 和 parallelStream() 方法中构造一个 Stream
            // 统计列表中某元素出现的个数 [count]
            System.out.println(list_stream.stream().filter(s->s.equals(6)).count());// output:3
            // 列表对筛选元素进行操作并转化为集合存储 [filter]
            System.out.println(list_stream.stream().filter(s->!s.equals(6)).map(s->s*s).
                    collect(Collectors.toList()));// [1, 4, 9, 16, 25]
            // 列表求和（reduce 好在可以设定初值， collector.summarize 好在输出结果非常丰富）
            System.out.println(list_stream.stream().reduce(2,(a,b)->a+2*b));// [Output : 68]初始 sum = 2,对每个元素乘以2再求和
            System.out.println(list_stream.stream().collect(Collectors.summarizingInt(a->2*a)));// IntSummaryStatistics{count=8, sum=66, min=2, average=8.250000, max=12}
            // 列表求平均数
            System.out.println(list_stream.stream().collect(Collectors.averagingInt(i->i)));// Output: 4.125
            // 求元素的最大数
                // first way
            List<Double> list_max = new Random().doubles(1,10)
                    .limit(5).boxed().collect(Collectors.toList());// 生成 【1,10】的 5 个随机数
            System.out.println(list_max);// [7.963467694210298, 1.573520441932426, 2.0374186556514022, 8.221803412069399, 5.052069918470123]
            System.out.println(list_max.stream().collect(Collectors.maxBy(Comparator.comparingDouble(i->i*3))));//Optional[8.221803412069399]
                // second way
            System.out.println(list_max.stream().reduce(Math::max));// Optional[9.519512696586744]
            // 字符串拼接
            System.out.println(list_stream.stream().map(i->i.toString()).
                    collect(Collectors.joining("#")));// 1#2#3#4#5#6#6#6
            System.out.println(list_stream.stream().map(i->i.toString()).
                    collect(Collectors.joining(",","[","]")));//[1,2,3,4,5,6,6,6]
            System.out.println(list_stream.stream().map(i->i.toString()).reduce("#", String::concat));//#12345666
            System.out.println(list_stream.stream().map(Objects::toString).reduce("Start", String::concat));//Start12345666
            System.out.println(list_stream.stream().map(Objects::toString).
                    reduce(new StringJoiner(",","[","]"),StringJoiner::add,StringJoiner::merge));//[1,2,3,4,5,6,6,6]
            // 分组
            System.out.println(list_stream.stream().collect(groupingBy(i->i>3)));//{false=[1, 2, 3], true=[4, 5, 6, 6, 6]}
            // 根据 list 创建 map
            List<Integer> list1 = IntStream.range(2,10).boxed().collect(Collectors.toList());
            Map<Integer, Integer> map1 = list1.stream().collect(Collectors.toMap(p->p,q->q*2));// {2=4, 3=6, 4=8, 5=10, 6=12, 7=14, 8=16, 9=18}
            System.out.println(map1);
            // peak
            Stream.of("one","two","three","four").filter(v->v.length()>3).peek(v->System.out.println("the filtered value: "+v)).
                    map(String::toUpperCase).peek(v->System.out.println("the mappped value: "+v)).collect(Collectors.toList());
            //peek output: the filtered value: three, the mappped value: THREE, the filtered value: four,   the mappped value: FOUR
        // ———————————通过数组创建Stream——————————————
            String[] arr_v = {"aa","bbb","cccc","dd"};
            // 也有针对具体元素类型的 InStream 与 DoubleStream
            Stream<String> stringStream = Arrays.stream(arr_v);// 获取指定范围的 stream ： Arrays.stream(arr_v， 1,3)
            System.out.println(stringStream.count());// Output:4,统计数组的个数
            Stream<String> stringStream01 = Stream.of(arr_v);
            // 重用 a stream chain 的中间操作
//            System.out.println(Stream.of("cyt","ly","hkp","lrx","zjn").map(String::length).collect(Collectors.toList()));// 统计每个字符串元素的长度
            Supplier<Stream<String>> streamSupplier = ()->Stream.of("cyt","ly","hkp","lrx","zjn").map(String::toUpperCase).sorted();
            streamSupplier.get().filter(s->s.startsWith("C")).forEach(System.out::println); // output: CYT
            System.out.println(streamSupplier.get().collect(Collectors.joining(",","*","*")));// output : *CYT,HKP,LRX,LY,ZJN*
            // flapMap
            Map<String, List<Integer>> map = new LinkedHashMap<>();
            map.put("a", Arrays.asList(1,2,3));
            map.put("b", Arrays.asList(4,5,6));
            System.out.println(map.values().stream().flatMap(List::stream).collect(Collectors.toList()));// output: [1, 2, 3, 4, 5, 6]
            //**********************//
            List<Map<String, String>> list_flap = new ArrayList<>();
            Map<String, String> map_flap1 = new HashMap<>();
            Map<String, String> map_flap2 = new HashMap<>();
            map_flap1.put("1","ae");
            map_flap1.put("2","bd");
            map_flap2.put("3","ccc");
            map_flap2.put("4","dd");
            map_flap2.put("5","dd");
            list_flap.add(map_flap1);
            list_flap.add(map_flap2);
            Set<String> output = list_flap.stream().map(Map::values).flatMap(Collection::stream).collect(Collectors.toSet());
            System.out.println(output);//output: [dd, bd, ccc, ae]
            // limit 返回 stream 的前 n 个元素, skip 扔掉前 n 个元素
            System.out.println(list_stream.stream().limit(5).skip(2).collect(Collectors.toList()));//output: [3,4,5]
            // 排序 Stream 的排序比数组排序强大之处在于可以对 stream 先进行各类 map、filter、limit、skip 等
            Stream.of("cyt","ly","hkp","lrx","zjn").limit(4).sorted((p1,p2)->p1.compareTo(p2)).forEach(System.out::println);// output: cyt、hkp、lrx、ly
            // distinct
            System.out.println(list_stream.stream().distinct().collect(Collectors.toList())); // output : [1, 2, 3, 4, 5, 6]
            // xx_Match
            System.out.println(list_stream.stream().allMatch(p->p < 6));//output: false
            System.out.println(list_stream.stream().anyMatch(p->p == 1));//output: true
            System.out.println(list_stream.stream().noneMatch(p->p == 0));//output: true

        }

        public static void testOptional(){
            // Stream 中的 findAny、max/min、reduce 等方法返回的均是 optional 值
            String str1 = "cyt";
            String str2=null;
            Optional.ofNullable(str1).ifPresent(System.out::println);
            Optional.ofNullable(str2).ifPresent(System.out::println);// optional 避免空指针异常
            Stream.of(1,2,3).reduce(Integer::sum).ifPresent(System.out::println);//output: 6
//            System.out.println(Stream.of("a","b","c","d").reduce("*",String::concat));
            System.out.println(Optional.ofNullable(str1).map(String::length).orElse(-1));//output: 3
            System.out.println(Optional.ofNullable(str2).map(String::length).orElse(-1));//output: -1
            ArrayList<Integer> list_stream = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,6,6));
            System.out.println(list_stream.stream().allMatch(p->p < 6));
            System.out.println(list_stream.stream().noneMatch(p->p == 0));//output: true
        }


}
