package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

/**
 * 💡AbstractTaskPanel：所有任务面板的抽象基类，封装统一的布局与按钮控制逻辑。
 */
public abstract class AbstractTaskPanel extends JPanel {

    // 1. 变量初始化

        // 1） 基本属性字段 ====

        // 主窗口引用（用于页面跳转）
        protected MainFrame mainFrame;

        // 任务关联的年级与ID（用于存档与任务区分）
        protected int grade;//任务对应年级
        protected String taskId;//任务唯一标识符

        // 尝试次数、得分、完成状态记录（用于任务进度管理）
        protected int attemptCount = 0;
        protected int score = 0;
        protected boolean taskFinished = false;


        // 2） UI 组件定义 ====

        protected JLabel instructionLabel;     // 任务标题或说明
        protected JPanel contentPanel;         // 子类添加具体任务内容的面板
        protected JButton submitButton;        // 提交答案按钮
        protected JButton endSessionButton;    // 结束任务按钮
        protected JButton homeButton;          // 返回首页按钮




    // 2. 构造方法

    public AbstractTaskPanel(MainFrame mainFrame, int grade, String taskId) {
        // 1.初始化类的基本属性字段
        this.mainFrame = mainFrame;
        this.grade = grade;
        this.taskId = taskId;

        // 2.设置当前任务面板的基本布局和边距
        setLayout(new BorderLayout()); // 使用边界布局：上下左右中
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // 内边距
        setOpaque(false); // 设置背景透明

        // 2.调用方法，初始化并排列各组件
        initComponents();    // 创建按钮与说明区域
        layoutComponents();  // 组装到面板结构中

        startTask(); // 启动任务（由子类实现）⚠️，前面不是已经实现了吗？
    }





    // 3. 方法实现与抽象

    // 1）初始化组件方法——初始化任务面板的所有基本控件

    protected void initComponents() {

        //1. 顶部标题与底部按钮
            // 顶部任务说明文字（由子类提供标题）
            instructionLabel = StyleUtils.createInstructionLabel(getTaskTitle());

            // 提交按钮（绑定提交操作）
            submitButton = StyleUtils.createStyledButton("提交答案");
            submitButton.addActionListener(e -> onSubmit());

            // 结束会话按钮（绑定任务结束逻辑）
            endSessionButton = StyleUtils.createStyledButton("结束会话");
            endSessionButton.addActionListener(e -> endTaskSession());

            // 返回首页按钮（保存分数并跳转）
            homeButton = StyleUtils.createStyledButton("Home");
            homeButton.addActionListener(e -> {
                ProgressTracker.saveProgress(grade, taskId, score); // 存档
                mainFrame.returnToHome(); // 回首页
            });

        //2. 中间内容面板（子类添加组件的容器）
        contentPanel = StyleUtils.createBubblePanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));// 垂直排列
    }



    // 2）组件布局方法——与上面的 1）内组件一一对应
    private void layoutComponents() {

        // 顶部区域组合（说明 + 返回按钮）
        JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(instructionLabel, BorderLayout.CENTER); // 左：说明文字
            top.add(homeButton, BorderLayout.EAST);       // 右：返回按钮

        // 底部区域组合（提交 + 结束）
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));//就是从左往右一个个放，排列风格设置为靠右
            bottom.setOpaque(false);//设置为透明
            bottom.add(endSessionButton);//结束对话
            bottom.add(submitButton);//任务提交

        // 中部区域不需要再组合，具体的内容放置在子类中实现

        // 把上面三组合起来，放到当前任务面板里
        this.add(top, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER); // 直接放入中部
        this.add(bottom, BorderLayout.SOUTH);
    }



    // 3）直接提供 1）中建立组件需要的方法的具体实现，即所有任务面板都通用的一些控制行为

        // 结束当前任务：调用子类保存逻辑 + 回首页
        protected void endTaskSession() {
            if (!taskFinished) {
                taskFinished = true;
                saveAndFinish(); // 交由子类实现具体保存与反馈
            }
            mainFrame.returnToHome(); // 无论如何都回首页
        }

        // 根据得分返回一句鼓励语（用于反馈）
        protected String getEncouragement(int score) {
            if (score >= 5) return "太棒了，继续保持！";
            if (score >= 3) return "不错哦，可以再练练！";
            return "没关系，重来一次就会更好～";
        }





    // 4）提供 1）中建立组件需要的方法的抽象方法定义（具体交给子类实现）

        // 子类实现：返回当前任务的说明标题
        protected abstract String getTaskTitle();

        // 子类实现：初始化任务逻辑
        protected abstract void startTask();

        // 子类实现：提交答案后的处理逻辑
        protected abstract void onSubmit();

        // 子类实现：保存得分、弹窗反馈等结束操作
        protected abstract void saveAndFinish();


}