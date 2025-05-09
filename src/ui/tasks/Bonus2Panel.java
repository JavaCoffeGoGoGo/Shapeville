package ui.tasks;

import logic.GradingSystem;
import logic.ProgressTracker;
import ui.MainFrame;
import util.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bonus2Panel：扇形面积与弧长计算练习（8 题，高级模式评分）
 */
public class Bonus2Panel extends AbstractTaskPanel {

    private static final double[][] ANSWERS = {
            {9.87, 12.57},
            {368, 40.84},
            {378, 39.79},
            {465, 42.24},
            {10.69,6.11},
            {151, 37.7},
            {352, 58.64},
            {491, 65.45}
    };

    // 练习状态
    private enum Phase { CHOICE, QUESTION, REVEAL }
    private Phase phase = Phase.CHOICE;
    private Set<String> completedSectors;
    private String currentId;
    private int round, attemptsLeft, remainingSeconds, score;
    private Timer countdownTimer;

    // UI 组件
    private JPanel choicePanel, questionPanel, revealPanel;
    private JLabel timerLabel, feedbackLabel;
    private JTextField areaField, lengthField;

    public Bonus2Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);

        // 初始化状态
        completedSectors = new HashSet<>();
        score = 0;
        round = 0;

        // 修改 backButton 行为：返回到扇形选择，不保存进度
        for (ActionListener al : backButton.getActionListeners()) {
            backButton.removeActionListener(al);
        }
        backButton.addActionListener(e -> {
            if (countdownTimer != null && countdownTimer.isRunning()) {
                countdownTimer.stop();
            }
            initChoicePanel();
        });

        // 开始在“选择”界面
        initChoicePanel();
    }

    @Override
    protected String getTaskTitle() {
        if (phase == null) {
            // 在父类 initComponents 期间调用时，phase 还未初始化
            return "复合图形面积：请选择图形";
        }
        switch (phase) {
            case CHOICE:
                return "扇形练习：请选择扇形类型";
            case QUESTION:
                return String.format("扇形计算：请输入面积和弧长（第 %d / 8 题，π=3.14）", round);
            case REVEAL:
                return "揭示：扇形答案（π=3.14）";
            default:
                return "";
        }
    }

    @Override
    protected void startTask() {
        // 如果 completedShapes 还没初始化（构造尚未完成），就不要执行任何逻辑
        if (completedSectors == null) return;
        // 重新开始
        completedSectors.clear();
        score = 0;
        round = 0;
        initChoicePanel();
    }

    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;
        double aAns, lAns;
        try {
            aAns = Double.parseDouble(areaField.getText().trim());
            lAns = Double.parseDouble(lengthField.getText().trim());
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("请输入有效数字！");
            return;
        }

        attemptsLeft--;
        int attemptNum = 3 - attemptsLeft;
        double[] corr = ANSWERS[Integer.parseInt(currentId) - 1];

        if (Math.abs(aAns - corr[0]) < 0.01 && Math.abs(lAns - corr[1]) < 0.01) {
            int delta = GradingSystem.grade(attemptNum, /* isAdvanced= */ true);
            score += delta;
            feedbackLabel.setText("正确！本次得分：" + delta + "，累计：" + score);
            countdownTimer.stop();
            submitButton.setEnabled(false);
            new Timer(1000, ev -> {
                ((Timer)ev.getSource()).stop();
                finishSector();
            }).start();
        } else if (attemptsLeft > 0) {
            feedbackLabel.setText("错误！再试一次～");
        } else {
            // 三次用尽：揭示答案
            countdownTimer.stop();
            feedbackLabel.setText(String.format("正确答案：面积 %.2f，弧长 %.2f",
                    corr[0], corr[1]));
            submitButton.setEnabled(false);
            new Timer(1000, ev -> {
                ((Timer)ev.getSource()).stop();
                finishSector();
            }).start();
        }
    }

    @Override
    protected void saveAndFinish() {
        ProgressTracker.saveProgress(grade, taskId, score);
        SwingUtilities.invokeLater(() -> {
            StyleUtils.applyGlobalStyle(SwingUtilities.getWindowAncestor(this));
            JOptionPane.showMessageDialog(
                    this,
                    "任务完成！得分：" + score + " / " + (8 * 6),
                    "完成任务",
                    JOptionPane.INFORMATION_MESSAGE
            );
            mainFrame.showPanel("HOME");
        });
    }

    @Override
    protected String getEncouragement() {
        return "太棒了！";
    }

    /** Prompt 2: 选择界面 **/
    private void initChoicePanel() {
        phase = Phase.CHOICE;
        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();

        choicePanel = new JPanel(new GridLayout(2, 4, 10, 10));
        for (int i = 1; i <= 8; i++) {
            String id = String.valueOf(i);
            JButton btn = StyleUtils.createStyledButton("扇形" + id);
            btn.setEnabled(!completedSectors.contains(id));
            btn.addActionListener(e -> onSectorSelected(id));
            choicePanel.add(btn);
        }

        contentPanel.add(choicePanel);
        submitButton.setVisible(false);
        backButton.setVisible(false);
        homeButton.setVisible(true);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /** Prompt 3: 选中扇形后进入练习界面 **/
    private void onSectorSelected(String id) {
        currentId = id;
        round++;
        attemptsLeft = 3;
        remainingSeconds = 300;
        phase = Phase.QUESTION;
        showQuestionPanel();
        startTimer();
    }

    private void showQuestionPanel() {
        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();

        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        // 倒计时
        timerLabel = new JLabel("剩余时间：300s");
        styleCenter(timerLabel);
        questionPanel.add(timerLabel);

        // 问题图
        ImageIcon qIcon = new ImageIcon(
                getClass().getResource("/images/bonus2/bonusTwoQuestion" + currentId + ".png")
        );
        JLabel qPic = new JLabel(qIcon);
        qPic.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(qPic);

        // 输入区
        areaField = StyleUtils.createRoundedTextField(150, 32);
        areaField.setAlignmentX(Component.CENTER_ALIGNMENT);
        lengthField = StyleUtils.createRoundedTextField(150, 32);
        lengthField.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(new JLabel("面积：") {{ setFont(StyleUtils.DEFAULT_FONT); setAlignmentX(Component.CENTER_ALIGNMENT);}});
        questionPanel.add(areaField);
        questionPanel.add(Box.createVerticalStrut(5));
        questionPanel.add(new JLabel("弧长：") {{ setFont(StyleUtils.DEFAULT_FONT); setAlignmentX(Component.CENTER_ALIGNMENT);}});
        questionPanel.add(lengthField);

        // 反馈
        feedbackLabel = new JLabel(" ");
        styleCenter(feedbackLabel);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(feedbackLabel);

        // 添加到 contentPanel
        contentPanel.add(questionPanel);
        submitButton.setVisible(true);
        submitButton.setEnabled(true);
        backButton.setVisible(true);
        homeButton.setVisible(true);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /** Prompt 4: 倒计时 **/
    private void startTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            timerLabel.setText("剩余时间：" + remainingSeconds + "s");
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                feedbackLabel.setText("时间到！");
                submitButton.setEnabled(false);
                new Timer(1000, ev -> {
                    ((Timer)ev.getSource()).stop();
                    finishSector();
                }).start();
            }
        });
        countdownTimer.start();
    }

    /** Prompt 5: 完成一题，进入揭示 & 下一题 **/
    private void finishSector() {
        phase = Phase.REVEAL;
        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();

        // 揭示两张答案图
        revealPanel = new JPanel();
        revealPanel.setLayout(new BoxLayout(revealPanel, BoxLayout.Y_AXIS));

        // 面积图
        ImageIcon aIcon = new ImageIcon(
                getClass().getResource("/images/bonus2/areaOf" + currentId + ".png")
        );
        JLabel aPic = new JLabel(aIcon);
        aPic.setAlignmentX(Component.CENTER_ALIGNMENT);
        revealPanel.add(aPic);

        // 弧长图
        ImageIcon lIcon = new ImageIcon(
                getClass().getResource("/images/bonus2/lengthOf" + currentId + ".png")
        );
        JLabel lPic = new JLabel(lIcon);
        lPic.setAlignmentX(Component.CENTER_ALIGNMENT);
        revealPanel.add(Box.createVerticalStrut(10));
        revealPanel.add(lPic);

        contentPanel.add(revealPanel);
        submitButton.setVisible(false);
        backButton.setVisible(true);
        homeButton.setVisible(true);

        // 置灰
        completedSectors.add(currentId);
        contentPanel.revalidate();
        contentPanel.repaint();

        // 下一步：全部完成则结束，否则延迟返回选择
        if (completedSectors.size() == 8) {
            new Timer(1000, ev -> {
                ((Timer)ev.getSource()).stop();
                saveAndFinish();
            }).start();
        } else {
            new Timer(1000, ev -> {
                ((Timer)ev.getSource()).stop();
                initChoicePanel();
            }).start();
        }
    }

    /** 工具：居中样式 **/
    private void styleCenter(JComponent c) {
        c.setFont(StyleUtils.DEFAULT_FONT);
        c.setForeground(StyleUtils.TEXT_COLOR);
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}