package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

/**
 * 💡AbstractTaskPanel：所有任务面板的抽象基类，封装统一的布局与按钮控制逻辑。
 *
 * 子类只需关注自身的核心任务逻辑（如题目生成、答案提交、分数保存等），
 * 不需重复编写通用的界面结构和交互操作。
 */
public abstract class AbstractTaskPanel extends JPanel {

    // ==== 🧱 1. 基本属性字段 ====

    // 👉 所属主窗口引用（用于页面跳转）
    protected MainFrame mainFrame;

    // 👉 当前任务关联的年级与ID（用于存档与任务区分）
    protected int grade;
    protected String taskId;

    // 👉 尝试次数、得分、完成状态记录（用于进度追踪）
    protected int attemptCount = 0;
    protected int score = 0;
    protected boolean taskFinished = false;


    // ==== 🎛️ 2. UI 组件定义 ====

    protected JLabel instructionLabel;     // 任务标题或说明
    protected JPanel contentPanel;         // 子类添加具体任务内容的面板
    protected JButton submitButton;        // 提交答案按钮
    protected JButton endSessionButton;    // 结束任务按钮
    protected JButton homeButton;          // 返回首页按钮







    // ==== 🏗️ 3. 构造方法 ====

    public AbstractTaskPanel(MainFrame mainFrame, int grade, String taskId) {
        this.mainFrame = mainFrame;
        this.grade = grade;
        this.taskId = taskId;

        // 💡 设置当前任务面板的基本布局和边距
        setLayout(new BorderLayout()); // 使用边界布局：上下左右中
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // 内边距
        setOpaque(false); // 设置背景透明

        // ⚙️ 初始化并排列各组件
        initComponents();    // 创建按钮与说明区域
        layoutComponents();  // 组装到面板结构中

        startTask(); // 🎯 启动任务（由子类实现）
    }







    // ==== 🔧 4. 初始化组件方法 ====

    /** 初始化任务面板的所有基本控件 */
    protected void initComponents() {
        // 🏷️ 顶部任务说明文字（由子类提供标题）
        instructionLabel = StyleUtils.createInstructionLabel(getTaskTitle());

        // ✅ 提交按钮（绑定提交操作）
        submitButton = StyleUtils.createStyledButton("提交答案");
        submitButton.addActionListener(e -> onSubmit());

        // ❌ 结束会话按钮（绑定任务结束逻辑）
        endSessionButton = StyleUtils.createStyledButton("结束会话");
        endSessionButton.addActionListener(e -> endTaskSession());

        // 🏠 返回首页按钮（保存分数并跳转）
        homeButton = StyleUtils.createStyledButton("Home");
        homeButton.addActionListener(e -> {
            ProgressTracker.saveProgress(grade, taskId, score); // 存档
            mainFrame.returnToHome(); // 回首页
        });

        // 📦 内容面板（子类添加组件的容器）
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 垂直排列
    }



    // ==== 📐 5. 组件布局方法 ====

    /** 将各组件布局到任务面板结构中（上下结构） */
    private void layoutComponents() {
        // 🟦 顶部区域（说明 + 返回按钮）
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(instructionLabel, BorderLayout.WEST); // 左：说明文字
        top.add(homeButton, BorderLayout.EAST);       // 右：返回按钮

        // 🟥 底部区域（提交 + 结束）
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(endSessionButton);
        bottom.add(submitButton);

        // 📤 中部区域（题目内容，滚动容器包裹）
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER); // 加滚动条支持长题目
        add(bottom, BorderLayout.SOUTH);
    }



    // ==== ⏹️ 6. 通用任务控制方法 ====

    /** 结束当前任务：调用子类保存逻辑 + 回首页 */
    protected void endTaskSession() {
        if (!taskFinished) {
            taskFinished = true;
            saveAndFinish(); // 交由子类实现具体保存与反馈
        }
        mainFrame.returnToHome(); // 无论如何都回首页
    }

    /** 根据得分返回一句鼓励语（用于反馈） */
    protected String getEncouragement(int score) {
        if (score >= 5) return "太棒了，继续保持！";
        if (score >= 3) return "不错哦，可以再练练！";
        return "没关系，重来一次就会更好～";
    }





    // ==== 📌 7. 抽象方法定义（交给子类实现） ====

    /** 子类实现：返回当前任务的说明标题 */
    protected abstract String getTaskTitle();

    /** 子类实现：初始化任务逻辑（如生成题目） */
    protected abstract void startTask();

    /** 子类实现：提交答案后的处理逻辑 */
    protected abstract void onSubmit();

    /** 子类实现：保存得分、弹窗反馈等结束操作 */
    protected abstract void saveAndFinish();


}