package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import util.DrawingAngleUtils;
import logic.ProgressTracker;
import logic.GradingSystem;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Task2Panel：角度类型识别任务面板
 */
public class Task2Panel extends AbstractTaskPanel {

    private List<Integer> angleList;
    private int currentAngle;
    private JPanel angleDrawPanel;
    private JTextField answerField;
    private JLabel feedbackLabel;

    public Task2Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
        backButton.setVisible(false);
    }

    @Override
    protected String getTaskTitle() {
        return "角度类型识别：请输入角度类型（共4轮）";
    }

    @Override
    protected void startTask() {
        // 初始化数据
        round = 0;
        score = 0;
        attemptsLeft = 3;

        // 只生成 10–350，不含 0、360
        angleList = IntStream.rangeClosed(1, 35)
                .map(i -> i * 10)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(angleList);
        angleList = angleList.subList(0, 4);
        Collections.shuffle(angleList);

        // 每次只创建一次组件
        answerField = StyleUtils.createRoundedTextField(200, 32);
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(StyleUtils.DEFAULT_FONT);
        feedbackLabel.setForeground(StyleUtils.TEXT_COLOR);

        initAngleDrawPanel();

        // 用 BoxLayout 做垂直布局
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(angleDrawPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // 用 FlowLayout 容器确保水平居中——**核心改动**！
        JPanel inputWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        inputWrapper.setOpaque(false);
        inputWrapper.add(answerField);
        contentPanel.add(inputWrapper);

        contentPanel.add(Box.createVerticalStrut(5));

        JPanel feedbackWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        feedbackWrapper.setOpaque(false);
        feedbackWrapper.add(feedbackLabel);
        contentPanel.add(feedbackWrapper);

        contentPanel.add(Box.createVerticalGlue());

        // 加载第一题
        loadNextAngle();
    }

    private void loadNextAngle() {
        if (round >= 4 || angleList.isEmpty()) {
            saveAndFinish();
            return;
        }

        currentAngle = angleList.remove(0);
        attemptsLeft = 3;
        round++;

        // 清空旧输入与反馈
        answerField.setText("");
        feedbackLabel.setText(" ");

        // 重绘角度图与面板
        angleDrawPanel.repaint();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;

        String text = answerField.getText().trim();
        if (text.isEmpty()) {
            feedbackLabel.setText("请输入角度类型！");
            refreshFeedback();
            return;
        }

        attemptsLeft--;
        int attemptNum = 3 - attemptsLeft;
        String correctType = getType(currentAngle);

        if (text.equalsIgnoreCase(correctType)) {
            // 回答正确
            score += GradingSystem.grade(attemptNum, false);
            feedbackLabel.setText("正确！干得漂亮～");
            refreshFeedback();

            submitButton.setEnabled(false);
            Timer t = new Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                submitButton.setEnabled(true);
                loadNextAngle();
            });
            t.setRepeats(false);
            t.start();

        } else if (attemptsLeft > 0) {
            // 还有机会
            feedbackLabel.setText("错误！再试一次～");
            refreshFeedback();

        } else {
            // 用尽机会
            feedbackLabel.setText("正确答案：" + correctType );
            refreshFeedback();

            submitButton.setEnabled(false);
            Timer t = new Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                submitButton.setEnabled(true);
                loadNextAngle();
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void refreshFeedback() {
        feedbackLabel.revalidate();
        feedbackLabel.repaint();
    }

    private String getType(int angle) {
        if (angle > 0 && angle < 90) {
            return "锐角";
        } else if (angle == 90) {
            return "直角";
        } else if (angle > 90 && angle < 180) {
            return "钝角";
        } else if (angle == 180) {
            return "平角";
        } else {
            return "反射角";
        }
    }

    @Override
    protected void saveAndFinish() {
        ProgressTracker.saveProgress(grade, taskId, score);
        SwingUtilities.invokeLater(() -> {
            StyleUtils.applyGlobalStyle(SwingUtilities.getWindowAncestor(this));
            JOptionPane.showMessageDialog(
                    this,
                    "任务完成！你的得分是：" + score + " / 12",
                    "完成任务",
                    JOptionPane.INFORMATION_MESSAGE
            );
            mainFrame.showPanel("HOME");
        });
    }

    @Override
    protected String getEncouragement() {
        return "干得漂亮！";
    }

    private void initAngleDrawPanel() {
        angleDrawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth(), h = getHeight();
                g2.translate((w - 200) / 2, (h - 200) / 2);
                DrawingAngleUtils.drawAngle(g2, currentAngle);
                g2.dispose();
            }
        };
        angleDrawPanel.setPreferredSize(new Dimension(200, 200));
        angleDrawPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}