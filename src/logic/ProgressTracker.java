package logic;

import data.UserState;

import java.util.*;

/**
 * 模块说明：
 * ProgressTracker 是用户任务进度的核心管理类
 *
 * 特殊说明：
 * 所有进度数据都存放于 UserState.progressMap 中，ProgressTracker 只做逻辑计算和功能封装
 */


public class ProgressTracker {

    //1. 变量初始化：

        // 每道题目的最大得分（用于进度百分比计算）
        private static final int MAX_SCORE_PER_TASK;

        //先复习一些基础知识：
        //1.对比 static final public/private
            //static 控制变量属于类而非对象 所有对象共享一份数据
            //final 控制变量的值是否可以修改 只能赋值一次，变成常量
            //public/private 控制变量的访问权限 即是否允许其他类访问变量
        //2.
        // 可以把 static { ... } 这种静态初始化块看作是“静态变量的构造器”
        // 在类加载（JVM第一次用到这个类）时执行的一段初始化代码块，用于给静态变量赋值或执行一次性设置
            //JVM第一次用到这个类可以理解为两种情况：
            //	1.	首次访问类的静态变量或方法（比如ProgressTracker.getOverallProgress();）
            //	2.	首次创建类的实例（即 new）
            // 所谓一次性就是一次性，后面的方法里不会有对这些变量的修改

        // 静态初始化块
        static {
            // Step 1: 确保 UserState 类中的全局数据结构 progressMap 已经从磁盘文件中加载到了内存中
            UserState.load();
            // Step 2:
            MAX_SCORE_PER_TASK = GradingSystem.grade(1, false);
        }


    //2. 方法定义：
    // 先清晰一下progressMap模板
    // progressMap = {
    //  1: {"task1": 80, "task2": 100},
    //  2: {"task1": 90}
    //}

        // 1） 保存某个任务的得分：
        public static void saveProgress(int grade, String taskId, int score) {
            // 更新 UserState 中的进度 Map：会自动创建缺失的年级子表并写入得分
            UserState.updateProgress(grade, taskId, score);

            // 将内存中的全部进度持久化到 user_state.dat
            UserState.persist();
        }


        // 2） 获取某个年级的进度百分比
        public static int getGradeProgress(int grade) {
            // 从 UserState 中取出该年级的任务得分表，如果没有则拿到一个空但只读的Map
            Map<String, Integer> m = UserState.getProgressMap()
                    //	•	.getOrDefault————要么拿，要么取默认值
                    //	•	如果找得到这个年级，就拿出来；
                    //	•	找不到（说明你还没做这个年级的题），那么就取一个Collections.emptyMap()，即“空表”，防止报错！
                    // Collections.emptyMap()为java.util自带
                    .getOrDefault(grade, Collections.emptyMap());


            // 若无任务记录，直接返回 0
            if (m.isEmpty()) return 0;

            // 有任务记录，开始计算当前年级学生的总得分
            int sum = m.values()//取出当前Map 里所有的值，类型是：Collection<V>，（比如{ "task1": 80, "task2": 100 } → Collection）
                       .stream()//转为流式结构，方便一步步处理
                       .mapToInt(Integer::intValue)//把 Integer 转成原始的 int（性能优化）
                       .sum();//求和，计算当前年级学生的总得分

            //理论满分 = 该年级任务数 × 单任务最大分
            int max = m.size() * MAX_SCORE_PER_TASK;

            // 计算百分比并返回
            return max == 0 ? 0 : sum * 100 / max;
        }


        // 3） 获取所有年级的平均进度
        public static int getOverallProgress() {

            // 1. 先调用 getAllScores 获取全部得分表，为下文做铺垫
            List<Integer> scores = GradingSystem.getAllScores();
            if (scores.isEmpty()) return 0;//如果列表为空，返回 0
                //1） 计算总任务数
                int taskCount = scores.size();
                //2）计算满分总和
                int maxTotal = taskCount * MAX_SCORE_PER_TASK;

            //2. 然后调取总得分
            int totalScore = GradingSystem.getTotalScore();

            //3. 最后计算百分比并返回
            return maxTotal == 0 ? 0 : totalScore * 100 / maxTotal;
        }


        // 4） 以是否有得分记录判断某个任务是否已经完成
        public static boolean isTaskCompleted(int grade, String taskId) {
            //✅ 含义：
            //从 UserState 取年级表，检查是否包含该任务 ID
            return UserState.getProgressMap()
                    .getOrDefault(grade, Collections.emptyMap())
                    .containsKey(taskId);//检查是否包含该任务并返回布尔值
        }
}