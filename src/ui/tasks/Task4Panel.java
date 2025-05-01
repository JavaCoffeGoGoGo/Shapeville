package ui.tasks;

// src/ui/tasks/Task4Panel.java

public class Task4Panel extends AbstractTaskPanel {
    private JLabel comboImageLabel;
    private JComboBox<String> shapeTypeComboBox;

    private int currentIndex = 0;
    private final String[] comboTypes = {"正方形 + 三角形", "长方形 + 圆形", "圆形 + 三角形"};
    private final String[] comboImagePaths = {
            "combo/square_triangle.png",
            "combo/rectangle_circle.png",
            "combo/circle_triangle.png"
    };

    public Task4Panel(MainFrame mainFrame, int grade) {
        super(mainFrame, grade, "Task4");
    }

    @Override
    protected String getTaskTitle() {
        return "组合图形识别任务";
    }

    @Override
    protected void startTask() {
        contentPanel.removeAll();

        comboImageLabel = new JLabel();
        comboImageLabel.setIcon(ImageUtils.loadIcon(comboImagePaths[currentIndex], 200, 200));

        shapeTypeComboBox = new JComboBox<>(new String[]{
                "正方形 + 三角形", "长方形 + 圆形", "圆形 + 三角形"
        });
        shapeTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("请选择图形组合类型："));
        inputPanel.add(shapeTypeComboBox);

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(comboImageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(inputPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void onSubmit() {
        attemptCount++;
        String selected = (String) shapeTypeComboBox.getSelectedItem();

        if (selected.equals(comboTypes[currentIndex])) {
            score++;
            JOptionPane.showMessageDialog(this, "正确！得分 +1");
        } else {
            JOptionPane.showMessageDialog(this, "错误！正确答案是：" + comboTypes[currentIndex]);
        }

        currentIndex++;
        if (currentIndex >= comboTypes.length) {
            endTaskSession();
        } else {
            comboImageLabel.setIcon(ImageUtils.loadIcon(comboImagePaths[currentIndex], 200, 200));
            shapeTypeComboBox.setSelectedIndex(0);
        }
    }
}