package logic;

import ui.MainFrame;
import data.UserState;

/**
 * TaskManager 作为全局控制单例，负责：
 * 1) 管理当前年级和任务切换逻辑
 * 2) 与 MainFrame 协作，实现页面导航
 * 3) 记录用户当前任务到 UserState
 */
public class TaskManager {
    // 单例实例
    private static TaskManager instance;

    // 主窗体引用，用于页面切换
    private MainFrame mainFrame;

    // 当前选中的年级
    private int currentGrade;

    /**
     * 私有构造，绑定 MainFrame
     * @param frame 主窗体实例
     */
    private TaskManager(MainFrame frame) {
        this.mainFrame = frame;
    }

    /**
     * 获取单例实例，如果尚未初始化则创建
     * @param frame 主窗体实例
     * @return TaskManager 单例
     */
    public static TaskManager getInstance(MainFrame frame) {
        if (instance == null) {
            instance = new TaskManager(frame);
        }
        return instance;
    }

    /**
     * 设置当前年级
     * @param grade 年级编号（1~4）
     */
    public void setGrade(int grade) {
        this.currentGrade = grade;
    }

    /**
     * 启动指定任务：
     * 1) 记录当前任务到 UserState
     * 2) 通知主窗体切换到对应面板
     *
     * @param taskId TaskConfig.getId() 中的任务标识
     */
    public void startTask(String taskId) {
        // 记录到用户状态
        UserState.setCurrentTask(currentGrade, taskId);
        // 切换到对应面板
        mainFrame.showPanel(taskId);
    }

    /**
     * 结束当前任务，会回到首页
     * UserState 和 ProgressTracker 等保存逻辑面板内部已完成
     */
    public void endTask() {
        mainFrame.returnToHome();
    }

    /**
     * 获取当前年级
     * @return 当前年级编号
     */
    public int getGrade() {
        return currentGrade;
    }
}