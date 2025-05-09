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
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Task1ThreeDPanel：立体图形识别任务面板，实现基础积分等级的立体图形识别逻辑。
 * 用户观看立体图形图像，输入其名称，总共8个图形，每个图形最多尝试3次。
 */
public class Task1ThreeDPanel extends AbstractTaskPanel {

    // 1. 变量初始化
    // 1） 基本变量
    private static final String[] SHAPES = {
            "cone", "cube", "cuboid", "cylinder", "sphere",
            "squarebasedpyramid", "tetrahedron", "triangularprism"
    };
    private List<String> shapeList;
    private String currentShape;

    // 2） UI 组件
    private JLabel imageLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;

    public Task1ThreeDPanel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
        // 隐藏默认的返回按钮
        backButton.setVisible(false);

        // 重排底部按钮：submit、返回、home
        bottom.remove(homeButton);
        bottom.remove(backShapeButton);
        bottom.add(submitButton);
        bottom.add(backShapeButton);
        bottom.add(homeButton);
        bottom.revalidate();
        bottom.repaint();

        // 统一反馈标签样式
        feedbackLabel.setFont(StyleUtils.DEFAULT_FONT);
        feedbackLabel.setForeground(StyleUtils.TEXT_COLOR);
    }

    // 2. 子类实现方法

    // 2.1）任务标题
    @Override
    protected String getTaskTitle() {
        return "立体图形识别：请输入图形名称（共8轮）";
    }

    // 2.2）开始任务
    @Override
    protected void startTask() {
        round = 0;
        score = 0;
        attemptsLeft = 3;
        shapeList = new ArrayList<>(Arrays.asList(SHAPES));
        Collections.shuffle(shapeList);
        loadNextShape();
    }

    // 2.3）加载题目
    private void loadNextShape() {
        // 1）检查任务结束
        if (round >= 8 || shapeList.isEmpty()) {
            saveAndFinish();
            return;
        }

        // 2）准备本轮数据
        currentShape = shapeList.remove(0);
        attemptsLeft = 3;
        round++;

        // 3）清空旧组件
        contentPanel.removeAll();

        // 4）创建并配置新组件
        // 4.1 加载并显示图像
        imageLabel = new JLabel(loadShapeImage(currentShape));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4.2 答案输入框：圆角、1px边框、统一字体和颜色
        int imgW = imageLabel.getIcon().getIconWidth();
        int h = 32;
        answerField = StyleUtils.createRoundedTextField(imgW, h);
        answerField.setBorder(BorderFactory.createLineBorder(StyleUtils.TITLE_COLOR, 1));
        answerField.setFont(StyleUtils.DEFAULT_FONT);
        answerField.setForeground(StyleUtils.TEXT_COLOR);
        answerField.setBackground(Color.WHITE);
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4.3 反馈标签
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 5）添加到 contentPanel
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(feedbackLabel);

        // 6）刷新界面
        contentPanel.revalidate();
        contentPanel.repaint();

        // 7）返回按钮（移除旧的再添加）
        if (backShapeButton != null && Arrays.asList(bottom.getComponents()).contains(backShapeButton)) {
            bottom.remove(backShapeButton);
        }
        backShapeButton = StyleUtils.createStyledButton("返回上一界面");
        backShapeButton.addActionListener(e -> {
            int option = StyleUtils.showStyledConfirmDialog(
                    this,
                    "退出确认",
                    "确认中途退出吗？\n当前进度将不会保存。"
            );
            if (option == JOptionPane.YES_OPTION) {
                mainFrame.showPanel("g12_shape");
            }
        });
        bottom.add(backShapeButton);
        bottom.revalidate();
        bottom.repaint();
    }

    // 2.4）图像加载：圆角 & 抗锯齿
    private Icon loadShapeImage(String shapeName) {
        String path = "/images/task1ThreeD/" + shapeName + ".png";
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                BufferedImage original = ImageIO.read(stream);
                int targetHeight = 400;
                int origW = original.getWidth();
                int origH = original.getHeight();
                double scale = (double) targetHeight / origH;
                int scaledW = (int) (origW * scale);

                BufferedImage roundedImage = new BufferedImage(scaledW, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = roundedImage.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int arc = 20;
                RoundRectangle2D clip = new RoundRectangle2D.Float(0, 0, scaledW, targetHeight, arc, arc);
                g2.setClip(clip);
                g2.drawImage(original.getScaledInstance(scaledW, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);

                g2.setClip(null);
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(clip);
                g2.dispose();

                return new ImageIcon(roundedImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageIcon();
    }

    // 2.5）提交处理
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
            int delta = GradingSystem.grade(attemptNum, /*isAdvanced=*/false);
            score += delta;
            feedbackLabel.setText("正确！干的漂亮～");
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
                feedbackLabel.setText("错误！再试一次～");
            } else {
                feedbackLabel.setText("正确答案是：" + currentShape + "（得分 0）");
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

    // 2.6）结束任务
    @Override
    protected void saveAndFinish() {
        ProgressTracker.saveProgress(grade, taskId, score);
        // 应用全局样式
        StyleUtils.applyGlobalStyle(SwingUtilities.getWindowAncestor(this));
        JOptionPane.showMessageDialog(
                this,
                "任务完成！你的得分是：" + score + " / 8\n\n",
                "完成任务",
                JOptionPane.INFORMATION_MESSAGE
        );
        mainFrame.showPanel("HOME");
    }

    // 2.7）鼓励语
    @Override
    protected String getEncouragement() {
        return "干得漂亮！";
    }
}