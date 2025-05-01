package ui.tasks;

// src/ui/tasks/AbstractTaskPanel.java

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
        startTask(); // ⬅️ 每个子类具体实现的流程入口
    }

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

    protected void endTaskSession() {
        taskFinished = true;
        ProgressTracker.saveProgress(grade, taskId, score);
        JOptionPane.showMessageDialog(this,
                "本次得分：" + score + " 分\n" + getEncouragement(score),
                "任务结束",
                JOptionPane.INFORMATION_MESSAGE
        );
        mainFrame.returnToHome();
    }

    protected String getEncouragement(int score) {
        if (score >= 5) return "太棒了，继续保持！";
        if (score >= 3) return "不错哦，可以再练练！";
        return "没关系，重来一次就会更好～";
    }

    // 🧠 各子类必须实现的方法
    protected abstract String getTaskTitle();  // 返回任务标题文本
    protected abstract void startTask();       // 初始化任务内容
    protected abstract void onSubmit();        // 用户点击“提交”时的处理逻辑
}
