package ui.tasks;

import logic.ProgressTracker;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

/**
 * Task1Panel：图形识别任务面板，实现基础积分等级的图形识别逻辑。
 * 用户观看图形图像，输入其名称，总共 4 个图形，每个图形最多尝试 3 次。
 */
public class Task1Panel extends AbstractTaskPanel {

    // ==== 🧱 1. 图形资源 ====
    private static final String[] SHAPES = {
            "circle", "heptagon", "hexagon", "kite", "octagon", "oval",
            "pentagon", "rectangle", "rhombus", "square", "triangle"
    };

    private List<String> shapeList; // 用于存储未展示过的图形
    private String currentShape;    // 当前图形名称
    private int round = 0;          // 当前轮次（共4轮）
    private int attemptsLeft = 3;   // 当前题目剩余尝试次数

    // ==== 🎛️ 2. UI 组件 ====
    private JLabel imageLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;







    public Task1Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }








    @Override
    protected String getTaskTitle() {
        return "图形识别：请输入图形名称（共4个）";
    }

    @Override
    protected void startTask() {
        shapeList = new ArrayList<>(Arrays.asList(SHAPES));
        Collections.shuffle(shapeList); // 打乱图形顺序
        round = 0;
        score = 0;
        loadNextShape(); // 加载第一个图形
    }

    /** 加载一个新的图形图片并刷新界面 */
    private void loadNextShape() {
        if (round >= 4 || shapeList.isEmpty()) {
            saveAndFinish();
            return;
        }

        currentShape = shapeList.remove(0); // 取出一个未展示的图形
        attemptsLeft = 3;                   // 每轮最多尝试三次
        round++;

        // 清空内容面板重新添加
        contentPanel.removeAll();

        // 加载图像
        imageLabel = new JLabel(loadShapeImage(currentShape));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        answerField = new JTextField(15);
        answerField.setMaximumSize(new Dimension(200, 30));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(feedbackLabel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /** 加载指定图形的图像资源 */
    private Icon loadShapeImage(String shapeName) {
        String path = "/images/task1TwoD/" + shapeName + ".png";
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                BufferedImage img = ImageIO.read(stream);
                Image scaled = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } else {
                return new ImageIcon(); // 加载失败占位
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ImageIcon(); // 异常处理
        }
    }

    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;

        String userInput = answerField.getText().trim().toLowerCase();
        if (userInput.isEmpty()) {
            feedbackLabel.setText("请输入你的答案！");
            return;
        }

        if (userInput.equals(currentShape)) {
            score++;
            feedbackLabel.setText("✅ 正确！" + getEncouragement(score));
            attemptCount++;
            submitButton.setEnabled(false); // 防止重复提交
            Timer timer = new Timer(1000, e -> {
                submitButton.setEnabled(true);
                loadNextShape();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            attemptsLeft--;
            if (attemptsLeft > 0) {
                feedbackLabel.setText("❌ 错误！再试一次～ 剩余尝试：" + attemptsLeft);
            } else {
                feedbackLabel.setText("❌ 正确答案是：" + currentShape);
                attemptCount++;
                submitButton.setEnabled(false);
                Timer timer = new Timer(1500, e -> {
                    submitButton.setEnabled(true);
                    loadNextShape();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    @Override
    protected void saveAndFinish() {
        taskFinished = true;
        ProgressTracker.saveProgress(grade, taskId, score); // 保存得分
        JOptionPane.showMessageDialog(this,
                "任务完成！你的得分是：" + score + " / 4\n" + getEncouragement(score),
                "完成任务", JOptionPane.INFORMATION_MESSAGE);
    }
}