package logic;

import ui.MainFrame;
import ui.tasks.*;
import javax.swing.*;
import java.util.Map;//Java 自带的集合接口 Map
import java.util.Set;//Java 自带的集合接口 Set
import java.util.function.BiFunction;//引入 Java 8 的函数式接口 BiFunction（接收两个参数并返回一个结果的函数）

/**
 * 模块说明：
 * TaskRegistry 是一个任务注册表，用于根据 taskId（如 “g1_shape”） 动态生成对应的任务面板（JPanel）
 */
public class TaskRegistry {

//1. 变量声明部分：

    //先补充一些基础知识：
        // 1. static：属于类本身，不依赖于某个对象实例；
            // 就是说：不需要 new TaskRegistry()，整个程序里所有人就可以访问它。
            // 例如，以下面那个方法为例，我们可以直接：TaskRegistry.getAllTaskIds()。

        // 2. final：表示“这张 Map 表”创建好之后，它的引用地址就不能变了。
            // 引用类型（reference types）+ final——用 final 后，这个引用地址不能再变
            // 但地址指向的那个对象（ArrayList）的内容能不能变，取决于对象本身是不是可变的

        // 3. Map<String, BiFunction<...>>：这是变量的 类型
            //    registry：这是变量的 名字
            //    Map.of(...)：是这个变量的 初始值——因此不可变

        //4.  "g1_shape", (frame, grade) -> new Task1Panel(frame, grade)
            // 是在 Map.of(...) 里 按照 Map<String, BiFunction<MainFrame, Integer, JPanel>> 的格式来写的“键值对”。

        // 5. Map集合，也就是一个字典，用于之后输入一个任务编号，找到对应创建方法（BiFunction）并创建面板：
            //	Key（键）是 String 类型的任务 ID；
            //  Value（值）是一个 BiFunction<MainFrame, Integer, JPanel>，表示一个函数

        // 6. 一个 BiFunction<MainFrame, Integer, JPanel>，表示一个函数
            //    •	  接受两个参数：MainFrame frame 和 Integer grade，
            //    •	  返回值是一个 JPanel（任务面板）。

    // 然后是变量声明：这是一个 Map集合，也就是一个字典
    // 用于之后输入一个任务编号，找到对应创建方法（BiFunction）并创建面板
    private static final Map<String, BiFunction<MainFrame, Integer, JPanel>> registry = Map.of(
            // 这几个参数与BiFunction里的一一对应
            "g12_shape", (frame, grade) -> new Task1Panel(frame, grade, "g12_shape")
//            "g12_angle", (frame, grade) -> new Task2Panel(frame, grade, "g12_angle"),
//            "g34_area_basic", (frame, grade) -> new Task3Panel(frame, grade, "g34_area_basic"),
//            "g34_circle", (frame, grade) -> new Task4Panel(frame, grade, "g34_circle"),
//            "g34_area_complex", (frame, grade) -> new Bonus1Panel(frame, grade, "g34_area_complex"),
//            "g34_src", (frame, grade) -> new Bonus2Panel(frame, grade, "g34_src")
    );


//2. 方法实现

    // 1） 获取所有已注册的任务 ID
    public static Set<String> getAllTaskIds() {
        return registry.keySet();
    }

    // 2） 按 taskId 实例化对应任务面板
    public static JPanel createTaskPanel(String taskId, MainFrame frame, int grade) {

        // 在注册表里查找这个任务 ID 对应的值
        // 比如输入"g1_shape"，
        // 查找到(frame, grade) -> new Task1Panel(frame, grade)
        // 并返回对应的BiFunction<frame, grade, Task1Panel>给名为actor的BiFunction
        BiFunction<MainFrame, Integer, JPanel> actor = registry.get(taskId);

        if (actor != null) {
            //名为actor的BiFunction采用接口类自带的apply方法
            //效果相当于new Task1Panel(frame, grade);
            //并直接返回
            return actor.apply(frame, grade);
        } else {
            // 未注册的任务，显示警告信息，避免程序崩溃
            JPanel panel = new JPanel();
            panel.add(new JLabel("未找到任务: " + taskId));
            return panel;
        }
    }
}