package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

/**
 * 抽象基类，封装了所有任务面板的通用布局与控制按钮逻辑，
 * 子类只需实现启动任务、提交处理等核心逻辑。
 */
public abstract class AbstractTaskPanel extends JPanel {
    protected MainFrame mainFrame;
    protected int grade;
    protected String taskId;
    protected int attemptCount = 0;
    protected int score = 0;
    protected boolean taskFinished = false;

    protected JLabel instructionLabel;
    protected JPanel contentPanel;
    protected JButton submitButton;
    protected JButton endSessionButton;
    protected JButton homeButton;

    public AbstractTaskPanel(MainFrame mainFrame, int grade, String taskId) {
        this.mainFrame = mainFrame;
        this.grade = grade;
        this.taskId = taskId;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setOpaque(false);

        initComponents();
        layoutComponents();

        startTask(); // 子类实现
    }

    /** 初始化所有组件 */
    private void initComponents() {
        instructionLabel = StyleUtils.createInstructionLabel(getTaskTitle());

        submitButton = StyleUtils.createStyledButton("提交答案");
        submitButton.addActionListener(e -> onSubmit());

        endSessionButton = StyleUtils.createStyledButton("结束会话");
        endSessionButton.addActionListener(e -> endTaskSession());

        homeButton = StyleUtils.createStyledButton("🏠 返回首页");
        homeButton.addActionListener(e -> {
            ProgressTracker.saveProgress(grade, taskId, score);
            mainFrame.returnToHome();
        });

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    }

    /** 布局组件到主面板 */
    private void layoutComponents() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(instructionLabel, BorderLayout.WEST);
        top.add(homeButton, BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(endSessionButton);
        bottom.add(submitButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    /** 结束任务会话：由子类保存、弹窗提示并跳转首页 */
    protected void endTaskSession() {
        if (!taskFinished) {
            taskFinished = true;
            saveAndFinish(); // 由子类负责保存和反馈
        }
        mainFrame.returnToHome();
    }

    /** 根据得分给出鼓励语 */
    protected String getEncouragement(int score) {
        if (score >= 5) return "太棒了，继续保持！";
        if (score >= 3) return "不错哦，可以再练练！";
        return "没关系，重来一次就会更好～";
    }

    // ==== 抽象方法 ====

    /** 子类提供任务标题或说明 */
    protected abstract String getTaskTitle();

    /** 子类启动题目逻辑 */
    protected abstract void startTask();

    /** 子类响应“提交答案” */
    protected abstract void onSubmit();

    /** 子类保存分数、展示反馈弹窗等逻辑 */
    protected abstract void saveAndFinish();

    // ==== 公共访问器 ====

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }

    public JLabel getInstructionLabel() {
        return instructionLabel;
    }

    public int getScore() {
        return score;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getGrade() {
        return grade;
    }

    public boolean isTaskFinished() {
        return taskFinished;
    }

    // ==== 公共方法 ====

    /** 设置任务标题 */
    public void setTaskTitle(String title) {
        if (instructionLabel != null) {
            instructionLabel.setText(title);
        }
    }

    /** 重置任务相关的分数与状态 */
    public void resetScores() {
        attemptCount = 0;
        score = 0;
        taskFinished = false;
    }
}