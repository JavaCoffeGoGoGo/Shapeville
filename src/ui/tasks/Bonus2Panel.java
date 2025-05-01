package ui.tasks;

//	•	FanShapeConfig 包含扇形的半径、圆心角、图片路径，并提供：
//	•	calculateArea(): 返回扇形面积（\pi r^2 \times \theta/360）。
//	•	calculateArcLength(): 返回弧长（2\pi r \times \theta/360）。
//	•	getAreaFormula() / getArcLengthFormula(): 返回带入具体数值的公式字符串。
//	•	使用 TimerUtils 控制 5 分钟倒计时；倒计时结束后自动展示解析并进入下题。
//	•	3 次错误后，通过 showSolution() 展示公式与正确值，再调用 nextQuestion() 继续。
//	•	继承自 AbstractTaskPanel，统一了导航、评分和结束逻辑。
// src/ui/tasks/Bonus2Panel.java

import data.FanShapeConfig;

public class Bonus2Panel extends AbstractTaskPanel {
    private JLabel fanImageLabel;
    private JComboBox<String> taskTypeComboBox;  // “面积” 或 “弧长”
    private JTextField answerField;
    private JLabel timerLabel;

    // 8 个扇形图配置
    private final List<FanShapeConfig> fans = List.of(
            new FanShapeConfig("扇形1", "fans/fan1.png", 5.0, 60.0),
            new FanShapeConfig("扇形2", "fans/fan2.png", 7.0, 90.0),
            // … 共 8 个配置（半径、圆心角）
    );
    private int currentIndex = 0;

    public Bonus2Panel(MainFrame mainFrame, int grade) {
        super(mainFrame, grade, "Bonus2");
    }

    @Override
    protected String getTaskTitle() {
        return "进阶任务2：扇形计算";
    }

    @Override
    protected void startTask() {
        contentPanel.removeAll();

        // 倒计时标签
        timerLabel = new JLabel("剩余时间：05:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contentPanel.add(timerLabel);

        // 当前扇形图
        FanShapeConfig cfg = fans.get(currentIndex);
        fanImageLabel = new JLabel();
        fanImageLabel.setIcon(ImageUtils.loadIcon(cfg.getImagePath(), 300, 300));
        contentPanel.add(fanImageLabel);

        // 选择题型：面积 or 弧长
        taskTypeComboBox = new JComboBox<>(new String[]{"面积", "弧长"});
        taskTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));

        // 输入框
        answerField = new JTextField(8);
        answerField.setFont(new Font("Arial", Font.PLAIN, 24));

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("请选择计算类型："));
        inputPanel.add(taskTypeComboBox);
        inputPanel.add(new JLabel("请输入结果（保留1位小数）："));
        inputPanel.add(answerField);
        contentPanel.add(inputPanel);

        // 启动 5 分钟倒计时
        TimerUtils.startCountdown(300, remaining -> {
            timerLabel.setText("剩余时间：" + formatTime(remaining));
            if (remaining <= 0) {
                showSolutionAndNext();
            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void onSubmit() {
        attemptCount++;
        FanShapeConfig cfg = fans.get(currentIndex);
        String type = (String) taskTypeComboBox.getSelectedItem();
        double correctValue = type.equals("面积")
                ? cfg.calculateArea()
                : cfg.calculateArcLength();

        String input = answerField.getText().trim();
        try {
            double userValue = Double.parseDouble(input);
            if (Math.abs(userValue - correctValue) < 0.1) {
                score++;
                JOptionPane.showMessageDialog(this, "回答正确！得分 +1");
                nextQuestion();
                return;
            } else if (attemptCount < 3) {
                JOptionPane.showMessageDialog(this, "错误！请再试一次");
                return;
            }
            // 3 次错误后展示解法
            showSolution(cfg, type, correctValue);
            nextQuestion();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效数字！");
        }
    }

    private String formatTime(int sec) {
        int m = sec / 60, s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void showSolution(FanShapeConfig cfg, String type, double correctValue) {
        String formula = type.equals("面积")
                ? cfg.getAreaFormula()
                : cfg.getArcLengthFormula();
        String detail = formula
                + "\n正确值：" + String.format("%.1f", correctValue);
        JOptionPane.showMessageDialog(this, detail, "解题解析",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void nextQuestion() {
        TimerUtils.stopCountdown();
        currentIndex++;
        attemptCount = 0;
        answerField.setText("");
        if (currentIndex >= fans.size()) {
            endTaskSession();
        } else {
            startTask();
        }
    }

    private void showSolutionAndNext() {
        FanShapeConfig cfg = fans.get(currentIndex);
        // 默认展示面积与弧长两种公式
        String detail = cfg.getAreaFormula() + "\n" + cfg.getArcLengthFormula()
                + "\n正确面积：" + String.format("%.1f", cfg.calculateArea())
                + "\n正确弧长：" + String.format("%.1f", cfg.calculateArcLength());
        JOptionPane.showMessageDialog(this, detail, "时间到，解题解析",
                JOptionPane.INFORMATION_MESSAGE);
        nextQuestion();
    }
}
