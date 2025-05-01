package ui;

// src/ui/HomePanel.java

public class HomePanel extends JPanel {
    private MainFrame mainFrame; // 引用主框架以便切换页面

    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        StyleUtils.applyGlobalStyle(MainFrame.getFrame());

        // 顶部标题区
        JLabel title = StyleUtils.createTitleLabel("欢迎来到数学学习游戏！");
        add(title, BorderLayout.NORTH);

        // 中部核心内容区（等级说明 + 年级按钮）
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // ➤ 等级说明面板
        centerPanel.add(createLevelIntroPanel());

        // ➤ 年级选择面板
        centerPanel.add(createGradeSelectionPanel());

        add(centerPanel, BorderLayout.CENTER);

        // 底部按钮栏：进度 + Home + End Session
        add(createBottomControlPanel(), BorderLayout.SOUTH);
    }

    // 📘 等级介绍说明（Basic vs Advanced）
    private JPanel createLevelIntroPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3));
        panel.setOpaque(false);

        panel.add(new JLabel("尝试次数"));
        panel.add(new JLabel("Basic 得分"));
        panel.add(new JLabel("Advanced 得分"));

        panel.add(new JLabel("第一次"));
        panel.add(new JLabel("3 分"));
        panel.add(new JLabel("6 分"));

        panel.add(new JLabel("第二次"));
        panel.add(new JLabel("2 分"));
        panel.add(new JLabel("4 分"));

        panel.add(new JLabel("第三次"));
        panel.add(new JLabel("1 分"));
        panel.add(new JLabel("2 分"));

        return panel;
    }

    // 🎯 年级选择按钮（1~4年级）
    private JPanel createGradeSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setOpaque(false);
        JLabel label = new JLabel("请选择你的年级：", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);

        for (int i = 1; i <= 4; i++) {
            int grade = i;
            JButton gradeButton = StyleUtils.createStyledButton("年级 " + i);
            gradeButton.addActionListener(e -> mainFrame.setGradeAndSwitchToSelector(grade));
            panel.add(gradeButton);
        }

        return panel;
    }

    // ⏱ 底部控制区（进度条 + Home + End）
    private JPanel createBottomControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // ➤ 进度条
        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(ProgressTracker.getOverallProgress());
        progressBar.setStringPainted(true);
        progressBar.setForeground(StyleUtils.PRIMARY_COLOR);
        panel.add(progressBar, BorderLayout.CENTER);

        // ➤ 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton homeButton = StyleUtils.createStyledButton("🏡 回首页");
        homeButton.addActionListener(e -> mainFrame.returnToHome());

        JButton endButton = StyleUtils.createStyledButton("❌ 结束会话");
        endButton.addActionListener(e -> {
            int score = GradingSystem.getTotalScore();
            JOptionPane.showMessageDialog(this,
                    "本次总得分：" + score + "\n继续加油！",
                    "会话结束", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.resetToHome();
        });

        buttonPanel.add(homeButton);
        buttonPanel.add(endButton);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }
}
