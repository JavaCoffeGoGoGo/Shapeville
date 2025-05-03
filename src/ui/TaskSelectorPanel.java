package ui;

import data.TaskConfig;
import data.TaskRepository;
import util.StyleUtils;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * TaskSelectorPanel 是“任务选择界面”。
 * 用户选择当前年级下的一个具体任务，进入对应的练习流程。
 */
public class TaskSelectorPanel extends JPanel {
    private MainFrame mainFrame;   // 主框架引用，用于页面跳转
    private int currentGrade;      // 当前选择的年级编号（1~4）

    /**
     * 构造方法，初始化任务选择面板
     * @param mainFrame 主窗口框架，用于跳转
     * @param grade     当前年级编号
     */
    public TaskSelectorPanel(MainFrame mainFrame, int grade) {
        this.mainFrame = mainFrame;
        this.currentGrade = grade;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setOpaque(false);

        // ---------- 顶部标题 ----------
        JLabel title = StyleUtils.createTitleLabel("年级 " + grade + " - 请选择一个任务开始");
        add(title, BorderLayout.NORTH);

        // ---------- 中部任务按钮区域 ----------
        JPanel taskPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 自动行数，2列排列
        taskPanel.setOpaque(false);

        List<TaskConfig> tasks = TaskRepository.getTasksByGrade(grade);

        if (tasks.isEmpty()) {
            JLabel noTaskLabel = new JLabel("暂无可用任务", SwingConstants.CENTER);
            noTaskLabel.setFont(new Font("Arial", Font.ITALIC, 18));
            taskPanel.setLayout(new BorderLayout());
            taskPanel.add(noTaskLabel, BorderLayout.CENTER);
        } else {
            for (TaskConfig task : tasks) {
                JButton taskButton = StyleUtils.createStyledButton(task.getTaskName());

                // 若任务已完成则标记✅且不可点击
                if (ProgressTracker.isTaskCompleted(grade, task.getId())) {
                    taskButton.setEnabled(false);
                    taskButton.setText(task.getTaskName() + " ✅");
                }

                taskButton.addActionListener(e -> mainFrame.startTask(grade, task));
                taskPanel.add(taskButton);
            }
        }

        add(taskPanel, BorderLayout.CENTER);

        // ---------- 底部控制区域 ----------
        add(createBottomControls(), BorderLayout.SOUTH);
    }

    /**
     * 创建底部控制区域：年级进度条 + 返回按钮
     */
    private JPanel createBottomControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // 年级完成度进度条
        int progress = ProgressTracker.getGradeProgress(currentGrade);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setString("当前年级完成度：" + progress + "%");
        progressBar.setStringPainted(true);
        progressBar.setForeground(StyleUtils.PRIMARY_COLOR);
        panel.add(progressBar, BorderLayout.CENTER);

        // 返回按钮（靠右对齐）
        JButton backButton = StyleUtils.createStyledButton("⬅ 回到年级选择");
        backButton.addActionListener(e -> mainFrame.returnToHome());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(backButton);

        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }
}