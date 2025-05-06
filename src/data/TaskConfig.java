package data;

/**
 * 模块说明：
 * TaskConfig 是任务的元信息封装类，用于描述每个任务的唯一标识（id）与对外显示名称（taskName）。
 */
public class TaskConfig {

    // 1.变量初始化
        // 任务的唯一标识（程序内部使用）
        private final String id;
        // 任务名称（用于界面展示等）
        private final String taskName;

    // 2.构造函数
    public TaskConfig(String id, String taskName) {
        this.id = id;
        this.taskName = taskName;
    }

    // 3.方法区：
    public String getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }
}