package logic;

//TaskManager.java 作为全局控制单例，管理当前年级和任务切换逻辑，与 MainFrame 协作，实现页面导航和状态记录。
// src/logic/TaskManager.java

public class TaskManager {
    private static TaskManager instance;
    private MainFrame mainFrame;
    private int currentGrade;

    private TaskManager(MainFrame frame) {
        this.mainFrame = frame;
    }

    /** 单例获取 */
    public static TaskManager getInstance(MainFrame frame) {
        if (instance == null) {
            instance = new TaskManager(frame);
        }
        return instance;
    }

    /** 设置当前年级 */
    public void setGrade(int grade) {
        this.currentGrade = grade;
    }

    /** 启动指定任务 */
    public void startTask(String taskId) {
        // 记录在 UserState 中当前任务
        UserState.setCurrentTask(currentGrade, taskId);
        // 切换界面
        mainFrame.showPanel(taskId);
    }

    /** 结束当前任务并返回首页 */
    public void endTask() {
        // 保存进度已在各面板内部调用 ProgressTracker.saveProgress
        mainFrame.returnToHome();
    }

    /** 获取当前年级 */
    public int getGrade() {
        return currentGrade;
    }
}
