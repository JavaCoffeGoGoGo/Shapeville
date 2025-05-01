package ui.tasks;

// src/ui/tasks/Task2Panel.java
//🛠️ 依赖工具类建议：
//        •	ImageUtils.loadIcon(String path, int w, int h)：统一图片缩放加载。
//        •	StyleUtils.createStyledButton(String text)：统一按钮样式。
//        •	ProgressTracker.saveProgress(...)：记录成绩或上传。
//        •	图片资源路径：位于 resources/images/shapes/ 和 resources/images/angles/

public class Task2Panel extends AbstractTaskPanel {
    private JLabel angleImageLabel;
    private JComboBox<String> angleTypeComboBox;

    private int currentIndex = 0;
    private final String[] angleTypes = {"锐角", "直角", "钝角"};
    private final String[] angleImagePaths = {
            "angles/acute.png", "angles/right.png", "angles/obtuse.png"
    };

    public Task2Panel(MainFrame mainFrame, int grade) {
        super(mainFrame, grade, "Task2");
    }

    @Override
    protected String getTaskTitle() {
        return "角度分类任务";
    }

    @Override
    protected void startTask() {
        contentPanel.removeAll();

        angleImageLabel = new JLabel();
        angleImageLabel.setIcon(ImageUtils.loadIcon(angleImagePaths[currentIndex], 200, 200));

        angleTypeComboBox = new JComboBox<>(angleTypes);
        angleTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("请选择角度类型："));
        inputPanel.add(angleTypeComboBox);

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(angleImageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(inputPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void onSubmit() {
        attemptCount++;
        String selectedType = (String) angleTypeComboBox.getSelectedItem();

        if (selectedType.equals(angleTypes[currentIndex])) {
            score++;
            JOptionPane.showMessageDialog(this, "回答正确！得分 +1");
        } else {
            JOptionPane.showMessageDialog(this, "回答错误！正确答案是：" + angleTypes[currentIndex]);
        }

        currentIndex++;
        if (currentIndex >= angleTypes.length) {
            endTaskSession();
        } else {
            // 下一题
            angleImageLabel.setIcon(ImageUtils.loadIcon(angleImagePaths[currentIndex], 200, 200));
            angleTypeComboBox.setSelectedIndex(0);
        }
    }
}