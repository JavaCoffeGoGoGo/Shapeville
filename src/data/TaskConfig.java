package data;

/**
 * 封装每个任务的配置信息，如 ID 和显示名称。
 */
public class TaskConfig {
    private final String id;
    private final String taskName;

    public TaskConfig(String id, String taskName) {
        this.id = id;
        this.taskName = taskName;
    }

    public String getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }
}