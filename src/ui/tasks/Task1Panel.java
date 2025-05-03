package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import util.DrawingUtils;
import logic.GradingSystem;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

/**
 * 图形识别任务面板：展示图形、接受输入、判分并记录进度。
 * 使用 DrawingUtils 动态绘制图形。
 */
public class Task1Panel extends AbstractTaskPanel {
    private ShapeDrawingPanel drawingPanel;
    private JTextField answerField;

    // 图形名称与类型
    private final String[] shapeNames = {"正方形", "长方形", "圆形", "三角形"};
    private final String[] shapeTypes = {"square", "rectangle", "circle", "triangle"};

    private int currentIndex = 0;

    public Task1Panel(MainFrame mainFrame, int grade) {
        super(mainFrame, grade, "g1_shape");
        initUI();
    }

    private void initUI() {
        setTaskTitle(getTaskTitle());
        getSubmitButton().setText("提交答案");
        getSubmitButton().addActionListener(e -> onSubmit());
    }

    @Override
    protected String getTaskTitle() {
        return "图形识别任务";
    }

    @Override
    protected void startTask() {
        currentIndex = 0;
        resetScores();

        JPanel content = getContentPanel();
        content.removeAll();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(Box.createVerticalStrut(20));

        // 图形绘制面板
        drawingPanel = new ShapeDrawingPanel(shapeTypes[currentIndex]);
        drawingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(drawingPanel);
        content.add(Box.createVerticalStrut(20));

        // 输入区域
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(StyleUtils.createTitleLabel("请输入图形名称："));
        answerField = new JTextField(10);
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inputPanel.add(answerField);
        content.add(inputPanel);

        content.revalidate();
        content.repaint();
    }

    @Override
    protected void onSubmit() {
        attemptCount++;
        String userAns = answerField.getText().trim();
        boolean correct = userAns.equalsIgnoreCase(shapeNames[currentIndex]);
        if (correct) {
            score++;
            JOptionPane.showMessageDialog(this, StyleUtils.createFeedbackLabel("回答正确！", true));
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    StyleUtils.createFeedbackLabel("回答错误！正确答案：" + shapeNames[currentIndex], false)
            );
        }

        currentIndex++;
        if (currentIndex >= shapeNames.length) {
            endTaskSession(); // 自动调用 saveAndFinish
        } else {
            drawingPanel.setShapeType(shapeTypes[currentIndex]);
            answerField.setText("");
        }
    }

    @Override
    protected void saveAndFinish() {
        int total = shapeNames.length;
        boolean isAdv = false;
        int points = GradingSystem.grade(score, total, isAdv);

        ProgressTracker.saveProgress(getGrade(), getTaskId(), points);
        JOptionPane.showMessageDialog(
                this,
                "本次得分：" + points + "\n您已完成图形识别任务！",
                "任务完成",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** 内部类：绘图组件 */
    private static class ShapeDrawingPanel extends JPanel {
        private String type;

        public ShapeDrawingPanel(String type) {
            this.type = type;
            setPreferredSize(new Dimension(200, 200));
            setOpaque(false);
        }

        public void setShapeType(String type) {
            this.type = type;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            DrawingUtils.drawShape((Graphics2D) g, type, 100, 100);
        }
    }
}