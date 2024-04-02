# Spring Boot 3.0 升级

## 版本选择
据springboot3要求所述，我们锁定了Java版本最低要求：Java 17+

同样的，Java版本也需要是[GA版本](https://openjdk.org/projects/jdk/)，并且为了获取稳定的JDK供应商支持，需要选择[LTS版本](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)(8,11,17,21).

目前可选的是17和21，有新用新的原则，选择了21.

## 版本迁移  Java版本升级 Java8 -> Java21
虽然版本选择时，最终确定了Java版本。Java版本升级带来的语法不兼容问题，应该首要解决。

### 迁移分析工具
可以借助该工具进行迁移分析，非必须

 https://docs.oracle.com/en-us/iaas/jms/doc/java-migration-analysis.html
 https://juejin.cn/post/7326393655392190501
 
### lombok 升级
https://projectlombok.org/setup/maven

这个错误是由于 Java 9+ 模块化系统的引入而导致的模块访问限制的问题。具体来说，lombok.javac.apt.LombokProcessor类试图访问 com.sun.tools.javac.processing.JavacProcessingEnvironment类，但是由于jdk.compiler模块没有导出com.sun.tools.javac.processing给未命名的模块，因此访问被拒绝。

### JAVA_OPT 新增必要项
出现以下报错：
`
nested exception is java.lang.reflect.InaccessibleObjectException: Unable to make field private final byte[] java.lang.String.value accessible: module java.base does not "opens java.lang" to unnamed module @433d61fb
`

需要在JAVA_OPT 添加以下内容
`--add-opens java.base/java.lang=ALL-UNNAMED`
注意：两个地方需要添加：IDEA 和 Dockerfile
![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/2fcdf66a-8b35-4fe5-9d0e-8224ded2d582)

问题原因: Java 9开始引入的模块化。https://www.cnblogs.com/IcanFixIt/p/7144366.html

### JAVA_OPT 移除过时项
 1. `-XX:+UseCGroupMemoryLimitForHeap`：这个选项在Java 10中被弃用，并在Java 12中被移除。取而代之的是`-XX:+UseContainerSupport`，这个选项在Java 10及以后的版本中默认启用。
 2. `-XX:+UseConcMarkSweepGC`：这个选项在Java 9中被标记为弃用，并在Java 14中被移除。取而代之的是G1垃圾收集器（通过`-XX:+UseG1GC`启用）或者Z垃圾收集器（通过`-XX:+UseZGC`启用）。

### IDEA maven、 jvm options JRE、Language Leve、Module SDK 选择Java21
![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/47011736-d3c3-4955-894f-c912c338d573)

![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/33f22003-60d5-4aca-b90b-a0d99f2bf3a5)

![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/5cd95859-35f6-433f-8beb-8b945967f5c9)

![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/33dd8a45-79cf-41c9-8031-7b8c6a230647)

### pom.xml，maven setting
pom编译配置
![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/745d5a8f-6e15-47da-8c3f-c017f8ccc88d)

插件版本升级
![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/db17c19d-d503-4627-aa5f-f02f8ce648a5)

maven setting (该项会被项目配置覆盖，可以不改，是全局默认值)
![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/63a4719f-c988-4a36-b641-8e4d8303d6f7)

> setting中注意https://developer.aliyun.com/mvn/guide 阿里云仓库更新

### dockerfile
1、升级基础镜像版本
2、上述JAVA_OPTS更新

![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/df5f01fc-ad8e-47d7-9b19-2fa277030ee1)

### 清理过时的内容，适配新内容
![image](https://github.com/open-irdc/SpringBoot3-And-SpringCloud3-In-Action/assets/22070982/e342d6e5-ffa8-4750-be61-6fe322a51602)

应该使用try(resourse) {} cache (e){} 的方式
