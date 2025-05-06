package ui.tasks;

import util.DrawingAngleUtils;
import logic.ProgressTracker;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class Task2Panel extends AbstractTaskPanel {

    private static final int MAX_ATTEMPTS = 3;
    private static final int MAX_ROUNDS = 4;

    private int currentRound = 0;
    private int attempts = 0;
    private int currentAngle;

    private JTextField angleInputField;
    private JLabel feedbackLabel;
    private JPanel drawingPanel;

    public Task2Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "请判断下方角度的类型（锐角 / 直角 / 钝角 / 平角 / 反射角）";
    }

    @Override
    protected void startTask() {
        prepareDrawingPanel();
        nextRound();
    }

    @Override
    protected void onSubmit() {
        if (taskFinished) return;

        String userAnswer = angleInputField.getText().trim();
        String correctAnswer = getAngleType(currentAngle);

        if (userAnswer.equals(correctAnswer)) {
            score += 1;
            feedbackLabel.setText("🎉 恭喜你，答对了！当前得分：" + score);
            nextRound();
        } else {
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                feedbackLabel.setText("😢 错误次数用完，正确答案是：" + correctAnswer);
                nextRound();
            } else {
                feedbackLabel.setText("❌ 错误，还有 " + (MAX_ATTEMPTS - attempts) + " 次机会。");
            }
        }
    }

    @Override
    protected void saveAndFinish() {
        JOptionPane.showMessageDialog(this, "任务完成！你的总得分为：" + score + "\n" + getEncouragement(score));
        ProgressTracker.saveProgress(grade, taskId, score);
    }

    // ======= 自定义组件与逻辑部分 =======

    private void prepareDrawingPanel() {
        // 角度绘图面板
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                DrawingAngleUtils.drawAngle(g, currentAngle);
            }
        };
        drawingPanel.setPreferredSize(new Dimension(400, 400));

        // 输入区域 + 提交按钮
        JPanel inputPanel = new JPanel(new FlowLayout());
        angleInputField = new JTextField(10);
        inputPanel.add(new JLabel("你的答案："));
        inputPanel.add(angleInputField);

        // 反馈标签
        feedbackLabel = new JLabel("尝试判断角度类型");
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 加入到 contentPanel 中（AbstractTaskPanel 提供）
        contentPanel.add(drawingPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(feedbackLabel);
    }

    private void nextRound() {
        if (currentRound < MAX_ROUNDS) {
            currentRound++;
            attempts = 0;
            currentAngle = (int) (Math.random() * 36) * 10; // 0 ~ 350 间的十度倍数
            angleInputField.setText("");
            drawingPanel.repaint();
        } else {
            taskFinished = true;
            submitButton.setEnabled(false);
            feedbackLabel.setText("🎯 任务完成！你的总得分是：" + score);
            saveAndFinish();
        }
    }

    private String getAngleType(int angle) {
        if (angle < 90) return "锐角";
        else if (angle == 90) return "直角";
        else if (angle < 180) return "钝角";
        else if (angle == 180) return "平角";
        else return "反射角";
    }
}