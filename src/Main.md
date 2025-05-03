## 详解SwingUtilities.invokeLater(new Runnable(){}
### 为什么要用 SwingUtilities.invokeLater(...)？
#### 先复习一下swing图形界面库
##### 🧱 什么是 Swing？

Swing 是 Java 提供的一套 图形用户界面（GUI）开发工具包，是 AWT（Abstract Window Toolkit）的升级版，用来开发跨平台的桌面程序，比如：
* 按钮（JButton）
* 标签（JLabel）
* 入框（JTextField）
* 窗口（JFrame）
* 面板（JPanel）
* ...  
它都属于 Java 提供的 javax.swing 包。

##### 🧵 为什么 Swing 不是线程安全的？（先大致了解）

###### 先了解线程，我们打个比方：

* **程序**就像一家餐厅
* **线程**（Thread）就是餐厅里的服务员
* 每个线程可以同时做不同的事情，比如一个服务员在点餐，一个在上菜

###### 再了解什么叫做线程不安全？
比如两个线程同时操作一张桌子（就像都往桌子上放菜单）：

你希望先放“火锅”，再换成“烧烤”。但因为这俩线程可能同时动手，结果可能是：
* 火锅没写完就被烧烤覆盖
* 火锅烧烤混在一起
* 程序崩溃（内存状态出错）

这种情况就叫**线程不安全**。

###### 最后了解一下锁机制
“加锁”就像是服务员干活前挂个“暂停使用”的牌子，其他人不能碰这个桌子。

🚩Swing 的图形更新不是线程安全的，根本原因有两个：

1.  Swing 并没有为每个组件的状态设置锁机制
2.  Swing 组件之间的状态往往是“互相关联”的，树状结构，“牵一发而动全身”（了解即可）


#### 由此可知，Swing 本身不是线程安全的
为了安全：Swing 要求所有图形更新必须在“事件调度线程”中执行  

这个线程叫 EDT（Event Dispatch Thread），你可以理解成 Swing 的“专属画图师傅”。  
所有你要改界面的行为，都得跟这个师傅说一声：“请你帮我画。”  

Java 给我们提供了工具函数：  
SwingUtilities.invokeLater(Runnable task);  
这是一个“任务类”——告诉我 run() 方法里要干啥  
你把任务写成一个 Runnable，丢进去，系统会安排在 EDT 中排队执行。  
这样就不会出现多个线程抢着画图的问题。  


### 💡什么是 new Runnable() { ... }？
#### 先了解一下先多线程实现的一般思路
 
##### Java 的多线程，需要你提供一个“任务”，告诉线程要做什么。
这个“任务类”必须实现 Java 自带的接口（这和基础的接口知识可以对应）：
```java
public interface Runnable {
    public void run();
}
```
##### 那我们一般是怎么写的呢？

首先建立类并实现：
```java
class MyTask implements Runnable {
    public void run() {
        // 做事情
    }
}
```
然后新建一个实例并提供给相关接口：
```java
SwingUtilities.invokeLater(new MyTask());
```

#### 但是有时候我们觉得麻烦，不想多写一个类。

于是 Java 允许我们直接在用的时候写“这个类长啥样”,也就是
```java
SwingUtilities.invokeLater(new Runnable() {
    public void run() {
        // 修改 Swing 界面
    }
});
```
* 我现在就创建一个没有名字的类
* 这个类实现了 Runnable 接口
* 它里面实现了 run() 方法
* 我把它作为参数传进去，用完就扔，不会复用

### 总结：可以想象成
> “我手上有个任务是画界面，我不打算自己画（怕乱），
我要请专业人员 Swing 主线程画。 
于是我把‘该怎么画’写在纸上（Runnable.run()），
然后交给 Swing 的接待员（invokeLater()）安排进队列。”