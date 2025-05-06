package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Task3Panel：图形面积计算任务，继承 AbstractTaskPanel
 * 支持矩形、三角形、梯形、平行四边形的面积计算
 * 随机生成边长（1~20），3 分钟内，最多 3 次尝试
 * 展示公式代入和正确答案
 */
public class Task3Panel extends AbstractTaskPanel {

    private static final String[] SHAPES = {"rectangle", "triangle", "trapezium", "parallelogram"};
    private static final int MAX_ATTEMPTS = 3;
    private static final int MAX_ROUNDS = SHAPES.length;
    private static final int TIME_LIMIT_SEC = 180;

    private List<String> shapeList;
    private String currentShape;
    private int attemptsLeft;
    private int score;
    private int remainingTime;

    // UI components
    private JLabel shapeImageLabel;
    private JLabel dimsLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;
    private JLabel timeLabel;
    private JLabel formulaImageLabel;
    private JLabel formulaTextLabel;
    private Timer countdownTimer;

    public Task3Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "图形面积计算：计算矩形、三角形、梯形和平行四边形的面积";
    }

    @Override
    protected void startTask() {
        shapeList = new ArrayList<>(Arrays.asList(SHAPES));
        score = 0;
        loadNextShape();
    }

    @Override
    protected void onSubmit() {
        if (taskFinished) return;
        String input = answerField.getText().trim();
        if (input.isEmpty()) {
            feedbackLabel.setText("请输入面积！");
            return;
        }
        try {
            double userAns = Double.parseDouble(input);
            double correct = calculateArea();
            if (Math.abs(userAns - correct) < 0.001) {
                score += 1;
                feedbackLabel.setText("✅ 正确！得分 +1 (总分: " + score + ")");
                countdownTimer.stop();
                showSolution(correct);
            } else {
                attemptsLeft--;
                if (attemptsLeft <= 0) {
                    feedbackLabel.setText("❌ 尝试次数用尽，正确答案: " + correct);
                    countdownTimer.stop();
                    showSolution(correct);
                } else {
                    feedbackLabel.setText("❌ 错误，还有 " + attemptsLeft + " 次机会");
                }
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("请输入有效数字！");
        }
    }

    @Override
    protected void saveAndFinish() {
        JOptionPane.showMessageDialog(this,
                "任务完成！你的总得分是：" + score + "\n" + getEncouragement(score),
                "完成任务", JOptionPane.INFORMATION_MESSAGE);
        ProgressTracker.saveProgress(grade, taskId, score);
    }

    private void loadNextShape() {
        contentPanel.removeAll();
        formulaImageLabel = null;
        formulaTextLabel = null;

        if (shapeList.isEmpty()) {
            taskFinished = true;
            submitButton.setEnabled(false);
            saveAndFinish();
            return;
        }

        currentShape = shapeList.remove(0);
        attemptsLeft = MAX_ATTEMPTS;
        remainingTime = TIME_LIMIT_SEC;

        shapeImageLabel = new JLabel(loadImageIcon(currentShape + ".png"));
        shapeImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String dimsText = generateDimensions();
        dimsLabel = new JLabel(dimsText);
        dimsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        answerField = new JTextField(10);
        answerField.setMaximumSize(new Dimension(100, 30));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        timeLabel = new JLabel(formatTime(remainingTime));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackLabel = new JLabel("请在 " + formatTime(remainingTime) + " 内输入面积");
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(shapeImageLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(dimsLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(timeLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(feedbackLabel);
        contentPanel.revalidate();
        contentPanel.repaint();

        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            remainingTime--;
            timeLabel.setText(formatTime(remainingTime));
            if (remainingTime <= 0) {
                countdownTimer.stop();
                double correct = calculateArea();
                feedbackLabel.setText("⏰ 时间到！正确答案: " + correct);
                showSolution(correct);
            }
        });
        countdownTimer.start();
    }

    private void showSolution(double correct) {
        formulaImageLabel = new JLabel(loadImageIcon("areaOf" + capitalize(currentShape) + ".png"));
        formulaImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formulaTextLabel = new JLabel(getFormulaText(getShapeName(currentShape), extractParamsFromLabel()));
        formulaTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        answerField.setEnabled(false);
        submitButton.setEnabled(false);

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(formulaImageLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(formulaTextLabel);
        contentPanel.revalidate();
        contentPanel.repaint();

        new Timer(2000, e -> {
            submitButton.setEnabled(true);
            loadNextShape();
        }) {{ setRepeats(false); }}.start();
    }

    private double calculateArea() {
        Map<String, Double> map = extractLabelValues();
        return switch (currentShape) {
            case "rectangle" -> map.get("宽") * map.get("高");
            case "triangle" -> 0.5 * map.get("底") * map.get("高");
            case "trapezium" -> 0.5 * (map.get("上底") + map.get("下底")) * map.get("高");
            case "parallelogram" -> map.get("底") * map.get("高");
            default -> 0;
        };
    }

    private String generateDimensions() {
        Random r = new Random();
        return switch (currentShape) {
            case "rectangle" -> "宽:" + (r.nextInt(20) + 1) + ",高:" + (r.nextInt(20) + 1);
            case "triangle" -> "底:" + (r.nextInt(20) + 1) + ",高:" + (r.nextInt(20) + 1);
            case "trapezium" -> "上底:" + (r.nextInt(20) + 1) + ",下底:" + (r.nextInt(20) + 1) + ",高:" + (r.nextInt(20) + 1);
            case "parallelogram" -> "底:" + (r.nextInt(20) + 1) + ",高:" + (r.nextInt(20) + 1);
            default -> "";
        };
    }

    private Map<String, Double> extractLabelValues() {
        Map<String, Double> map = new HashMap<>();
        String[] parts = dimsLabel.getText().split(",");
        for (String part : parts) {
            String[] kv = part.split(":");
            map.put(kv[0], Double.parseDouble(kv[1]));
        }
        return map;
    }

    private double[] extractParamsFromLabel() {
        Map<String, Double> map = extractLabelValues();
        return switch (currentShape) {
            case "rectangle" -> new double[]{map.get("宽"), map.get("高")};
            case "triangle" -> new double[]{map.get("底"), map.get("高")};
            case "trapezium" -> new double[]{map.get("上底"), map.get("下底"), map.get("高")};
            case "parallelogram" -> new double[]{map.get("底"), map.get("高")};
            default -> new double[]{};
        };
    }

    private String getFormulaText(String shapeType, double... params) {
        return switch (shapeType) {
            case "矩形" -> String.format("面积 = 长 × 宽 = %.1f × %.1f = %.1f", params[0], params[1], params[0] * params[1]);
            case "三角形" -> String.format("面积 = ½ × 底 × 高 = ½ × %.1f × %.1f = %.1f", params[0], params[1], 0.5 * params[0] * params[1]);
            case "梯形" -> String.format("面积 = ½ × (上底 + 下底) × 高 = ½ × (%.1f + %.1f) × %.1f = %.1f",
                    params[0], params[1], params[2], 0.5 * (params[0] + params[1]) * params[2]);
            case "平行四边形" -> String.format("面积 = 底 × 高 = %.1f × %.1f = %.1f", params[0], params[1], params[0] * params[1]);
            default -> "未知图形类型";
        };
    }

    private String getShapeName(String shapeKey) {
        return switch (shapeKey) {
            case "rectangle" -> "矩形";
            case "triangle" -> "三角形";
            case "trapezium" -> "梯形";
            case "parallelogram" -> "平行四边形";
            default -> "未知";
        };
    }

    private ImageIcon loadImageIcon(String fileName) {
        String path = "/images/task3/" + fileName;
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in != null) {
                BufferedImage img = ImageIO.read(in);
                return new ImageIcon(img);
            }
        } catch (IOException ignored) {}
        return new ImageIcon();
    }

    private String formatTime(int sec) {
        int m = sec / 60, s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}