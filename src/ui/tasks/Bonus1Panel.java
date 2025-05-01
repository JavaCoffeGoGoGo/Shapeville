package ui.tasks;

//说明：
//        •	CompositeShapeConfig 封装复合图形的名称、图片路径、构成部分参数、公式说明等，并提供 calculateArea() 和 getFormulaExplanation() 方法。
//        •	使用 TimerUtils 进行全局倒计时管理，并在回调中更新 timerLabel 文本。
//        •	三次失败或时间耗尽后，调用 showSolution() 展示详细解法，再进入下一题。
//        •	继承 AbstractTaskPanel，自动拥有统一的导航、结束会话、得分保存逻辑。
// src/ui/tasks/Bonus1Panel.java

import data.CompositeShapeConfig;

public class Bonus1Panel extends AbstractTaskPanel {
    private JLabel compositeImageLabel;
    private JTextField answerField;
    private JLabel timerLabel;

    // 预定义 9 个复合图形配置
    private final List<CompositeShapeConfig> shapes = List.of(
            new CompositeShapeConfig("形状1", "composite/shape1.png", /* params */),
            new CompositeShapeConfig("形状2", "composite/shape2.png", /* params */),
            // … 共 9 个
    );
    private int currentIndex = 0;

    public Bonus1Panel(MainFrame mainFrame, int grade) {
        super(mainFrame, grade, "Bonus1");
    }

    @Override
    protected String getTaskTitle() {
        return "进阶任务1：复合图形计算";
    }

    @Override
    protected void startTask() {
        contentPanel.removeAll();
        // 显示倒计时标签
        timerLabel = new JLabel("剩余时间：5:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contentPanel.add(timerLabel);

        // 显示第一张复合图形
        compositeImageLabel = new JLabel();
        CompositeShapeConfig cfg = shapes.get(currentIndex);
        compositeImageLabel.setIcon(ImageUtils.loadIcon(cfg.getImagePath(), 300, 300));
        contentPanel.add(compositeImageLabel);

        // 输入框与提交按钮
        answerField = new JTextField(10);
        answerField.setFont(new Font("Arial", Font.PLAIN, 24));
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("请输入组合图形面积："));
        inputPanel.add(answerField);
        contentPanel.add(inputPanel);

        // 启动5分钟倒计时
        TimerUtils.startCountdown(300, remaining -> {
            timerLabel.setText("剩余时间：" + formatTime(remaining));
            if (remaining <= 0) {
                // 时间到，直接展示解析并下一题
                showSolutionAndNext();
            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void onSubmit() {
        attemptCount++;
        String input = answerField.getText().trim();
        CompositeShapeConfig cfg = shapes.get(currentIndex);
        double correctArea = cfg.calculateArea();  // 组合部分面积累加

        try {
            double userArea = Double.parseDouble(input);
            if (Math.abs(userArea - correctArea) < 0.1) {
                score++;
                JOptionPane.showMessageDialog(this, "回答正确！得分 +1");
            } else {
                if (attemptCount < 3) {
                    JOptionPane.showMessageDialog(this, "错误！请再试一次");
                    return;
                }
                // 三次失败后展示解法
                showSolution(cfg, correctArea);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效数字！");
            return;
        }

        // 本题完成后进入下一题
        nextQuestion();
    }

    // 格式化剩余时间 mm:ss
    private String formatTime(int seconds) {
        int m = seconds / 60, s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    // 失败三次后展示公式与解法
    private void showSolution(CompositeShapeConfig cfg, double correctArea) {
        String detail = cfg.getFormulaExplanation() +
                "\n正确答案：" + String.format("%.1f", correctArea);
        JOptionPane.showMessageDialog(this, detail, "解题解析", JOptionPane.INFORMATION_MESSAGE);
    }

    // 时间到或答题后统一调用
    private void nextQuestion() {
        // 停止当前计时器并重置
        TimerUtils.stopCountdown();
        currentIndex++;
        attemptCount = 0;
        answerField.setText("");

        if (currentIndex >= shapes.size()) {
            endTaskSession();
        } else {
            startTask();  // 重新渲染下一题
        }
    }

    // 时间到自动展示并进入下题
    private void showSolutionAndNext() {
        CompositeShapeConfig cfg = shapes.get(currentIndex);
        showSolution(cfg, cfg.calculateArea());
        nextQuestion();
    }
}
