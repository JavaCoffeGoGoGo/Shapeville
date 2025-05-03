package ui;

import util.StyleUtils;
import logic.GradingSystem;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

/**
 * HomePanel 是应用的主界面面板。
 * 包含标题、等级说明、年级选择按钮、进度条和控制按钮等组件。
 */
public class HomePanel extends JPanel {
    private MainFrame mainFrame; // 用于页面跳转与状态传递

    // ---------- 构造方法 ----------
    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout()); // 使用边界布局：上中下结构

        // 应用全局样式（背景色、字体等）
        StyleUtils.applyGlobalStyle(this);

        // ---------- 顶部标题 ----------
        JLabel title = StyleUtils.createTitleLabel("欢迎来到数学学习游戏！");
        add(title, BorderLayout.NORTH);

        // ---------- 中部内容（等级说明 + 年级选择） ----------
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false); // 背景透明以适配全局样式或背景图
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // 添加内边距
        centerPanel.add(createLevelIntroPanel(), BorderLayout.NORTH); // 上方等级说明
        centerPanel.add(createGradeSelectionPanel(), BorderLayout.CENTER); // 下方年级选择
        add(centerPanel, BorderLayout.CENTER);

        // ---------- 底部进度条与按钮 ----------
        add(createBottomControlPanel(), BorderLayout.SOUTH);
    }

    // ---------- 等级说明区 ----------
    private JPanel createLevelIntroPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 5, 5)); // 4 行 3 列
        panel.setOpaque(false); // 背景透明

        Font labelFont = new Font("Arial", Font.PLAIN, 16); // 统一字体风格

        // 定义表格内容：标题 + 三次尝试的得分对照
        String[][] labels = {
                {"尝试次数", "Basic 得分", "Advanced 得分"},
                {"第一次", "3 分", "6 分"},
                {"第二次", "2 分", "4 分"},
                {"第三次", "1 分", "2 分"}
        };

        // 逐行逐列添加标签
        for (String[] row : labels) {
            for (String text : row) {
                JLabel label = new JLabel(text, SwingConstants.CENTER); // 居中显示
                label.setFont(labelFont);
                panel.add(label);
            }
        }

        return panel;
    }

    // ---------- 年级选择区 ----------
    private JPanel createGradeSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // 顶部提示文字
        JLabel label = new JLabel("请选择你的年级：", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        // 按钮区：2 行 2 列
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonGrid.setOpaque(false);

        // 创建 4 个年级按钮，绑定事件
        for (int i = 1; i <= 4; i++) {
            int grade = i; // Lambda 需使用 effectively final 变量
            JButton gradeButton = StyleUtils.createStyledButton("年级 " + i);
            gradeButton.addActionListener(e -> mainFrame.setGradeAndSwitchToSelector(grade));
            buttonGrid.add(gradeButton);
        }

        panel.add(buttonGrid, BorderLayout.CENTER);
        return panel;
    }

    // ---------- 底部进度条与控制按钮 ----------
    private JPanel createBottomControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // 中间进度条
        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(ProgressTracker.getOverallProgress()); // 从逻辑层读取当前总进度
        progressBar.setStringPainted(true); // 显示百分比文字
        progressBar.setForeground(StyleUtils.PRIMARY_COLOR);
        panel.add(progressBar, BorderLayout.CENTER);

        // 右侧控制按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        // 回首页按钮：回到 HomePanel 本身
        JButton homeButton = StyleUtils.createStyledButton("🏡 回首页");
        homeButton.addActionListener(e -> mainFrame.returnToHome());

        // 结束按钮：显示得分 + 重置状态
        JButton endButton = StyleUtils.createStyledButton("❌ 结束会话");
        endButton.addActionListener(e -> {
            int score = GradingSystem.getTotalScore(); // 逻辑层计算总得分
            JOptionPane.showMessageDialog(this,
                    "本次总得分：" + score + "\n继续加油！",
                    "会话结束", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.resetToHome(); // 清空进度并回首页
        });

        // 添加按钮到控制区
        buttonPanel.add(homeButton);
        buttonPanel.add(endButton);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }
}