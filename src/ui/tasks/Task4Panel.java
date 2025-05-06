package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.image.BufferedImage;

public class Task4Panel extends AbstractTaskPanel {

    private static final int MAX_ATTEMPTS = 3;
    private static final int TIME_LIMIT_SEC = 180;
    private static final double PI = 3.14;

    private String calcType;
    private int attemptsLeft;
    private int remainingTime;
    private Timer countdown;

    // UI Components
    private JLabel imageLabel;
    private JLabel dimsLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;
    private JLabel timeLabel;
    private JLabel formulaImageLabel;
    private JLabel formulaTextLabel;

    public Task4Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "圆的面积与周长计算：π取3.14，请选择面积或周长进行计算";
    }

    @Override
    protected void startTask() {
        loadRound();
    }

    @Override
    protected void onSubmit() {
        if (answerField.isEnabled() == false) return;
        String text = answerField.getText().trim();
        if (text.isEmpty()) {
            feedbackLabel.setText("请输入结果！");
            return;
        }
        try {
            double input = Double.parseDouble(text);
            double correct = computeCorrect();
            if (Math.abs(input - correct) < 0.001) {
                feedbackLabel.setText("✅ 正确！");
                countdown.stop();
                highlightDimension();
                showSolution(correct);
            } else {
                attemptsLeft--;
                if (attemptsLeft <= 0) {
                    feedbackLabel.setText("❌ 机会用尽！");
                    countdown.stop();
                    highlightDimension();
                    showSolution(correct);
                } else {
                    feedbackLabel.setText("❌ 错误，还有 " + attemptsLeft + " 次");
                }
            }
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("请输入有效数字！");
        }
    }

    @Override
    protected void saveAndFinish() {
        ProgressTracker.saveProgress(grade, taskId, 0);
    }

    private void loadRound() {
        removeAllContent();
        attemptsLeft = MAX_ATTEMPTS;
        remainingTime = TIME_LIMIT_SEC;

        // Random select
        Random rand = new Random();
        calcType = rand.nextBoolean() ? "面积" : "周长";
        double radius = 1 + rand.nextInt(20);
        String dimsText = calcType.equals("面积") ?
                String.format("半径: %.1f", radius) :
                String.format("直径: %.1f", radius * 2);

        // Build UI
        imageLabel = new JLabel(loadIcon("circle.png", 150, 150));
        dimsLabel = new JLabel(dimsText);
        dimsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerField = new JTextField(10);
        timeLabel = new JLabel(formatTime(remainingTime));
        feedbackLabel = new JLabel("π取3.14，请在" + formatTime(remainingTime) + "内输入" + calcType);

        contentPanel.removeAll();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(dimsLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(timeLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(feedbackLabel);
        contentPanel.revalidate();
        contentPanel.repaint();

        // Timer
        if (countdown != null && countdown.isRunning()) countdown.stop();
        countdown = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTime--;
                timeLabel.setText(formatTime(remainingTime));
                if (remainingTime <= 0) {
                    countdown.stop();
                    feedbackLabel.setText("⏰ 时间到！");
                    highlightDimension();
                    showSolution(computeCorrect());
                }
            }
        });
        countdown.start();
    }

    private void showSolution(double correct) {
        // disable input
        answerField.setEnabled(false);
        submitButton.setEnabled(false);

        // formula display
        formulaImageLabel = new JLabel(loadIcon(
                calcType.equals("面积") ? "calculatingAreaBasedOnRadius.png"
                        : "calculatingCircumferenceBasedOnRadius.png", 400,200));
        formulaTextLabel = new JLabel(buildFormulaText(correct));

        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(formulaImageLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(formulaTextLabel);
        contentPanel.revalidate();
        contentPanel.repaint();

        // ask continue
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(
                    Task4Panel.this,
                    "是否继续直径训练？",
                    "继续训练",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                answerField.setEnabled(true);
                submitButton.setEnabled(true);
                loadRound();
            } else {
                mainFrame.returnToHome();
            }
        });
    }

    private void highlightDimension() {
        dimsLabel.setBorder(new LineBorder(Color.RED, 2));
    }

    private double computeCorrect() {
        String text = dimsLabel.getText();
        double val = Double.parseDouble(text.split(":")[1].trim());
        double radius = text.startsWith("直径") ? val / 2 : val;
        return calcType.equals("面积") ? PI * radius * radius : 2 * PI * radius;
    }

    private String buildFormulaText(double result) {
        String text = dimsLabel.getText();
        double val = Double.parseDouble(text.split(":")[1].trim());
        double radius = text.startsWith("直径") ? val / 2 : val;
        if (calcType.equals("面积")) {
            return String.format("面积 = π × 半径² = 3.14 × %.1f² = %.2f", radius, result);
        } else {
            return String.format("周长 = 2 × π × 半径 = 2 × 3.14 × %.1f = %.2f", radius, result);
        }
    }

    private void removeAllContent() {
        contentPanel.removeAll();
    }

    private ImageIcon loadIcon(String name, int w, int h) {
        try (InputStream in = getClass().getResourceAsStream("/images/task4/" + name)) {
            if (in != null) {
                BufferedImage img = ImageIO.read(in);
                Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (IOException ignored) {}
        return new ImageIcon();
    }


    private String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }
}
