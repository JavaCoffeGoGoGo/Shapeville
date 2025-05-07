package ui;

import data.TaskConfig;
import data.TaskRepository;
import util.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 💡模块说明：任务选择界面面板（TaskSelectorPanel）
 */
public class TaskSelectorPanel extends JPanel {

    //  主窗口引用（用于跳转界面）
    private MainFrame mainFrame;
    //  当前任务区间的起始年级与终止年级（如 1~2，3~4）
    private int startGrade;
    private int endGrade;

    /**
     *  构造方法：初始化任务选择面板（支持年级区间）
     */
    public TaskSelectorPanel(MainFrame mainFrame, int startGrade, int endGrade) {
        this.mainFrame = mainFrame;
        this.startGrade = startGrade;
        this.endGrade = endGrade;

        // ============= 1. 整体布局与外观设定 =============
        setLayout(new BorderLayout(20,20)); // 主面板为 BorderLayout
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // 设置内边距
        setOpaque(false); // 背景透明，继承父面板样式

        // ============= 2. 逐个部分组件设定 =============

        // ============= 1）顶部：标题 =============
        String titleText = "年级 " + startGrade + "-" + endGrade + " - 请选择一个任务开始";
        JLabel title = StyleUtils.createTitleLabel(titleText);
        this.add(title, BorderLayout.NORTH);

        // ============= 2）中部：任务按钮区域 =============
        JPanel taskPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 自动行数、2列排列
        taskPanel.setOpaque(false);

        // 年级1-2任务一致、3-4任务一致，取 startGrade 的任务列表
        List<TaskConfig> tasks = TaskRepository.getTasksByGrade(startGrade);
        for (TaskConfig task : tasks) {
            // 创建任务按钮
            JButton taskButton = StyleUtils.createStyledButton(task.getTaskName());

            // 点击后以 startGrade 作为“年级”启动任务
            taskButton.addActionListener(e ->
                    mainFrame.enterTask(startGrade, task)
            );

            // 添加到任务面板
            taskPanel.add(taskButton);
        }

        // 🎯 将任务面板加入主面板中部
        this.add(taskPanel, BorderLayout.CENTER);

        // ============= 3）底部：进度条 + 返回按钮 =============
        this.add(createBottomControls(), BorderLayout.SOUTH);
    }

    /**
     * 创建底部控制区域（返回按钮）
     */
    private JPanel createBottomControls() {
        //1. 底部容器设定
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);


        //2. 返回按钮设定
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton backButton = StyleUtils.createStyledButton("Home");
        backButton.addActionListener(e -> mainFrame.returnToHome());
        btnPanel.add(backButton);

        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }
}