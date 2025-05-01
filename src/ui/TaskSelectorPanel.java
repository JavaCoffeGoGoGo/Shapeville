package ui;

//✳️ 支持类说明（简要）
//        •	TaskConfig：封装每个任务的标识（如 id, taskName）。
//        •	TaskRepository.getTasksByGrade(int grade)：返回该年级的任务列表（可能读取 JSON 或硬编码）。
//        •	ProgressTracker.isTaskCompleted(grade, taskId)：判断某个任务是否已完成。
//        •	mainFrame.startTask(grade, task)：触发进入具体任务模块。
// src/ui/TaskSelectorPanel.java

public class TaskSelectorPanel extends JPanel {
    private MainFrame mainFrame;
    private int currentGrade;

    public TaskSelectorPanel(MainFrame mainFrame, int grade) {
        this.mainFrame = mainFrame;
        this.currentGrade = grade;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setOpaque(false);

        // 顶部标题
        JLabel title = StyleUtils.createTitleLabel("年级 " + grade + " - 请选择一个任务开始");
        add(title, BorderLayout.NORTH);

        // 中部任务选择按钮区域
        JPanel taskPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        taskPanel.setOpaque(false);

        List<TaskConfig> tasks = TaskRepository.getTasksByGrade(grade);
        for (TaskConfig task : tasks) {
            JButton taskButton = StyleUtils.createStyledButton(task.getTaskName());
            if (ProgressTracker.isTaskCompleted(grade, task.getId())) {
                taskButton.setEnabled(false);
                taskButton.setText(task.getTaskName() + " ✅");
            }
            taskButton.addActionListener(e -> {
                mainFrame.startTask(grade, task);
            });
            taskPanel.add(taskButton);
        }

        add(taskPanel, BorderLayout.CENTER);

        // 底部：返回首页、进度条
        add(createBottomControls(), BorderLayout.SOUTH);
    }

    private JPanel createBottomControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // ➤ 进度条
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(ProgressTracker.getGradeProgress(currentGrade));
        progressBar.setString("当前年级完成度：" + progressBar.getValue() + "%");
        progressBar.setStringPainted(true);
        progressBar.setForeground(StyleUtils.ACCENT_COLOR);
        panel.add(progressBar, BorderLayout.CENTER);

        // ➤ 返回按钮
        JButton backButton = StyleUtils.createStyledButton("⬅ 回到年级选择");
        backButton.addActionListener(e -> mainFrame.returnToHome());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(backButton);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }
}