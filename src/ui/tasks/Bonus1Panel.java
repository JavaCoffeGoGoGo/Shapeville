package ui.tasks;

import logic.GradingSystem;
import logic.ProgressTracker;
import ui.MainFrame;
import util.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Bonus1Panel：复合图形面积计算（9 图，3 次尝试，高级模式评分）
 */
public class Bonus1Panel extends AbstractTaskPanel {

    // —— 阶段枚举 & 默认初始化，避免 null 引用 ——
    private enum Phase { CHOICE, QUESTION, REVEAL }
    private Phase phase = Phase.CHOICE;

    private static final double[] ANSWERS = {
            229, 331, 598, 288, 12, 159.5, 151, 3456, 174
    };

    private Set<String> completedShapes;
    private String currentShape;
    private int attemptsLeft;
    private int round;
    private int score;
    private Timer countdownTimer;
    private int remainingSeconds;

    // UI 组件
    private JPanel choicePanel, questionPanel, answerPanel;
    private JLabel timerLabel, feedbackLabel;
    private JTextField answerField;
    private JButton returnToChoiceButton;

    public Bonus1Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
        completedShapes = new HashSet<>();
        score = 0;
        round = 0;
        initChoicePanel();
    }

    @Override
    protected String getTaskTitle() {
        if (phase == null) {
            // 在父类 initComponents 期间调用时，phase 还未初始化
            return "复合图形面积：请选择图形";
        }
        return switch (phase) {
            case CHOICE   -> "复合图形面积：请选择图形";
            case QUESTION -> "复合图形面积：计算 — 图形 " + currentShape;
            case REVEAL   -> "揭示：复合图形面积答案 — 图形 " + currentShape;
        };
    }

    @Override
    protected void startTask() {
        // 如果 completedShapes 还没初始化（构造尚未完成），就不要执行任何逻辑
        if (completedShapes == null) return;

        // 以下为原有逻辑
        completedShapes.clear();
        score = 0;
        round = 0;
        initChoicePanel();
    }

    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;
        String txt = answerField.getText().trim();
        double userAns;
        try {
            userAns = Double.parseDouble(txt);
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("请输入有效数字！");
            return;
        }

        attemptsLeft--;
        int attemptNum = 3 - attemptsLeft;
        double correct = ANSWERS[Integer.parseInt(currentShape) - 1];

        if (Math.abs(userAns - correct) < 0.01) {
            int delta = GradingSystem.grade(attemptNum, /* 高级模式 */ true);
            score += delta;
            feedbackLabel.setText("正确！得分：" + delta);
            countdownTimer.stop();
            submitButton.setEnabled(false);
            new Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                finishShape();
            }).start();
        } else if (attemptsLeft > 0) {
            feedbackLabel.setText("错误！再试一次～");
        } else {
            countdownTimer.stop();
            feedbackLabel.setText("正确答案：" + correct);
            submitButton.setEnabled(false);
            new Timer(500, e -> {
                ((Timer)e.getSource()).stop();
                finishShape();
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
                    "任务完成！得分：" + score + " / " + (9 * 6),
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

    // ========== Prompt 2：选择面板 ==========
    private void initChoicePanel() {
        phase = Phase.CHOICE;
        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();

        choicePanel = new JPanel(new GridLayout(3, 3, 10, 10));
        for (int i = 1; i <= 9; i++) {
            String id = String.valueOf(i);
            JButton btn = StyleUtils.createStyledButton("图形" + id);
            btn.setEnabled(!completedShapes.contains(id));
            btn.addActionListener(e -> onShapeSelected(id));
            choicePanel.add(btn);
        }

        contentPanel.add(choicePanel);
        submitButton.setVisible(false);
        backButton.setVisible(false);
        homeButton.setVisible(true);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ========== Prompt 3：显示问题 & 启动定时器 ==========
    private void onShapeSelected(String id) {
        currentShape = id;
        round++;
        attemptsLeft = 3;
        remainingSeconds = 300;

        showQuestionPanel();
        startTimer();
    }

    private void showQuestionPanel() {
        phase = Phase.QUESTION;
        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();

        // “返回选择”按钮
        if (returnToChoiceButton != null) bottom.remove(returnToChoiceButton);
        returnToChoiceButton = StyleUtils.createStyledButton("返回选择");
        returnToChoiceButton.addActionListener(e -> initChoicePanel());
        bottom.add(returnToChoiceButton, 1);

        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        timerLabel = new JLabel("剩余时间：300s");
        timerLabel.setFont(StyleUtils.DEFAULT_FONT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionPanel.add(timerLabel);

        JLabel pic = new JLabel(new ImageIcon(
                getClass().getResource("/images/bonus1/bonusOneQuestion" + currentShape + ".png")
        ));
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(pic);

        answerField = StyleUtils.createRoundedTextField(200, 32);
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(answerField);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(StyleUtils.DEFAULT_FONT);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionPanel.add(Box.createVerticalStrut(5));
        questionPanel.add(feedbackLabel);

        contentPanel.add(questionPanel);
        submitButton.setVisible(true);
        submitButton.setEnabled(true);
        backButton.setVisible(true);
        homeButton.setVisible(true);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

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
                new Timer(500, ev -> {
                    ((Timer)ev.getSource()).stop();
                    finishShape();
                }).start();
            }
        });
        countdownTimer.start();
    }

    // ========== Prompt 5：完成一题 ==========
    private void finishShape() {
        phase = Phase.REVEAL;
        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();

        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));

        JLabel pic = new JLabel(new ImageIcon(
                getClass().getResource("/images/bonus1/answerOf" + currentShape + ".png")
        ));
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerPanel.add(pic);

        double correct = ANSWERS[Integer.parseInt(currentShape) - 1];
        JLabel txt = new JLabel("正确答案：" + correct);
        txt.setFont(StyleUtils.DEFAULT_FONT);
        txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerPanel.add(Box.createVerticalStrut(10));
        answerPanel.add(txt);

        contentPanel.add(answerPanel);
        submitButton.setVisible(false);
        backButton.setVisible(true);
        homeButton.setVisible(true);

        // 置灰
        completedShapes.add(currentShape);

        contentPanel.revalidate();
        contentPanel.repaint();

        // 下一步：完成全部或返回选择
        if (completedShapes.size() == 9) {
            new Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                saveAndFinish();
            }).start();
        } else {
            new Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                initChoicePanel();
            }).start();
        }
    }
}