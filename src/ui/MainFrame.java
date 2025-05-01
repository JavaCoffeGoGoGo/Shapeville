package ui;

import logic.TaskRegistry;
import util.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    CardLayout cardLayout;                  // 用于切换多个面板
    JPanel mainPanel;                       // 中央容器
    HomePanel homePanel;                    // 首页面板
    TaskSelectorPanel taskSelectorPanel;    // 年级任务选择器
    Map<String, JPanel> taskPanels;         // 各个任务面板按名字管理

    public MainFrame() {
        // 初始化窗口
        setTitle("MathLearningGame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // 居中显示
        StyleUtils.applyGlobalStyle(this); // 应用全局样式

        // 初始化主面板与布局
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 加载各个页面
        homePanel = new HomePanel(this);
        taskSelectorPanel = new TaskSelectorPanel(this);

        // 添加面板到 CardLayout
        mainPanel.add(homePanel, "HOME");
        mainPanel.add(taskSelectorPanel, "SELECTOR");

        // 动态加载任务面板
        taskPanels = new HashMap<>();
        for (String taskName : TaskRegistry.getAllTaskNames()) {
            JPanel taskPanel = TaskRegistry.createTaskPanel(taskName, this);
            taskPanels.put(taskName, taskPanel);
            mainPanel.add(taskPanel, taskName);
        }

        // 添加主面板
        add(mainPanel);
        showPanel("HOME");
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }
}
