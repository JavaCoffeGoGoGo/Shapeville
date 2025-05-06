package data;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块说明：
 * TaskRepository 是任务配置的集中仓库类
 */
public class TaskRepository {

    //上来就是方法区：
    //根据年级编号获取对应任务列表
    public static List<TaskConfig> getTasksByGrade(int grade) {
        // 知识补充：
            // 实现的功能和List<String> names = new ArrayList<>();没啥区别，但是更灵活
            // 至于怎么灵活，大致是存储逻辑可以更改，具体还需要进一步分析
            // 而且这里ArrayList 本身早就用了 implements List，我们不需要再声明一次
            // 只需要直接创建对象/实例并使用即可
        List<TaskConfig> tasks = new ArrayList<>();

        switch (grade) {
            case 1:
                //  一二年级任务
                tasks.add(new TaskConfig("g12_shape", "图形识别"));
                tasks.add(new TaskConfig("g12_angle", "角度分类"));
                break;
            case 3:
                // 三四年级任务
                tasks.add(new TaskConfig("g34_area_basic", "基本面积计算"));
                tasks.add(new TaskConfig("g34_circle", "圆面积/周长计算"));
                tasks.add(new TaskConfig("g34_area_complex", "进阶任务：复合图形面积计算"));
                tasks.add(new TaskConfig("g34_src", "进阶任务：扇形面积/周长计算"));
                break;
            default:
                // 非法年级：提供默认占位任务
                tasks.add(new TaskConfig("default_task", "暂无任务"));
        }

        return tasks;
    }
}