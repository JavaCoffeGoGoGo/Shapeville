package ui.tasks;

import ui.HomePanel;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

public class Bonus2Panel extends JPanel {
    private final MainFrame mainFrame;
    private final int grade;
    private final String taskId;

    private final String imagePath = "resources/images/bonus2/";
    private final int TOTAL_SHAPES = 8;
    private final Map<Integer, Boolean> attempted = new HashMap<>();
    private final JLabel imageLabel = new JLabel();
    private final JTextField inputField = new JTextField(10);
    private final JLabel feedbackLabel = new JLabel("");
    private final JButton submitButton = new JButton("提交");
    private final JButton homeButton = new JButton("返回首页");
    private final JButton continueButton = new JButton("继续练习");
    private final JLabel timerLabel = new JLabel("剩余时间: 300s");

    private int currentIndex = -1;
    private int remainingTime = 300;
    private int attempts = 0;
    private Timer countdownTimer;
    private double correctAnswer;

    public Bonus2Panel(MainFrame mainFrame, int grade, String taskId) {
        this.mainFrame = mainFrame;
        this.grade = grade;
        this.taskId = taskId;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("请选择一个扇形图进行计算:"));
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout());
        for (int i = 1; i <= TOTAL_SHAPES; i++) {
            JButton shapeBtn = new JButton(String.valueOf(i));
            int idx = i;
            shapeBtn.addActionListener(e -> {
                if (attempted.getOrDefault(idx, false)) {
                    JOptionPane.showMessageDialog(this, "该图形已完成！");
                } else {
                    currentIndex = idx;
                    startTask(idx);
                }
            });
            centerPanel.add(shapeBtn);
        }
        add(centerPanel, BorderLayout.CENTER);

        JPanel imagePanel = new JPanel();
        imagePanel.add(imageLabel);
        add(imagePanel, BorderLayout.WEST);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("请输入结果:"));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);
        inputPanel.add(timerLabel);
        add(inputPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(feedbackLabel);
        bottomPanel.add(continueButton);
        bottomPanel.add(homeButton);
        add(bottomPanel, BorderLayout.EAST);

        inputField.setEnabled(false);
        submitButton.setEnabled(false);
        continueButton.setEnabled(false);

        submitButton.addActionListener(e -> checkAnswer());
        continueButton.addActionListener(e -> {
            if (attempted.size() == TOTAL_SHAPES) {
                JOptionPane.showMessageDialog(this, "恭喜，全部完成！");
            }
            resetForNext();
        });
        homeButton.addActionListener(e -> goHome());
    }

    private void startTask(int index) {
        attempts = 0;
        inputField.setText("");
        feedbackLabel.setText("");
        continueButton.setEnabled(false);
        inputField.setEnabled(true);
        submitButton.setEnabled(true);
        generateQuestion(index);
        loadImage(index + ".png");

        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        remainingTime = 300;
        timerLabel.setText("剩余时间: 300s");
        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                remainingTime--;
                SwingUtilities.invokeLater(() -> timerLabel.setText("剩余时间: " + remainingTime + "s"));
                if (remainingTime <= 0) {
                    countdownTimer.cancel();
                    showAnswer();
                }
            }
        }, 1000, 1000);
    }

    private void generateQuestion(int index) {
        Random rand = new Random();
        int radius = rand.nextInt(10) + 1;
        int angle = (rand.nextInt(6) + 1) * 30;
        boolean isArea = rand.nextBoolean();

        if (isArea) {
            correctAnswer = Math.PI * radius * radius * angle / 360.0;
            feedbackLabel.setText("计算扇形面积，半径 = " + radius + ", 角度 = " + angle);
        } else {
            correctAnswer = 2 * Math.PI * radius * angle / 360.0;
            feedbackLabel.setText("计算弧长，半径 = " + radius + ", 角度 = " + angle);
        }
    }

    private void checkAnswer() {
        String input = inputField.getText().trim();
        try {
            double userAnswer = Double.parseDouble(input);
            if (Math.abs(userAnswer - correctAnswer) < 0.01) {
                showAnswer();
            } else {
                attempts++;
                if (attempts >= 3) {
                    showAnswer();
                } else {
                    feedbackLabel.setText("错误！请再试一次。（剩余尝试: " + (3 - attempts) + "）");
                }
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("请输入合法数字！");
        }
    }

    private void showAnswer() {
        inputField.setEnabled(false);
        submitButton.setEnabled(false);
        continueButton.setEnabled(true);
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        loadImage(currentIndex + "_副本.png");
        attempted.put(currentIndex, true);
    }

    private void resetForNext() {
        imageLabel.setIcon(null);
        feedbackLabel.setText("请选择一个新的图形");
        inputField.setText("");
    }

    private void loadImage(String filename) {
        ImageIcon icon = new ImageIcon(imagePath + filename);
        Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
    }

    private void goHome() {
        mainFrame.returnToHome();
    }
}