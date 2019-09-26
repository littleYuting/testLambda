import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class testMethodReference {
    public static void main(String[] args){
        /**
         * 方法引用：语法糖，支持 Lambda 简写
         * 实现抽象方法的参数列表，必须与方法引用方法的参数列表保持一致，返回值不做要求
         */
//        MethodReference.testMain();
        testSimpleCase();

    }
    public static void testSimpleCase(){
        // 以下案例使用 java 内置函数式接口
        // lambda express : Consumer<String> consumer = str -> System.out.println(str);
        Consumer<String> consumer = System.out::println;
        consumer.accept("I love China");
        // 类名 + 静态方法
        // lambda express : Function<Long, Long> f = x -> Math.abs(x);
        Function<Long, Long> f = x -> Math.abs(x);
        System.out.println(f.apply(-5L));
        // 类名 + 实例方法(参数列表中的某个参数是实例方法的调用者)
        // lambda express : BiPredicate<String, String> b = (x,y) -> x.equals(y);
        BiPredicate<String, String> b = String::equals;
        System.out.println(b.test("abc","abd"));
        // 引用构造器
        // lambda express : Function<Integer, StringBuffer> fun = n -> new StringBuffer(n);
        Function<Integer, StringBuffer> fun = StringBuffer::new;
        System.out.println(fun.apply(109));
        // 引用数组
        // lambda express : Function<Integer, int[]> fun1 = n -> new int[n];
        Function<Integer, int[]> fun1 =int[]::new;
        System.out.println(fun.apply(109));
    }

    static class MethodReference{
        public static void testMain(){
            // 引用构造函数
            PersonFactory factory = new PersonFactory(Person::new);
            ArrayList<Person> persons = new ArrayList<>();
            Person p1 = factory.getPerson();
            p1.setName("cyt");
            Person p2 = factory.getPerson();
            p2.setName("lrx");
            Person p3 = factory.getPerson();
            p3.setName("hkp");
            persons.add(p1);
            persons.add(p2);
            persons.add(p3);
            Person[] personArr1 = persons.toArray(new Person[persons.size()]);
            System.out.println("before sort:");
            printArray(personArr1);
            Arrays.sort(personArr1, MethodReference::myCompare);
            System.out.println("静态方法 after sort:");
            printArray(personArr1);

            Person[] personArr2 = persons.toArray(new Person[persons.size()]);
            Arrays.sort(personArr2, p1::compare);
            System.out.println("引用特定对象的实例方法 after sort:");
            printArray(personArr2);

            Person[] personArr3= persons.toArray(new Person[persons.size()]);
            Arrays.sort(personArr3, Person::CompareTo);
            System.out.println("引用特定类型的任意对象的实例方法 after sort:");
            printArray(personArr3);
        }
        public static void printArray(Person[] persons){
            Arrays.stream(persons).forEach(System.out::println);
        }

        public static int myCompare(Person p1, Person p2){
            return p1.getName().compareTo(p2.getName());
        }
    }
}
class Person{
    private String name;
    public Person(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compare(Person p1, Person p2){
        return p1.getName().compareTo(p2.getName());
    }

    public int CompareTo(Person p){
        return this.getName().compareTo(p.getName());
    }
    @Override
    public String toString(){
        return "this Person'name : " + this.getName();
    }
}
class PersonFactory{
    private Supplier<Person> supplier;
    public PersonFactory(Supplier<Person> supplier){
        this.supplier = supplier;
    }

    public Person getPerson() {
        return supplier.get();
    }
}