package data;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 模块说明：
 * UserState 是一个用户状态管理类
 */
public class UserState {

    //1. 初始化
        // 1） 年级 -> (任务 ID -> 进度值)，用于记录每个年级每个任务的用户进度
        private static Map<Integer, Map<String, Integer>> progressMap = new HashMap<>();

        // 2） 本地磁盘文件名，用于保存和恢复 progressMap 的内容（使用序列化格式，简单持久化实现）
        // 后缀 .dat 是随便取的
        private static final String STATE_FILE = "user_state.dat";




    //2. 实现并使用的方法

        // 1） 更新学生完成某个任务的进度
        public static void updateProgress(int grade, String taskId, int value) {

            // map.computeIfAbsent(g -> new HashMap<>());操作对象为Map中的一个键值对，
            // 意思是“如果这个 key（比如 2 年级）还没有值（value），
            // 那我把grade赋给g, 并执行后面的g -> new HashMap<>() 表达式，得到一个新value并放进去，生成一个新的小表
            progressMap.computeIfAbsent(grade, g -> new HashMap<>())
                    .put(taskId, value);
        }


        // 2） 保存所有进度到本地文件
        public static void persist() {
            // try-with-resources 会在读取结束或发生异常时自动关闭流
            // try部分 会将 progressMap 转换成 字节流（byte stream） 并写入文件 "user_state.dat"
                // 文件是平台无关但语言相关的，即 只能被 Java 程序解码，即只能被 Java 的 ObjectInputStream 反序列化读取
                // 而且后缀也是随便取的
            //具体实现逻辑如下：


            // ObjectOutputStream(new FileOutputStream(STATE_FILE))是Java设计好了的这一套 IO 类之间的“嵌套关系”
            // 它能把以下两个流程连起来：
                // ObjectOutputStream负责将progressMap写成字节流（即序列化）
                // FileOutputStream采用“匿名临时对象”，负责将字节流写入文件
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STATE_FILE))) {
                // 调用 ObjectOutputStream 类里定义的writeObject方法
                // 真正 把刚才提到的两个流程连起来，把整个嵌套 Map 序列化保存
                out.writeObject(progressMap);


                // JVM 会在 try 块中某行代码 throw 出异常对象时，进入 catch(...) 中匹配的类型，以本段代码为例：
                    // • 如果是 IOException，就会进入 catch(IOException e)，e 是匹配上的那个异常对象（已由代码 throw 出）
                    // • JVM 不会“自动 new 一个 IOException”，而是用“已经被 throw 出的那个现成的异常对象”；
                    // • JVM 会把这个异常对象赋值给 catch 中声明的变量 e；
                    // • 然后执行 catch 块中的语句。
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // 3） 加载历史进度
        public static void load() {
            // 1. 先检查文件是否存在，若不存在，说明程序第一次运行

            File file = new File(STATE_FILE);
            if (!file.exists()) {
                progressMap = new HashMap<>();// 初始化一个新的空 progressMap
                return;// 跳过读取过程。
            }

            // 2. 存在则开始读取恢复
                //  FileInputStream + ObjectInputStream 是 Java IO 中常见的“嵌套使用”方式：
                    // • FileInputStream 打开文件 user_state.dat，提供字节输入流
                    // • ObjectInputStream 负责从字节流中读取 Java 对象（即反序列化）
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {


                    // 调用 readObject() 从文件中读出一个对象
                        // 这个对象其实是我们之前 persist() 方法里写进去的 progressMap
                        // 现在读回来的是一个通用类型 Object，需要后续再判断具体是什么并强制转换
                    Object obj = in.readObject();

                    // 为了保险，先检查读出来的对象是不是我们想要的类型 Map
                    // 避免读错文件或被篡改的文件造成 ClassCastException 崩溃
                    if (obj instanceof Map) {
                        // 如果类型没问题，就把它强转成我们用的嵌套 Map 类型，并赋值给 progressMap
                        // 符合“从大转小”就必须强转原则
                        progressMap = (Map<Integer, Map<String, Integer>>) obj;
                    }

                    // 可能发生的异常处理：
                        // IOException：文件读失败（可能是损坏、权限问题等）
                        // ClassNotFoundException：反序列化时找不到原来的类（类结构变更或删除）
                        // JVM 会把捕获到的异常对象自动传给 e（自定义的变量名），进入 catch 块执行逻辑
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    progressMap = new HashMap<>();  // 也就是干脆就重建一个空的 Map，保证程序能正常运行下去
                }
        }


        //4） 返回当前内存中的进度表（年级 →（任务Id → 分数））
        public static Map<Integer, Map<String, Integer>> getProgressMap() {
            // 为了防止外部误改，最好返回一个不可修改的视图：
            return Collections.unmodifiableMap(progressMap);
        }


}