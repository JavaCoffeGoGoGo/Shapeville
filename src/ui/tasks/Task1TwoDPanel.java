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
 * Task1TwoDPanel：图形识别任务面板，实现基础积分等级的图形识别逻辑。
 * 用户观看图形图像，输入其名称，总共11个图形，每个图形最多尝试 3 次。
 */
public class Task1TwoDPanel extends AbstractTaskPanel {

    //1. 变量初始化
        // 1） 基本变量
            //全部图形名称的列表
            private static final String[] SHAPES = {
                    "circle", "heptagon", "hexagon", "kite", "octagon", "oval",
                    "pentagon", "rectangle", "rhombus", "square", "triangle"
            };
            //待识别图形列表
            private List<String> shapeList;
            //当前图形
            private String currentShape;

        // 2） UI 组件
            // 展示图形的图片
            private JLabel imageLabel;
            // 用户输入图形名称的输入框
            private JTextField answerField;
            // 反馈信息，比如“正确啦”、“再试试”
            private JLabel feedbackLabel;

    public Task1TwoDPanel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }


    //3.子类实现方法

        // 1）明确任务标题
        @Override
        protected String getTaskTitle() {
            return "平面图形识别：请输入图形名称（共11轮）";
        }

        // 2）开始任务
        @Override

        protected void startTask() {
            // 初始化状态
            round = 0;
            score = 0;
            // 每题最多重试次数
            attemptsLeft = 3;
            //待识别图形列表
            shapeList = new ArrayList<>(Arrays.asList(SHAPES));
            //打乱列表顺序，用于随机抽题
            Collections.shuffle(shapeList);
            //调用 loadNextShape() 加载第一题
            loadNextShape();
        }
        // 3）加载题目

            // 1.主方法——加载题目
            private void loadNextShape() {

                //1. 当已经做完 11 个题或者图形列表为空时，就直接结束任务
                if (round >= 11 || shapeList.isEmpty()) {
                    //弹出提示+保存成绩
                    saveAndFinish();
                    return;
                }
                //2.如果没有做完，更新当前任务状态

                // currentShape：从 shapeList 中取出一个没有展示过的图形（并且从列表里删除它）
                currentShape = shapeList.remove(0);// 每轮最多尝试三次
                // 进入新一轮，重置剩余尝试次数
                attemptsLeft = 3;
                round++;
                //3.添加组件并刷新
                // 1）先把 contentPanel 里面原来的组件全部清空
                contentPanel.removeAll();

                // 2）新建组件

                    // 1. 加载图像标签
                        // 读取图像，新建标签
                        imageLabel = new JLabel(loadShapeImage(currentShape));
                        // 让图片在面板中央显示
                        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    //2. 创建答案输入框
                        answerField = new JTextField(30);
                        answerField.setMaximumSize(new Dimension(200, 30));
                        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

                    //3. 创建反馈标签
                        feedbackLabel = new JLabel(" ");
                        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        feedbackLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

                // 3）添加组件到contentPanel
                    contentPanel.add(Box.createVerticalStrut(20));
                    contentPanel.add(imageLabel);
                    contentPanel.add(Box.createVerticalStrut(20));
                    contentPanel.add(answerField);
                    contentPanel.add(Box.createVerticalStrut(10));
                    contentPanel.add(feedbackLabel);

                // 4）并立刻刷新
                    // 调用 revalidate() 和 repaint() 刷新界面，以便立即显示新题
                    contentPanel.revalidate();
                    contentPanel.repaint();

                // 5)添加返回图形选择界面按钮
                    // 先移除旧按钮
                    if (backShapeButton != null && Arrays.asList(bottom.getComponents()).contains(backShapeButton)) {
                        bottom.remove(backShapeButton);
                    }
                    // 创建新按钮并添加
                        backShapeButton = StyleUtils.createStyledButton("返回图形选择界面");
                        backShapeButton.addActionListener(e -> {
                            // 保存当前进度
                            ProgressTracker.saveProgress(grade, taskId, score);
                            // 切回到 TaskSelectorPanel
                            mainFrame.showPanel("g12_shape");
                        });
                        bottom.add(backShapeButton);
                    // 刷新 bottom 面板
                    bottom.revalidate();
                    bottom.repaint();

            }
            // 2. 子方法一 ——先实现图片加载
            private Icon loadShapeImage(String shapeName) {
                String path = "/images/task1TwoD/" + shapeName + ".png";
                try (InputStream stream = getClass().getResourceAsStream(path)) {
                    if (stream != null) {
                        BufferedImage original = ImageIO.read(stream);

                        // 目标高度
                        int targetHeight = 400;
                        int originalWidth = original.getWidth();
                        int originalHeight = original.getHeight();

                        // 计算等比例缩放后的宽度
                        double scale = (double) targetHeight / originalHeight;
                        int scaledWidth = (int) (originalWidth * scale);
                        int scaledHeight = targetHeight;

                        // 创建带透明背景的新图像
                        BufferedImage roundedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = roundedImage.createGraphics();

                        // 启用抗锯齿和高质量渲染
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                        // 创建圆角剪切区域
                        int arc = 20;
                        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, scaledWidth, scaledHeight, arc, arc);
                        g2.setClip(roundedRect);

                        // 绘制缩放后的图像
                        g2.drawImage(original.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), 0, 0, null);

                        // 绘制浅灰色描边
                        g2.setClip(null); // 移除剪切区域以绘制边框
                        g2.setColor(new Color(200, 200, 200));
                        g2.setStroke(new BasicStroke(1.2f));
                        g2.draw(roundedRect);

                        g2.dispose();
                        return new ImageIcon(roundedImage);
                    } else {
                        return new ImageIcon(); // 如果流为空，返回空图像
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ImageIcon(); // 读取失败，返回空图像
                }
            }
            // 3.子方法二——实现满轮退出
            @Override
            protected void saveAndFinish() {

                // 保存进度
                ProgressTracker.saveProgress(grade, taskId, score);

                // 弹出得分提示框（点击确定或叉号都会继续执行下面代码）
                JOptionPane.showMessageDialog(this,
                        "任务完成！你的得分是：" + score + " / 11\n",
                        "完成任务", JOptionPane.INFORMATION_MESSAGE);

                // 返回主界面
                mainFrame.showPanel("HOME");
            }

    //4）提交答案交互处理

        // 1.主方法
        @Override
        protected void onSubmit() {
            // 如果尝试次数已用尽，直接返回，不再响应提交
            if (attemptsLeft <= 0) return;

            String userInput = answerField.getText().trim().toLowerCase();
            if (userInput.isEmpty()) {
                feedbackLabel.setText("当前答案为空哦！");
                return;
            }

            // 先减去一次机会
            attemptsLeft--;

            // 计算这是第几次尝试：Basic 模式下，第一次尝试时 attemptsLeft 从 3 -> 2，
            // 所以 attemptNum = 3 - attemptsLeft
            int attemptNum = 3 - attemptsLeft;

            // 答对了
            if (userInput.equals(currentShape)) {
                // 根据 Basic 规则给分
                int delta = GradingSystem.grade(attemptNum, /*isAdvanced=*/false);
                score += delta;

                feedbackLabel.setText("✅ 正确！本次得分：" + delta + "，累计：" + score);
                attemptCount++;
                submitButton.setEnabled(false); // 防止重复提交

                // 设置 1 秒后自动进入下一题
                Timer timer = new Timer(1000, e -> {
                    submitButton.setEnabled(true);
                    loadNextShape();
                });
                timer.setRepeats(false);
                timer.start();

            } else {
                // 答错，但还剩机会
                if (attemptsLeft > 0) {
                    feedbackLabel.setText("❌ 错误！再试一次～");
                } else {
                    // 最后一次也错了，显示正确答案（得分为 0）
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
        //2.子方法：反馈信息
        @Override
        protected String getEncouragement() {
            return "干得漂亮！";
        }


}