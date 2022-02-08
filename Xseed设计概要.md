## 前言
XSeed是一个提供业务行为埋点的日志框架，初版实现用户行为埋点、埋点信息存储、业务分类等功能。采用切面编程思想，将埋点行为和业务代码解耦，使用简单。  
使用了字节码插桩技术，将存储埋点信息等代码，通过字节码插入class文件，做到无痕埋点。

## 使用方法

我们采用注解的形式去标记埋点方法，描述埋点业务行为，XSeed会自动找到这些方法，当他们被调用时自动记录埋点信息。

```java

@XSeedFunction(functionDesc = "XXX用户登录",tag = "login", isSeedParam = true)
fun login(userAccount:String){

}

```
对我们需要埋点的业务方法，仅需一行注解便可实现无痕埋点。

- functionDesc：对方法行为的描述
- tag:XSeed会根据tag对信息进行分类
- isSeedParam：是否将描述方法的参数信息也记录下来


## 设计思路

XSeed分为两个模块，XSeed-Plugin和XSeed-utils，其中XSeed-Plugin模块是字节码插桩的实现模块，XSeed-utils模块是插入方法的实现模块。
目前该框架会生成两个记录文件：method_log.txt（业务行为埋点记录文件）、 crash_log.txt（App崩溃信息记录文件）
### XSeed-Plugin

XSeed-Plugin的字节码插桩实现步骤：

- 自定义Gradle插件，实现TransForm接口
- 拿到class文件，并通过注解找到目标方法
- 解析注解信息
- 通过ASM插入埋点字节码

此外为了提高编译效率，采用增量编译和并发编译方案，提高编译速度,并且为了获取用户调用实参信息，通过预加载的形式,将相应的实参在插入点动态写入文件，并且在预加载时将需要扫描的class文件路径保存，从而节省编译时间。  
此模块将单独打包为Gradle Plugin。  

### XSeed-utils

此模块实现埋点功能，主要实现埋点信息存储。  
初版暂时实现埋点行为分类、存储到本地的功能，后期实现埋点信息上传。

考虑到文件IO操作的性能，参考OKHttp使用队列和线程池去处理存储任务。
