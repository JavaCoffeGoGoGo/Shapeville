package ui.tasks;

import ui.HomePanel;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;

public class Bonus1Panel extends JPanel {
    private final MainFrame mainFrame;
    private final int grade;
    private final String taskId;

    private final String imagePath = "resources/images/bonus1/";
    private static final int TOTAL_TIME = 300; // 5 minutes
    private static final int MAX_ATTEMPTS = 3;
    private static final int TOTAL_SHAPES = 9;

    private final JButton[] shapeButtons = new JButton[TOTAL_SHAPES];
    private final boolean[] completed = new boolean[TOTAL_SHAPES];
    private final JLabel imageLabel = new JLabel();
    private final JTextField answerField = new JTextField(10);
    private final JLabel feedbackLabel = new JLabel(" ");
    private final JButton submitButton = new JButton("提交");
    private final JButton homeButton = new JButton("返回首页");
    private final JButton nextButton = new JButton("继续练习");
    private final JLabel timerLabel = new JLabel("剩余时间: " + TOTAL_TIME + "秒");

    private int currentShapeIndex = -1;
    private int attemptsLeft;
    private Timer countdownTimer;
    private int timeRemaining;

    private final JPanel selectionPanel = new JPanel();
    private final JPanel questionPanel = new JPanel();
    private final JPanel resultPanel = new JPanel();

    private final Map<Integer, String> imageMap = new HashMap<>();
    private final Map<Integer, String> answerImageMap = new HashMap<>();
    private final Map<Integer, String> questionTextMap = new HashMap<>();

    public Bonus1Panel(MainFrame mainFrame, int grade, String taskId) {
        this.mainFrame = mainFrame;
        this.grade = grade;
        this.taskId = taskId;

        setLayout(new BorderLayout());
        initData();
        initComponents();
    }

    private void initData() {
        for (int i = 0; i < TOTAL_SHAPES; i++) {
            imageMap.put(i, (i + 1) + ".png");
            answerImageMap.put(i, (i + 1) + "_副本.png");
            questionTextMap.put(i, "已知参数，请计算第 " + (i + 1) + " 个图形的面积。");
        }
    }

    private void initComponents() {
        // Top: home button
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        homeButton.addActionListener(e -> goHome());
        top.add(homeButton);
        add(top, BorderLayout.NORTH);

        // Selection panel
        selectionPanel.setLayout(new GridLayout(3, 3, 10, 10));
        for (int i = 0; i < TOTAL_SHAPES; i++) {
            final int idx = i;
            shapeButtons[i] = new JButton("图形 " + (i + 1));
            shapeButtons[i].addActionListener(e -> {
                if (completed[idx]) {
                    JOptionPane.showMessageDialog(this, "该图形已完成！");
                } else {
                    startQuestion(idx);
                }
            });
            selectionPanel.add(shapeButtons[i]);
        }
        add(selectionPanel, BorderLayout.CENTER);

        // Question panel
        questionPanel.setLayout(new BorderLayout());
        JPanel qTop = new JPanel(new FlowLayout());
        qTop.add(timerLabel);
        questionPanel.add(qTop, BorderLayout.NORTH);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionPanel.add(imageLabel, BorderLayout.CENTER);

        JPanel qBottom = new JPanel(new GridLayout(4, 1));
        qBottom.add(new JLabel("请输入你的答案："));
        qBottom.add(answerField);
        qBottom.add(feedbackLabel);
        submitButton.addActionListener(e -> checkAnswer());
        qBottom.add(submitButton);
        questionPanel.add(qBottom, BorderLayout.SOUTH);

        // Result panel
        resultPanel.setLayout(new FlowLayout());
        nextButton.addActionListener(e -> goBackToSelection());
        resultPanel.add(nextButton);
    }

    private void startQuestion(int index) {
        currentShapeIndex = index;
        attemptsLeft = MAX_ATTEMPTS;
        timeRemaining = TOTAL_TIME;
        feedbackLabel.setText(" ");
        answerField.setText("");
        submitButton.setEnabled(true);
        timerLabel.setText("剩余时间: " + timeRemaining + "秒");

        // Show question dialog and image
        JOptionPane.showMessageDialog(this, questionTextMap.get(index), "题目信息", JOptionPane.INFORMATION_MESSAGE);
        loadImage(imageMap.get(index));

        remove(selectionPanel);
        add(questionPanel, BorderLayout.CENTER);
        revalidate(); repaint();

        startTimer();
    }

    private void startTimer() {
        if (countdownTimer != null) countdownTimer.cancel();
        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    timeRemaining--;
                    timerLabel.setText("剩余时间: " + timeRemaining + "秒");
                    if (timeRemaining <= 0) {
                        countdownTimer.cancel();
                        showAnswer(false);
                    }
                });
            }
        }, 1000, 1000);
    }

    private void checkAnswer() {
        String input = answerField.getText().trim();
        if (input.isEmpty()) {
            feedbackLabel.setText("请输入答案！");
            return;
        }
        // TODO: replace with real calculation logic
        String correct = "100";
        if (input.equals(correct)) {
            countdownTimer.cancel();
            showAnswer(true);
        } else {
            attemptsLeft--;
            feedbackLabel.setText("错误！剩余尝试: " + attemptsLeft);
            if (attemptsLeft <= 0) {
                countdownTimer.cancel();
                showAnswer(false);
            }
        }
    }

    private void showAnswer(boolean correct) {
        submitButton.setEnabled(false);
        completed[currentShapeIndex] = true;
        shapeButtons[currentShapeIndex].setEnabled(false);
        shapeButtons[currentShapeIndex].setBackground(Color.GRAY);

        loadImage(answerImageMap.get(currentShapeIndex));
        feedbackLabel.setText(correct ? "回答正确！" : "已显示正确答案。");

        questionPanel.add(resultPanel, BorderLayout.EAST);
        revalidate(); repaint();
    }

    private void goBackToSelection() {
        remove(questionPanel);
        add(selectionPanel, BorderLayout.CENTER);
        revalidate(); repaint();

        if (allCompleted()) {
            JOptionPane.showMessageDialog(this, "你已完成所有图形计算任务！", "完成", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean allCompleted() {
        for (boolean b : completed) if (!b) return false;
        return true;
    }

    private void loadImage(String fileName) {
        ImageIcon icon = new ImageIcon(imagePath + fileName);
        Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
    }

    private void goHome() {
        if (countdownTimer != null) countdownTimer.cancel();
        mainFrame.returnToHome();
    }
}
