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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private int remainingSeconds;
    private boolean isRevealMode = false;

    public Task3Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
        backButton.setVisible(true);
        // feedbackLabel 初始化留在 initInputArea 中，避免 startTask 调用前空指针
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

        // 随机选4个图形
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
        initTimerLabel();
        initDrawingPanel();
        initInputArea();
        startCountdown(180);

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
                String imgPath = "/images/task3/" + currentShape + ".png";
                Image img;
                try {
                    img = ImageIO.read(getClass().getResource(imgPath));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                int iw = img.getWidth(null), ih = img.getHeight(null);
                int x = (w - iw) / 2, y = (h - ih) / 2;

                if (isRevealMode) {
                    String revealPath = "/images/task3/areaOf"
                            + capitalize(currentShape) + ".png";
                    try {
                        Image rev = ImageIO.read(getClass().getResource(revealPath));
                        int rw = rev.getWidth(null), rh = rev.getHeight(null);
                        g.drawImage(rev, (w - rw) / 2, (h - rh) / 2, null);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                g.drawImage(img, x, y, null);
                int[] d = dims.get(currentShape);
                g.setFont(StyleUtils.DEFAULT_FONT);
                g.setColor(StyleUtils.TEXT_COLOR);
                switch (currentShape) {
                    case "rectangle":
                    case "parallelogram":
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
        };
        drawingPanel.setPreferredSize(new Dimension(500, 400));
        drawingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(drawingPanel);
    }

    private void initInputArea() {
        answerField = StyleUtils.createRoundedTextField(200, 32);
        answerField.setMaximumSize(answerField.getPreferredSize());
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 在此初始化反馈标签，确保非空
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(StyleUtils.DEFAULT_FONT);
        feedbackLabel.setForeground(StyleUtils.TEXT_COLOR);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(feedbackLabel);
    }

    private void startCountdown(int seconds) {
        remainingSeconds = seconds;
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            timerLabel.setText("剩余时间：" + remainingSeconds + "s");
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                showRevealPanel();
            }
        });
        countdownTimer.start();
    }

    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;

        String text = answerField.getText().trim();
        double userAns;
        try {
            userAns = Double.parseDouble(text);
        } catch (Exception ex) {
            feedbackLabel.setText("请输入有效数字！");
            feedbackLabel.revalidate();
            feedbackLabel.repaint();
            return;
        }

        attemptsLeft--;
        int attemptNum = 3 - attemptsLeft;
        double correctArea = calcArea(currentShape, dims.get(currentShape));

        if (Math.abs(userAns - correctArea) < 0.01) {
            int delta = GradingSystem.grade(attemptNum, false);
            score += delta;
            feedbackLabel.setText("正确！本次得分：" + delta + "，累计：" + score);
            feedbackLabel.revalidate();
            feedbackLabel.repaint();

            if (countdownTimer.isRunning()) countdownTimer.stop();

            // 一次性定时器进入下一题
            Timer t = new Timer(1000, ev -> {
                ((Timer)ev.getSource()).stop();
                loadNextShape();
            });
            t.setRepeats(false);
            t.start();

        } else if (attemptsLeft > 0) {
            feedbackLabel.setText("错误！再试一次～");
            feedbackLabel.revalidate();
            feedbackLabel.repaint();

        } else {
            if (countdownTimer.isRunning()) countdownTimer.stop();
            showRevealPanel();
        }
    }

    private Map<String,int[]> generateDimsMap() {
        Map<String,int[]> map = new HashMap<>();
        Random rnd = new Random();
        map.put("rectangle",    new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        map.put("triangle",     new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        map.put("trapezium",    new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        map.put("parallelogram",new int[]{1 + rnd.nextInt(20), 1 + rnd.nextInt(20)});
        return map;
    }

    private double calcArea(String shape, int[] d) {
        return switch (shape) {
            case "rectangle", "parallelogram" -> d[0] * d[1];
            case "triangle"                   -> d[0] * d[1] / 2.0;
            case "trapezium"                  -> (d[0] + d[1]) * d[2] / 2.0;
            default                            -> 0.0;
        };
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    private void showRevealPanel() {
        isRevealMode = true;
        contentPanel.removeAll();

        String imgPath = "/images/task3/areaOf" + capitalize(currentShape) + ".png";
        ImageIcon icon;
        try {
            icon = new ImageIcon(ImageIO.read(getClass().getResource(imgPath)));
        } catch (IOException e) {
            e.printStackTrace();
            icon = new ImageIcon();
        }
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(imgLabel);

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

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private String buildFormula(String shape) {
        return switch (shape) {
            case "rectangle"     -> "A = w × h";
            case "triangle"      -> "A = b × h / 2";
            case "trapezium"     -> "A = (a + b) × h / 2";
            case "parallelogram" -> "A = b × h";
            default                -> "";
        };
    }

    private String buildSubstitution(String shape, int[] d) {
        return switch (shape) {
            case "rectangle"     -> String.format("A = %d × %d", d[0], d[1]);
            case "triangle"      -> String.format("A = %d × %d / 2", d[0], d[1]);
            case "trapezium"     -> String.format("A = (%d + %d) × %d / 2", d[0], d[1], d[2]);
            case "parallelogram" -> String.format("A = %d × %d", d[0], d[1]);
            default                -> "";
        };
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
