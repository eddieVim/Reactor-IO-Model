# Reactor-IO-Model
==可伸缩性的IO==


- 设计目标
- 步骤
- 分而治之

- 事件驱动模型

- Reactor模型
    - 基本版
    - 多线程版本
    - 其他版本

## 目标

1. 能够实现优雅地降级，在负载增加的情况下（更多的连接）
2. 能够随着系统的资源的增加，持续地提高性能
3. 还能满足可用性和性能目标
    - 短延迟
    - 满足高峰期请求
    - 可调整的服务质量

==分而治之，通常都是实现可伸缩性目标的最好的方法。==



## 步骤

在一些web服务、分布式存储等一些分布式的项目中，他们都基于这样的一个模型：

- 读取
- 反序列化
- 服务的处理
- 序列化
- 写出

**但是每一步的资源开销都是不一样的，所以我们要寻求一个更好的IO模型，用于充分利用系统的资源。**



## 分而治之

1. 分割整个过程成一个一个小任务，每个任务都执行一个动作，不会被阻塞
2. 在任务被触发的时候，去执行它（IO事件一般都被作为一个整个流程的第一个触发器）
3. 非阻塞IO读写、调度与IO感知事件的任务
4. 无穷无尽的变化可能



## 传统IO模型

![Classic Service Designs](https://tva1.sinaimg.cn/large/008eGmZEly1gox4l8wxv1j316q0u0gtx.jpg)

传统的IO模型，将每一个步骤都耦合在一个handler中，显然这样是不能够更好地利用系统的资源的。



## 事件驱动模型

1. 更少的资源（不需要每个客户端起一个线程
2. 更少的线程上下文切换开销
3. 但是调度会更慢，大部分需要手动绑定事件



## Reactor模型

- Reactor：类似于响应IO事件，并将事件分发给对应的处理器。
- Handlers：非阻塞的处理线程。



### java.nio api

- **Channels**

    连接至文件、socket等，支持非阻塞读

- **Buffers** 

    一个数组对象，可以被channel直接读写

- **Selectors** 

    通知channel哪一些IO事件已经就绪了

- **SelectionKeys**

    用于维护IO事件状态和绑定



### Single threaded version

![Single threaded version](https://tva1.sinaimg.cn/large/008eGmZEly1gp0isioew9j31be0fan2g.jpg)

#### Code

> https://github.com/eddieVim/Reactor-IO-Model/blob/master/src/main/java/pro/eddievim/reactor/single

### Multi threaded version

![Multi threaded version](https://tva1.sinaimg.cn/large/008eGmZEly1gp0itds4usj317b0u0n65.jpg)

- 能够动态地增加线程，适用于多处理器情况

- Worker Threads

    Reactor应该快速地触发Handler，Reactor的触发速度应该随着Handler的处理速度而改变；

    将非IO操作分流给Work Threads。

- 多个Reactor线程

    Reactor线程池在处理IO的时候，可能发生饱和，使用多个Reactor线程以匹配CPU和IO的速率。



#### Work Threads

- 负载一些非IO的操作，来加速Reactor线程的处理速度。
- 比事件驱动模型有着简单的实现方式，且足够的性能消耗超过开销。



#### Coordinating Tasks

##### HandOffs

每个任务的触发速度最快，但是耦合度高，易受请求并发的影响。

##### Callbacks

对每个调度器的回调设置状态、附件，是中介模式的变体

##### Queues

通过缓冲区跨阶段传递

##### Futures

每个任务产生了一个结果，使用`wait/notify or join`来协调结果

#### Code

> 与单线程版本区别，在于`Handler`类中维护了一个静态的线程池（所有的`Handler`对象共享这个线程池）。
>
> https://github.com/eddieVim/Reactor-IO-Model/tree/master/src/main/java/pro/eddievim/reactor/pool

### Multiple Reactor Threads

![Multiple Reactor Threads](https://tva1.sinaimg.cn/large/008eGmZEly1gp0ivp086aj317g0u0qcf.jpg)

- 用于匹配CPU与IO的速率
- 每个`Reactor`都有自己的`Selector`、线程、调度器循环
- 有一个`Acceptor`线程用来调度任务给其他`Reactor`

#### Code

> 与之前的版本相比，`Reactor`数量由1个变为多个。
>
> https://github.com/eddieVim/Reactor-IO-Model/tree/master/src/main/java/pro/eddievim/reactor/multi



### 一些其他 的NIO特性

- 一个`Reactor`多个`Selector`
    - 为不同的IO事件绑定不同的处理器
    - 可能需要小心同步情况去协调
- 文件传输
- 内存映射文件
- 堆外（直接）内存
    - 可以实现零拷贝
    - 但是初始化和销毁开销较大
    - 最适合具有长时间连接的应用程序









