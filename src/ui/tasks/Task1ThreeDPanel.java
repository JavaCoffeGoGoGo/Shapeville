package ui.tasks;

import logic.ProgressTracker;
import logic.GradingSystem;
import ui.MainFrame;
import util.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

/**
 * Task1ThreeDPanel：立体图形识别任务面板，实现基础积分等级的立体图形识别逻辑。
 * 用户观看立体图形图像，输入其名称，总共8个图形，每个图形最多尝试3次。
 */
public class Task1ThreeDPanel extends AbstractTaskPanel {

    // 1. 变量初始化
    // 1）基本变量
    private static final String[] SHAPES = {
            "cone", "cube", "cuboid", "cylinder", "sphere",
            "squarebasedpyramid", "tetrahedron", "triangularprism"
    };
    private List<String> shapeList;
    private String currentShape;

    // 2）UI 组件
    private JLabel imageLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;

    public Task1ThreeDPanel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    // 2. 任务标题
    @Override
    protected String getTaskTitle() {
        return "立体图形识别：请输入图形名称（共8轮）";
    }

    // 3. 开始任务
    @Override
    protected void startTask() {
        round = 0;
        score = 0;
        attemptsLeft = 3;
        shapeList = new ArrayList<>(Arrays.asList(SHAPES));
        Collections.shuffle(shapeList);
        loadNextShape();
    }

    // 4. 加载题目
    private void loadNextShape() {
        // 1）是否任务已结束
        if (round >= 8 || shapeList.isEmpty()) {
            saveAndFinish();
            return;
        }

        // 2）准备题目
        currentShape = shapeList.remove(0);
        attemptsLeft = 3;
        round++;

        // 3）清空旧组件
        contentPanel.removeAll();

        // 4）创建新组件
        imageLabel = new JLabel(loadShapeImage(currentShape));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        answerField = new JTextField(30);
        answerField.setMaximumSize(new Dimension(200, 30));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 5）添加组件到面板
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(feedbackLabel);

        // 6）刷新界面
        contentPanel.revalidate();
        contentPanel.repaint();

        // 7）添加返回按钮
        if (backShapeButton != null && Arrays.asList(bottom.getComponents()).contains(backShapeButton)) {
            bottom.remove(backShapeButton);
        }
        backShapeButton = StyleUtils.createStyledButton("返回图形选择界面");
        backShapeButton.addActionListener(e -> {
            ProgressTracker.saveProgress(grade, taskId, score);
            mainFrame.showPanel("g12_shape");
        });
        bottom.add(backShapeButton);
        bottom.revalidate();
        bottom.repaint();
    }

    // 5. 图像加载：带圆角和抗锯齿
    private Icon loadShapeImage(String shapeName) {
        String path = "/images/task1ThreeD/" + shapeName + ".png";
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                BufferedImage original = ImageIO.read(stream);
                int targetHeight = 400;
                int originalWidth = original.getWidth();
                int originalHeight = original.getHeight();
                double scale = (double) targetHeight / originalHeight;
                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = targetHeight;

                BufferedImage roundedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = roundedImage.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int arc = 20;
                RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, scaledWidth, scaledHeight, arc, arc);
                g2.setClip(roundedRect);
                g2.drawImage(original.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), 0, 0, null);

                g2.setClip(null);
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(roundedRect);
                g2.dispose();

                return new ImageIcon(roundedImage);
            } else {
                return new ImageIcon();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ImageIcon();
        }
    }

    // 6. 提交处理
    @Override
    protected void onSubmit() {
        if (attemptsLeft <= 0) return;

        String userInput = answerField.getText().trim().toLowerCase();
        if (userInput.isEmpty()) {
            feedbackLabel.setText("当前答案为空哦！");
            return;
        }

        attemptsLeft--;
        int attemptNum = 3 - attemptsLeft;

        if (userInput.equals(currentShape)) {
            int delta = GradingSystem.grade(attemptNum, false);
            score += delta;

            feedbackLabel.setText("✅ 正确！本次得分：" + delta + "，累计：" + score);
            attemptCount++;
            submitButton.setEnabled(false);

            Timer timer = new Timer(1000, e -> {
                submitButton.setEnabled(true);
                loadNextShape();
            });
            timer.setRepeats(false);
            timer.start();

        } else {
            if (attemptsLeft > 0) {
                feedbackLabel.setText("❌ 错误！再试一次～");
            } else {
                feedbackLabel.setText("❌ 正确答案是：" + currentShape + "（得分 0）");
                attemptCount++;
                submitButton.setEnabled(false);
                Timer timer = new Timer(1000, e -> {
                    submitButton.setEnabled(true);
                    loadNextShape();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    // 7. 结束任务
    @Override
    protected void saveAndFinish() {
        ProgressTracker.saveProgress(grade, taskId, score);
        JOptionPane.showMessageDialog(this,
                "任务完成！你的得分是：" + score + " / 8\n",
                "完成任务", JOptionPane.INFORMATION_MESSAGE);
        mainFrame.showPanel("HOME");
    }

    // 8. 鼓励语
    @Override
    protected String getEncouragement() {
        return "干得漂亮！";
    }
}