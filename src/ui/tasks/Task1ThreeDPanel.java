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
 * Task1ThreeDPanel：图形识别任务面板，实现进阶积分等级的立体图形识别逻辑。
 * 用户观看图形图像，输入其名称，总共 8 个图形，每个图形最多尝试 3 次。
 */
public class Task1ThreeDPanel extends AbstractTaskPanel {

    // 1. 资源准备
    // 1）图形资源
    private static final String[] SHAPES = {
            "cone", "L Cube", "6° cuboid", "cylinder", "sphere",
            "squareBasedPyramid", "tetrahedron", "triangularPrism"
    };

    // 任务运行时动态生成的待识别图形列表
    private List<String> shapeList;

    // 当前展示的图形名称（用来判断答案对不对）
    private String currentShape;

    // 当前进行到第几轮（最多8轮）
    private int round = 0;

    // 当前题目剩余尝试次数
    private int attemptsLeft = 3;

    // 2）UI 组件
    // 展示图形的图片
    private JLabel imageLabel;
    // 用户输入图形名称的输入框
    private JTextField answerField;
    // 反馈信息，比如“正确啦”、“再试试”
    private JLabel feedbackLabel;

    public Task1ThreeDPanel(MainFrame mainFrame, int grade, String taskId) {
        // 直接把 mainFrame、grade 和 taskId 交给了父类 AbstractTaskPanel 的构造方法
        // 初始化布局、按钮逻辑等，都是在父类完成的
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "立体图形识别：请输入图形名称（共8个）";
    }

    @Override
    protected void startTask() {
        //先复制出一份图形列表，方便后面逐个展示
        shapeList = new ArrayList<>(Arrays.asList(SHAPES));
        //Collections.shuffle() 是 Java 标准库的方法，可以把一个列表打乱顺序，用于随机抽题
        Collections.shuffle(shapeList);
        //初始化轮次和得分
        round = 0;
        score = 0;
        //然后马上调用 loadNextShape() 加载第一题
        loadNextShape(); // 加载第一个图形
    }

    // 加载下一题图形 & 刷新界面
    private void loadNextShape() {
        // 1. 当已经做完 8 个题或者图形列表为空时，就直接结束任务
        if (round >= 8 || shapeList.isEmpty()) {
            // 弹出提示+保存成绩
            saveAndFinish();
            return;
        }

        // 2. 更新当前任务状态
        // currentShape：从 shapeList 中取出一个没有展示过的图形（并且从列表里删除它）
        currentShape = shapeList.remove(0); // 取出一个未展示的图形
        attemptsLeft = 3;                   // 每轮最多尝试三次
        round++;

        // 3. 先把 contentPanel 里面原来的组件全部清空
        contentPanel.removeAll();

        // 4. 加载图像
        imageLabel = new JLabel(loadShapeImage(currentShape));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 5. 创建答案输入框
        answerField = new JTextField(30);
        answerField.setMaximumSize(new Dimension(200, 30));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 6. 创建反馈标签
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 7. 把刚才三个建好的组件加入 contentPanel，并立刻刷新
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(answerField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(feedbackLabel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 加载指定图形的图像资源
    private Icon loadShapeImage(String shapeName) {
        String path = "/images/task1ThreeD/" + shapeName + ".png"; // 使用正确的路径

        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                BufferedImage img = ImageIO.read(stream);
                return new ImageIcon(img);
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
        // 如果尝试次数已用尽，直接返回，不再响应提交
        if (attemptsLeft <= 0) return;

        String userInput = answerField.getText().trim().toLowerCase(); // 去除前后空格后将输入转为小写，统一格式
        if (userInput.isEmpty()) { // 若输入为空，给予提示信息并返回
            feedbackLabel.setText("请输入你的答案！");
            return;
        }

        // 答对了
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

        } else { // 答错了
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
        ProgressTracker.saveProgress(grade, taskId, score);
        JOptionPane.showMessageDialog(this,
                "任务完成！你的得分是：" + score + " / 8\n" + getEncouragement(score),
                "完成任务", JOptionPane.INFORMATION_MESSAGE);
    }
}