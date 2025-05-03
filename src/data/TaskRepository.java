package data;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务仓库类，根据年级返回对应的任务列表。
 */
public class TaskRepository {

    /**
     * 根据年级返回该年级可用任务（此处为硬编码版本）。
     */
    public static List<TaskConfig> getTasksByGrade(int grade) {
        List<TaskConfig> tasks = new ArrayList<>();

        switch (grade) {
            case 1:
                tasks.add(new TaskConfig("g1_shape", "图形识别"));
                tasks.add(new TaskConfig("g1_area", "面积计算"));
                break;
            case 2:
                tasks.add(new TaskConfig("g2_angle", "角度分类"));
                tasks.add(new TaskConfig("g2_composite", "复合图形"));
                break;
            case 3:
                tasks.add(new TaskConfig("g3_sector", "扇形求解"));
                tasks.add(new TaskConfig("g3_compare", "图形比较"));
                break;
            case 4:
                tasks.add(new TaskConfig("g4_advanced", "进阶综合题"));
                tasks.add(new TaskConfig("g4_mixed", "混合练习"));
                break;
            default:
                tasks.add(new TaskConfig("default_task", "暂无任务"));
        }

        return tasks;
    }
}