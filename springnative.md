## 简介
Spring Native 是基于 Java 17 和 Spring Framework 6.01。此外，Spring Boot 3.2.0-SNAPSHOT 需要 Java 17，并且与 Java 20 兼容2。
最新的Spring Native版本是**0.12.2**¹。对应的Spring版本是**6.1.1**⁵，Java版本是**21**⁷。

Spring Native的工作原理主要基于GraalVM的Ahead-Of-Time (AOT)编译¹。以下是一些关键的工作原理：

1. **原生镜像**：原生镜像是一种将Java代码构建为独立可执行文件的技术¹。这个可执行文件包括应用程序类、依赖项的类、运行时库类以及JDK的静态链接的本地代码¹。JVM被打包到原生镜像中，因此目标系统无需任何Java运行环境¹。

2. **GraalVM和Native Image Builder**：GraalVM是一个为Java和其他JVM语言编写的高性能JDK分发版¹。它提供了一个Native Image builder，这是一个工具，用于从Java应用程序构建本地代码，并将其与VM一起打包到一个独立的可执行文件中¹。

3. **Ahead-Of-Time (AOT)编译**：AOT编译是将高级Java代码编译为本地可执行代码的过程¹。通常，这是由JVM的即时编译器（JIT）在运行时完成的，这允许在执行应用程序时进行观察和优化¹。在AOT编译之前，可以选择进行一个称为AOT处理的单独步骤，即从代码中收集元数据并将它们提供给AOT编译器¹。

4. **Spring Ahead-of-Time处理**：典型的Spring Boot应用程序相当动态，配置在运行时进行²。实际上，Spring Boot自动配置的概念在很大程度上依赖于对运行时状态的反应，以便正确地配置事物²。然而，如果使用Spring Boot创建原生镜像，将假定一个封闭的世界，并限制应用程序的动态方面²。
<!--
源: 与必应的对话， 2023/12/3
(1) Native Images with Spring Boot and GraalVM | Baeldung. https://www.baeldung.com/spring-native-intro.
(2) GraalVM Native Image Support - Spring | Home. https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html.
(3) Native Support in Spring Boot 3.0.0-M5. https://spring.io/blog/2022/09/26/native-support-in-spring-boot-3-0-0-m5/.
-->

## Spring Native的优势
## Spring Native的劣势
Spring Native虽然带来了许多优势，但也有一些限制和权衡，包括¹²：

1. **构建时间较长**：相比于JVM，构建本地映像的过程更为繁重，需要更长的时间¹。
2. **运行时优化不足**：预热后的运行时优化相比于传统的Java运行方式要少²。
3. **静态分析**：在构建时会从主入口点，静态分析应用程序¹²。
4. **移除未使用的代码**：在构建时会移除未使用的代码¹²。
5. **配置反射、动态代理等**：对于反射，需要用户在编译期，通过配置文件或编译器参数的形式，明确告知编译器程序代码中哪些方法只通过反射来访问的²。
6. **类路径在构建时确定**：classpath在构建时就已经确定¹²。
7. **没有类延迟加载**：可执行文件中所有的内容都会在启动时加载到内存中¹²。
8. **在构建时就运行了一些代码**：在构建时就运行了一些代码¹²。
9. **一些Java切面类的特性未得到完全支持**：一些Java切面类的特性未得到完全支持¹。

这些限制可能会影响到Spring Native在某些场景下的适用性，因此在选择使用Spring Native之前，需要充分考虑这些因素。

<!--
(1) Spring Native 中文文档 - 简书. https://www.jianshu.com/p/011ae582a621.
(2) SpringNative：把Spring项目编译成原生程序 - 知乎. https://zhuanlan.zhihu.com/p/360726385.
(3) 走向 Native 化：Spring&Dubbo AOT 技术示例与原理讲解. https://cn.dubbo.apache.org/zh-cn/blog/2023/06/28/%E8%B5%B0%E5%90%91-native-%E5%8C%96springdubbo-aot-%E6%8A%80%E6%9C%AF%E7%A4%BA%E4%BE%8B%E4%B8%8E%E5%8E%9F%E7%90%86%E8%AE%B2%E8%A7%A3/.
(4) Spring-native 实战 - 知乎. https://zhuanlan.zhihu.com/p/432141194.
(5) undefined. https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/.
(6) undefined. https://github.com/spring-guides/gs-rest-service.
(7) undefined. https://repo.spring.io/release.
-->
Spring Native的一些劣势主要体现在以下几个方面¹：

1. **不支持Java代理，JMX，JVMTI，Java Flight Recorder**：这些功能在管理、测试和控制JVM应用程序时非常有用。但由于原生镜像不在JVM容器中运行，所以这些功能无法使用¹。

2. **反射需要额外的配置**：许多框架广泛使用反射，因此这些框架需要进行额外的配置和工作才能支持原生镜像。这就是为什么Spring创建了Spring Native项目¹。

3. **无法使用转储**：您将无法使用线程和堆转储。可以使用Linux内核功能获取一些关于线程的信息¹。

以下是一个简单的代码示例，展示了Spring Native在处理反射时可能遇到的问题：

```java
import java.lang.reflect.Method;

public class ReflectionExample {
    public static void main(String[] args) {
        try {
            Class<?> c = Class.forName("java.lang.String");
            Method m = c.getMethod("length");
            Integer length = (Integer) m.invoke("Hello, World!");
            System.out.println("Length: " + length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

在上述代码中，我们使用反射来调用String类的`length`方法。然而，由于Spring Native在编译时需要知道所有的类和方法，因此这种在运行时动态加载类和方法的做法可能会导致问题。为了解决这个问题，我们需要在Spring Native的配置中明确指定我们要反射的类和方法，这会增加配置的复杂性。

<!--
(1) Spring Native: What, Why and How? | by Wout Schoovaerts - Medium. https://schoovaertswout.medium.com/spring-native-what-why-and-how-d79f5beb626b.
(2) Native Images with Spring Boot and GraalVM | Baeldung. https://www.baeldung.com/spring-native-intro.
(3) What are the disadvantages of using spring boot in production?. https://stackoverflow.com/questions/54017348/what-are-the-disadvantages-of-using-spring-boot-in-production.
--
