package ui.tasks;

// src/ui/tasks/Task3Panel.java

public class Task3Panel extends AbstractTaskPanel {
    private JLabel shapeImageLabel;
    private JTextField answerField;

    private int currentIndex = 0;
    private final String[] shapeTypes = {"正方形", "长方形", "三角形", "圆形"};
    private final String[] shapeImagePaths = {
            "area/square.png", "area/rectangle.png", "area/triangle.png", "area/circle.png"
    };

    private final double[] correctAreas = {
            4 * 4,          // 正方形边长4
            5 * 3,          // 长方形5x3
            0.5 * 6 * 2,    // 三角形底6高2
            Math.PI * 2 * 2 // 圆形半径2
    };

    public Task3Panel(MainFrame mainFrame, int grade) {
        super(mainFrame, grade, "Task3");
    }

    @Override
    protected String getTaskTitle() {
        return "面积计算任务";
    }

    @Override
    protected void startTask() {
        contentPanel.removeAll();

        shapeImageLabel = new JLabel();
        shapeImageLabel.setIcon(ImageUtils.loadIcon(shapeImagePaths[currentIndex], 200, 200));

        answerField = new JTextField(10);
        answerField.setFont(new Font("Arial", Font.PLAIN, 24));

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("请输入图形面积（保留1位小数）："));
        inputPanel.add(answerField);

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(shapeImageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(inputPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void onSubmit() {
        attemptCount++;
        String input = answerField.getText().trim();

        try {
            double userArea = Double.parseDouble(input);
            double expected = Math.round(correctAreas[currentIndex] * 10) / 10.0;

            if (Math.abs(userArea - expected) < 0.1) {
                score++;
                JOptionPane.showMessageDialog(this, "正确！得分 +1");
            } else {
                JOptionPane.showMessageDialog(this, "错误！正确答案是：" + expected);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效数字！");
        }

        currentIndex++;
        if (currentIndex >= shapeTypes.length) {
            endTaskSession();
        } else {
            answerField.setText("");
            shapeImageLabel.setIcon(ImageUtils.loadIcon(shapeImagePaths[currentIndex], 200, 200));
        }
    }
}
