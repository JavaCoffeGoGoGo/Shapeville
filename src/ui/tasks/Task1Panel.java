package ui.tasks;

// src/ui/tasks/Task1Panel.java
//🛠️ 依赖工具类建议：
//        •	ImageUtils.loadIcon(String path, int w, int h)：统一图片缩放加载。
//        •	StyleUtils.createStyledButton(String text)：统一按钮样式。
//        •	ProgressTracker.saveProgress(...)：记录成绩或上传。
//        •	图片资源路径：位于 resources/images/shapes/ 和 resources/images/angles/

public class Task1Panel extends AbstractTaskPanel {
    private JLabel shapeImageLabel;
    private JTextField answerField;

    private String currentShapeName;
    private int currentIndex = 0;
    private final String[] shapeNames = {"正方形", "长方形", "圆形", "三角形"};
    private final String[] shapeImagePaths = {
            "shapes/square.png", "shapes/rectangle.png", "shapes/circle.png", "shapes/triangle.png"
    };

    public Task1Panel(MainFrame mainFrame, int grade) {
        super(mainFrame, grade, "Task1");
    }

    @Override
    protected String getTaskTitle() {
        return "图形识别任务";
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
        inputPanel.add(new JLabel("请输入图形名称："));
        inputPanel.add(answerField);

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(shapeImageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(inputPanel);
        contentPanel.revalidate();
        contentPanel.repaint();

        currentShapeName = shapeNames[currentIndex];
    }

    @Override
    protected void onSubmit() {
        attemptCount++;
        String userInput = answerField.getText().trim();

        if (userInput.equalsIgnoreCase(currentShapeName)) {
            score++;
            JOptionPane.showMessageDialog(this, "回答正确！得分 +1");
        } else {
            JOptionPane.showMessageDialog(this, "回答错误！正确答案是：" + currentShapeName);
        }

        currentIndex++;
        if (currentIndex >= shapeNames.length) {
            endTaskSession();
        } else {
            // 下一题
            answerField.setText("");
            shapeImageLabel.setIcon(ImageUtils.loadIcon(shapeImagePaths[currentIndex], 200, 200));
            currentShapeName = shapeNames[currentIndex];
        }
    }
}
