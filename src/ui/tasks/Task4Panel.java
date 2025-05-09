package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;
import logic.GradingSystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

/**
 * Task4Panel：圆的面积与周长计算（直径→半径交替练习）
 */
public class Task4Panel extends AbstractTaskPanel {
    private static final double PI = 3.14;

    private enum Phase {
        CHOICE,
        DIAM_AREA, REVEAL_AREA_DIAM,
        RAD_AREA,   REVEAL_AREA_RAD,
        DIAM_CIRC, REVEAL_CIRC_DIAM,
        RAD_CIRC,   REVEAL_CIRC_RAD
    }
    private Phase phase = Phase.CHOICE;

    private boolean areaDone = false, circDone = false;
    private double currentValue;
    private int round = 0, attemptsLeft = 3, score = 0;

    private Timer countdownTimer;
    private int remainingSeconds;

    // UI 组件
    private JPanel choicePanel, circlePanel;
    private JLabel timerLabel, valueLabel, feedbackLabel;
    private JTextField answerField;
    private JButton returnToChoiceButton;

    public Task4Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
        backButton.setVisible(false);
        initChoicePanel();
    }

    @Override
    protected String getTaskTitle() {
        if (phase == null) {
            phase = Phase.CHOICE;
        }
        switch (phase) {
            case CHOICE:
                return "圆计算：请选择“面积”或“周长”（π=3.14）";
            case DIAM_AREA:
                return "圆计算：请输入面积（直径阶段，π=3.14）";
            case RAD_AREA:
                return "圆计算：请输入面积（半径阶段，π=3.14）";
            case DIAM_CIRC:
                return "圆计算：请输入周长（直径阶段，π=3.14）";
            case RAD_CIRC:
                return "圆计算：请输入周长（半径阶段，π=3.14）";
            case REVEAL_AREA_DIAM:
            case REVEAL_AREA_RAD:
                return "揭示：面积计算（π=3.14）";
            case REVEAL_CIRC_DIAM:
            case REVEAL_CIRC_RAD:
                return "揭示：周长计算（π=3.14）";
            default:
                return "";
        }
    }

    @Override
    protected void startTask() {
        // 只有从 CHOICE 点击进入才启动
        if (phase == Phase.CHOICE) return;
        round = 0;
        score = 0;
        loadNextCircle();
    }

    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;
        double userAns;
        try {
            userAns = Double.parseDouble(answerField.getText().trim());
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("请输入有效数字！");
            return;
        }

        attemptsLeft--;
        boolean isArea = (phase == Phase.DIAM_AREA || phase == Phase.RAD_AREA);
        boolean isRadius = (phase == Phase.RAD_AREA || phase == Phase.RAD_CIRC);
        // 计算正确值
        double correct;
        if (isArea) {
            double r = isRadius ? currentValue : currentValue / 2.0;
            correct = PI * r * r;
        } else {
            correct = isRadius ? 2 * PI * currentValue : PI * currentValue;
        }

        if (Math.abs(userAns - correct) < 0.01) {
            int delta = GradingSystem.grade(3 - attemptsLeft, false);
            score += delta;
            feedbackLabel.setText("正确！得分：" + delta + "，累计：" + score);
            countdownTimer.stop();
            submitButton.setEnabled(false);
            // 延迟揭示
            new Timer(1000, e -> {
                ((Timer) e.getSource()).stop();
                showRevealPanel();
            }).start();
        } else if (attemptsLeft > 0) {
            feedbackLabel.setText("错误！再试一次～");
        } else {
            countdownTimer.stop();
            submitButton.setEnabled(false);
            showRevealPanel();
        }
    }

    @Override
    protected void saveAndFinish() {
        ProgressTracker.saveProgress(grade, taskId, score);
        SwingUtilities.invokeLater(() -> {
            StyleUtils.applyGlobalStyle(SwingUtilities.getWindowAncestor(this));
            JOptionPane.showMessageDialog(
                    this,
                    "任务完成！总得分：" + score + " / 12",
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

    /** Step 1：选择面板 **/
    private void initChoicePanel() {
        phase = Phase.CHOICE;
        instructionLabel.setText(getTaskTitle());

        contentPanel.removeAll();
        choicePanel = new JPanel(new GridLayout(1, 2, 20, 20));
        JButton areaBtn = StyleUtils.createStyledButton("面积");
        JButton circBtn = StyleUtils.createStyledButton("周长");
        areaBtn.setEnabled(!areaDone);
        circBtn.setEnabled(!circDone);

        areaBtn.addActionListener(e -> {
            phase = Phase.DIAM_AREA;
            startTask();
        });
        circBtn.addActionListener(e -> {
            phase = Phase.DIAM_CIRC;
            startTask();
        });

        choicePanel.add(areaBtn);
        choicePanel.add(circBtn);

        contentPanel.add(choicePanel);
        submitButton.setVisible(false);
        homeButton.setVisible(true);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /** Step 2：出题与布局 **/
    private void loadNextCircle() {
        if (++round > 4) {
            saveAndFinish();
            return;
        }
        attemptsLeft = 3;
        boolean isRadiusPhase = (phase == Phase.RAD_AREA || phase == Phase.RAD_CIRC);

        // 生成数值
        currentValue = isRadiusPhase
                ? 1 + new Random().nextInt(20)
                : 2 * (1 + new Random().nextInt(20));

        // 布局 Practice 界面
        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();
        initReturnToChoice();
        initTimerLabel();
        initCirclePanel(isRadiusPhase);
        initValueLabel(isRadiusPhase);
        initInputArea();
        submitButton.setVisible(true);
        submitButton.setEnabled(true);
        homeButton.setVisible(true);
        startCountdown(180);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /** 新增：随时返回选择，不计分、不置灰 **/
    private void initReturnToChoice() {
        if (returnToChoiceButton != null) bottom.remove(returnToChoiceButton);
        returnToChoiceButton = StyleUtils.createStyledButton("返回选择");
        returnToChoiceButton.addActionListener(e -> initChoicePanel());
        bottom.add(returnToChoiceButton, 1);  // 放在 submit 和 backButton 之间
        bottom.revalidate();
    }

    private void initTimerLabel() {
        timerLabel = new JLabel("剩余时间：180s");
        styleCenter(timerLabel);
        contentPanel.add(timerLabel);
    }

    private void initCirclePanel(boolean isRadius) {
        String path = isRadius
                ? "/images/task4/circleWithRadius.png"
                : "/images/task4/circleWithDiameter.png";
        Image img;
        try { img = ImageIO.read(getClass().getResource(path)); }
        catch (IOException ex) { ex.printStackTrace(); return; }

        circlePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                g.drawImage(img, (w - img.getWidth(null)) / 2, (h - img.getHeight(null)) / 2, null);
            }
        };
        circlePanel.setPreferredSize(new Dimension(300, 300));
        circlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(circlePanel);
    }

    private void initValueLabel(boolean isRadius) {
        String lbl = (isRadius ? "半径：" : "直径：") + (int) currentValue;
        valueLabel = new JLabel(lbl);
        styleCenter(valueLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(valueLabel);
    }

    private void initInputArea() {
        answerField = StyleUtils.createRoundedTextField(200, 32);
        styleCenter(answerField);
        feedbackLabel = new JLabel(" ");
        styleCenter(feedbackLabel);

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(feedbackLabel);
    }

    private void startCountdown(int secs) {
        remainingSeconds = secs;
        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            if (--remainingSeconds <= 0) {
                countdownTimer.stop();
                submitButton.setEnabled(false);
                feedbackLabel.setText("时间到！");
                // 延迟揭示
                new Timer(1000, ev -> {
                    ((Timer)ev.getSource()).stop();
                    showRevealPanel();
                }).start();
            }
            timerLabel.setText("剩余时间：" + remainingSeconds + "s");
        });
        countdownTimer.start();
    }

    /** Step 3：揭示 & 推进 **/
    private void showRevealPanel() {
        // 设定 reveal 阶段
        switch (phase) {
            case DIAM_AREA: phase = Phase.REVEAL_AREA_DIAM; break;
            case RAD_AREA:  phase = Phase.REVEAL_AREA_RAD;  break;
            case DIAM_CIRC: phase = Phase.REVEAL_CIRC_DIAM; break;
            default:        phase = Phase.REVEAL_CIRC_RAD;  break;
        }

        instructionLabel.setText(getTaskTitle());
        contentPanel.removeAll();

        // 加载揭示图
        String imgPath;
        switch (phase) {
            case REVEAL_AREA_DIAM: imgPath = "/images/task4/calculateAreaBasedOnDiameter.png"; break;
            case REVEAL_AREA_RAD:  imgPath = "/images/task4/calculatingAreaBasedOnRadius.png"; break;
            case REVEAL_CIRC_DIAM: imgPath = "/images/task4/calculateCircumferenceBasedOnDiameter.png"; break;
            default:               imgPath = "/images/task4/calculatingCircumferenceBasedOnRadius.png"; break;
        }
        ImageIcon icon;
        try { icon = new ImageIcon(ImageIO.read(getClass().getResource(imgPath))); }
        catch (IOException ex) { ex.printStackTrace(); icon = new ImageIcon(); }
        JLabel pic = new JLabel(icon);
        styleCenter(pic);
        contentPanel.add(pic);

        // 计算正确答案
        boolean isArea = (phase == Phase.REVEAL_AREA_DIAM || phase == Phase.REVEAL_AREA_RAD);
        boolean byRadius = (phase == Phase.REVEAL_AREA_RAD || phase == Phase.REVEAL_CIRC_RAD);
        double correct = isArea
                ? PI * Math.pow(byRadius ? currentValue : currentValue / 2.0, 2)
                : (byRadius ? 2 * PI * currentValue : PI * currentValue);

        JLabel txt = new JLabel(
                String.format("<html>π=3.14<br/>正确答案：%.2f</html>", correct)
        );
        styleCenter(txt);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(txt);

        contentPanel.revalidate();
        contentPanel.repaint();

        // 延迟推进
        new Timer(1000, e -> {
            ((Timer)e.getSource()).stop();
            // 半径揭示后，标记完成并返回选择；否则进入下一个半径阶段
            if (phase == Phase.REVEAL_AREA_RAD) {
                areaDone = true;
                initChoicePanel();
            } else if (phase == Phase.REVEAL_CIRC_RAD) {
                circDone = true;
                initChoicePanel();
            } else {
                phase = (phase == Phase.REVEAL_AREA_DIAM) ? Phase.RAD_AREA : Phase.RAD_CIRC;
                loadNextCircle();
            }
        }).start();
    }

    private void styleCenter(JComponent c) {
        c.setFont(StyleUtils.DEFAULT_FONT);
        c.setForeground(StyleUtils.TEXT_COLOR);
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}