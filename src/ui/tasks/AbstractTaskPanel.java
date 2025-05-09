package ui.tasks;

import ui.MainFrame;
import util.StyleUtils;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractTaskPanel extends JPanel {

    // 1. 变量初始化
        // 1） 基本属性
            // 主窗口引用（用于页面跳转）
            protected MainFrame mainFrame;

            // 任务关联的年级与ID（用于存档与任务区分）
            protected int grade;  // 任务对应年级
            protected String taskId;  // 任务唯一标识符

            // 尝试次数、得分（用于任务进度管理）
            protected int attemptCount = 0;
            protected int attemptsLeft = 3;
            protected int score = 0;
            protected int round = 0;

        // 2） UI 组件定义 ====
            protected JLabel instructionLabel;  // 任务标题或说明
            protected JPanel contentPanel;      // 子类添加具体任务内容的面板
            protected JPanel bottom;
            protected JButton submitButton;     // 提交答案按钮
            protected JButton homeButton;       // 返回首页按钮
            protected JButton backButton;
            protected JButton backShapeButton; // 返回任务选择界面按钮


    // 2. 构造方法
        public AbstractTaskPanel(MainFrame mainFrame, int grade, String taskId) {
            // 1）初始化类的基本属性字段
            this.mainFrame = mainFrame;
            this.grade = grade;
            this.taskId = taskId;

            // 2）设置当前任务面板的基本布局和边距
            setLayout(new BorderLayout(20,20));  // 使用边界布局：上下左右中
            setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));  // 内边距
            setOpaque(false);  // 设置背景透明

            // 3）调用方法，初始化并排列各组件
            initComponents();    // 创建按钮与说明区域
            layoutComponents();  // 组装到面板结构中

            startTask(); // 启动任务（由子类实现）
        }


    // 3. 方法实现与抽象

        // 1）初始化组件方法——初始化任务面板的所有基本控件

        protected void initComponents() {
            // 1. 顶部标题与底部按钮

                // 1）顶部任务说明文字
                instructionLabel = StyleUtils.createTitleLabel(getTaskTitle());

                // 2）提交按钮
                submitButton = StyleUtils.createStyledButton("提交答案");
                submitButton.addActionListener(e -> onSubmit());

                // 3）返回任务选择界面按钮
                            backButton = StyleUtils.createStyledButton("返回任务选择界面");
                            backButton.addActionListener(e -> {
                                int option = StyleUtils.showStyledConfirmDialog(
                                        this,
                                        "退出确认",
                                        "确认中途退出吗？\n当前进度将不会保存。"
                                );
                                if (option == JOptionPane.YES_OPTION) {
                                    mainFrame.showPanel("SELECTOR");
                                }
                            });

                // 4）返回首页按钮
                            homeButton = StyleUtils.createStyledButton("Home");
                            homeButton.addActionListener(e -> {
                                int option = StyleUtils.showStyledConfirmDialog(
                                        this,
                                        "退出确认",
                                        "确认中途退出吗？\n当前进度将不会保存。"
                                );
                                if (option == JOptionPane.YES_OPTION) {
                                    mainFrame.returnToHome();
                                }
            });

            // 2. 中间内容面板（子类添加组件的容器）
            contentPanel = StyleUtils.createBubblePanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));  // 垂直排列
        }

        // 2）组件布局方法——把刚才初始化的组件进行组合并添加到当前面板
        private void layoutComponents() {

            // 1. 顶部区域组合（说明 + 返回按钮）
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(instructionLabel, BorderLayout.CENTER);

            // 2. 底部区域组合（提交）
            bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));  // 排列风格设置为靠右
            bottom.setOpaque(false);  // 设置为透明
            bottom.add(submitButton);  // 任务提交
            bottom.add(backButton);
            bottom.add(homeButton, BorderLayout.EAST);

            // 3. 把三部分组件放到当前任务面板里
            this.add(top, BorderLayout.NORTH);
            this.add(contentPanel, BorderLayout.CENTER);  // 直接放入中部
            this.add(bottom, BorderLayout.SOUTH);
        }



        // 4）提供初始化组件需要的方法的抽象方法定义（具体交给子类实现）

            // 1. 子类实现：返回当前任务的说明标题
            protected abstract String getTaskTitle();

            // 2. 子类实现：提交答案后的处理逻辑
            protected abstract void onSubmit();

            // 3. 子类实现：保存得分、弹窗反馈等结束操作
            protected abstract void saveAndFinish();

            // 4.子类实现：返回鼓励话语
            protected abstract String getEncouragement();

            // 5. 子类实现：初始化任务逻辑
            protected abstract void startTask();
}