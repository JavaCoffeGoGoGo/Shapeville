package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;
import logic.GradingSystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Task3Panel：图形面积计算任务面板
 */
public class Task3Panel extends AbstractTaskPanel {

    private static final String[] SHAPES = {
            "rectangle", "triangle", "trapezium", "parallelogram"
    };

    private List<String> shapeList;
    private String currentShape;
    private Map<String,int[]> dims;
    private JLabel timerLabel;
    private JLabel feedbackLabel;
    private JTextField answerField;
    private JPanel drawingPanel;
    private Timer countdownTimer;
    private boolean isRevealMode = false;

    public Task3Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
        backButton.setVisible(true);
    }

    @Override
    protected String getTaskTitle() {
        return "面积计算：请输入面积（共4轮）";
    }

    @Override
    protected void startTask() {
        round = 0;
        score = 0;
        attemptsLeft = 3;
        shapeList = new ArrayList<>(Arrays.asList(SHAPES));
        Collections.shuffle(shapeList);
        shapeList = shapeList.subList(0, 4);
        loadNextShape();
    }

    private void loadNextShape() {
        if (round >= 4 || shapeList.isEmpty()) {
            saveAndFinish();
            return;
        }
        currentShape = shapeList.remove(0);
        attemptsLeft = 3;
        round++;
        isRevealMode = false;
        dims = generateDimsMap();

        contentPanel.removeAll();
        contentPanel.add(Box.createVerticalGlue());
        initTimerLabel();
        initDrawingPanel();
        initInputArea();
        startCountdown(180);
        contentPanel.add(Box.createVerticalGlue());

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void initTimerLabel() {
        timerLabel = new JLabel("剩余时间：180s");
        timerLabel.setFont(StyleUtils.DEFAULT_FONT);
        timerLabel.setForeground(StyleUtils.TEXT_COLOR);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(timerLabel);
    }

    private void initDrawingPanel() {
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                String path = isRevealMode
                        ? "/images/task3/areaOf" + capitalize(currentShape) + ".png"
                        : "/images/task3/" + currentShape + ".png";
                try {
                    Image img = ImageIO.read(getClass().getResource(path));
                    int iw = img.getWidth(null), ih = img.getHeight(null);
                    int x = (w - iw) / 2, y = (h - ih) / 2;
                    g.drawImage(img, x, y, null);
                    if (!isRevealMode) {
                        int[] d = dims.get(currentShape);
                        g.setFont(StyleUtils.DEFAULT_FONT);
                        g.setColor(StyleUtils.TEXT_COLOR);
                        switch (currentShape) {
                            case "rectangle": case "parallelogram":
                                g.drawString("w=" + d[0], x + iw + 10, y + 20);
                                g.drawString("h=" + d[1], x + iw + 10, y + 40);
                                break;
                            case "triangle":
                                g.drawString("b=" + d[0], x + iw + 10, y + 20);
                                g.drawString("h=" + d[1], x + iw + 10, y + 40);
                                break;
                            case "trapezium":
                                g.drawString("a=" + d[0], x + iw + 10, y + 20);
                                g.drawString("b=" + d[1], x + iw + 10, y + 40);
                                g.drawString("h=" + d[2], x + iw + 10, y + 60);
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        drawingPanel.setPreferredSize(new Dimension(500, 400));
        drawingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(drawingPanel);
    }

    private void initInputArea() {
        answerField = StyleUtils.createRoundedTextField(200, 32);
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(StyleUtils.DEFAULT_FONT);
        feedbackLabel.setForeground(StyleUtils.TEXT_COLOR);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(feedbackLabel);
    }

    // 在类成员中，保留这个字段，不要再用局部 seconds
    private int remainingSeconds;

    private void startCountdown(int seconds) {
        // 把传入的 seconds 赋给字段
        remainingSeconds = seconds;

        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            timerLabel.setText("剩余时间：" + remainingSeconds + "s");

            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                revealAndNext();
            }
        });
        countdownTimer.start();
    }

    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;
        double userAns;
        try {
            userAns = Double.parseDouble(answerField.getText().trim());
        } catch (Exception ex) {
            feedbackLabel.setText("请输入有效数字！");
            return;
        }
        attemptsLeft--;
        int attemptNum = 3 - attemptsLeft;
        double correctArea = calcArea(currentShape, dims.get(currentShape));

        if (Math.abs(userAns - correctArea) < 0.01) {
            score += GradingSystem.grade(attemptNum, false);
            revealAndNext();
        } else if (attemptsLeft > 0) {
            feedbackLabel.setText("错误！再试一次～");
        } else {
            revealAndNext();
        }
    }

    /** 揭示当前答案，并自动在1秒后进入下一题 */
    private void revealAndNext() {
        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
        isRevealMode = true;
        feedbackLabel.setText(""); // 隐藏旧提示
        contentPanel.removeAll();
        contentPanel.add(Box.createVerticalGlue());
        initTimerLabel();     // 可保留时间显示或隐藏
        initDrawingPanel();   // 会根据isRevealMode加载带公式图
        // 公式与代入
        int[] d = dims.get(currentShape);
        double ans = calcArea(currentShape, d);
        String html = String.format(
                "<html>公式：%s<br/>代入：%s<br/>正确答案：%.2f</html>",
                buildFormula(currentShape),
                buildSubstitution(currentShape, d),
                ans
        );
        JLabel txt = new JLabel(html);
        txt.setFont(StyleUtils.DEFAULT_FONT);
        txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(txt);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();

        // 延迟 1 秒进入下一题
        new Timer(3000, e -> {
            ((Timer)e.getSource()).stop();
            loadNextShape();
        }).start();
    }

    private Map<String,int[]> generateDimsMap() {
        Random rnd = new Random();
        Map<String,int[]> m = new HashMap<>();
        m.put("rectangle",    new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        m.put("triangle",     new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        m.put("trapezium",    new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        m.put("parallelogram",new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        return m;
    }

    private double calcArea(String shape, int[] d) {
        return switch (shape) {
            case "rectangle", "parallelogram" -> d[0] * d[1];
            case "triangle"                   -> d[0] * d[1] / 2.0;
            case "trapezium"                  -> (d[0] + d[1]) * d[2] / 2.0;
            default                            -> 0.0;
        };
    }

    private String buildFormula(String s) {
        return switch (s) {
            case "rectangle"     -> "A = w × h";
            case "triangle"      -> "A = b × h / 2";
            case "trapezium"     -> "A = (a + b) × h / 2";
            case "parallelogram" -> "A = b × h";
            default               -> "";
        };
    }

    private String buildSubstitution(String s, int[] d) {
        return switch (s) {
            case "rectangle"     -> String.format("A = %d × %d", d[0], d[1]);
            case "triangle"      -> String.format("A = %d × %d / 2", d[0], d[1]);
            case "trapezium"     -> String.format("A = (%d + %d) × %d / 2", d[0], d[1], d[2]);
            case "parallelogram" -> String.format("A = %d × %d", d[0], d[1]);
            default               -> "";
        };
    }

    private String capitalize(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    @Override
    protected void saveAndFinish() {
        ProgressTracker.saveProgress(grade, taskId, score);
        SwingUtilities.invokeLater(() -> {
            StyleUtils.applyGlobalStyle(
                    SwingUtilities.getWindowAncestor(this)
            );
            JOptionPane.showMessageDialog(
                    this,
                    "任务完成！你的得分是：" + score + " / " + (4 * 3),
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
}