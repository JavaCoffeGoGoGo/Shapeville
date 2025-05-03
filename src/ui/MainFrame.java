package ui;

import logic.TaskRegistry;
import data.TaskConfig;
import util.StyleUtils;

//导入 Java Swing 和 AWT 库中常用的类，比如 JFrame、JPanel、CardLayout
import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    //💡️GUI 程序如 MainFrame：成员变量通常不立即初始化
    // 1.此处仅声明一些成员变量的类型，构造函数中创建具体实例，在方法中被不断调用或修改

        //cardLayout：卡片布局，用于在多个面板之间切换
        private CardLayout cardLayout;
        //mainPanel：主容器面板，设置为卡片布局
        private JPanel mainPanel;

        //首页面板，用于年级选择
        private HomePanel homePanel;

        // 当前任务选择页（未在构造函数未具体实现，在方法中初始化）
        private TaskSelectorPanel taskSelectorPanel;
        //当前选择的年级，-1时表示未选择
        private int currentGrade = -1;





    //💡MainFrame 构造方法：逻辑丰富，像搭建一整个舞台
    // ⚠️与一般构造函数不同，需要进一步做类比分析
    //2.构造函数，主程序创建窗口时会执行，构造函数第一时间运行，负责“把所有东西准备好”
    public MainFrame() {

        //首先，是初始化窗口的基本属性（搭建剧院房子本身）
            setTitle("MathearningGame");//设置标题
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置关闭行为（关闭窗口时退出程序）
            setSize(1000, 700);//设置窗口尺寸
            setLocationRelativeTo(null);//居中显示

        //之后，进一步确定整体剧院风格颜色等样式（作用于整个 MainFrame，包括mainPanel 的“舞台布景”）
        //因为this 传入的是 MainFrame 对象本身，也就是继承了 JFrame 的窗口
                StyleUtils.applyGlobalStyle(this);

        //然后，初始化主面板，可以容纳多个子卡片（如homePanel）,相当于搭建舞台
                //先提供多卡片（面板）翻叠结构（但是一次只能显示一张）
                cardLayout = new CardLayout();
                //真正初始化的同时，确定整体舞台多卡片（面板）翻叠结构
                mainPanel = new JPanel(cardLayout);
                    //创建首页面板
                        homePanel = new HomePanel(this);
                    //容纳到主面板，并给首页一个名字叫 “HOME”，方便用 showPanel("HOME") 来切换
                    //但注意，此时仅是把“首页”这个子卡片放进舞台的卡片堆里
                        mainPanel.add(homePanel, "HOME");

        //接着，主面板 mainPanel放进 MainFrame 里（正式摆放好舞台）
            add(mainPanel);

        //最后，切换到首页homepanel卡片（正式拉开帷幕）
        //这里才是真正的把首页卡片推到台前聚光灯下
            showPanel("HOME");
    }





    //3.接下来定义一系列方法（对外暴露的操作接口，定义“这个窗口可以干什么”）

        //定义一个方法，根据名字切换到指定的面板使用 CardLayout 的 show 方法）。
        public void showPanel(String name) {
            cardLayout.show(mainPanel, name);
        }

        // 调用此方法进入某年级任务选择页（从 HomePanel 触发）
        public void goToTaskSelector(int grade) {
            //如果之前已经创建过任务选择页，就先将其移除（防止旧内容残留）
            if (taskSelectorPanel != null) {
                mainPanel.remove(taskSelectorPanel); // 移除旧面板
            }
            //更新当前年级，创建新的任务选择页面，加入主容器并显示
            currentGrade = grade;
            taskSelectorPanel = new TaskSelectorPanel(this, grade);
            mainPanel.add(taskSelectorPanel, "SELECTOR");
            showPanel("SELECTOR");
        }


        // 当用户选择了一个任务后，调用此方法切换到该任务面板
        public void startTask(int grade, TaskConfig task) {
                   // 获取任务的唯一标识 taskId
                           String taskId = task.getId();
                    // 通过 TaskRegistry 工厂方法创建该任务的界面
                            JPanel taskPanel = TaskRegistry.createTaskPanel(taskId, this, grade);
                    // 添加到 mainPanel 并立即显示
                            mainPanel.add(taskPanel, taskId);
                    showPanel(taskId);
                }

        // 提供从任务选择页返回首页的方式（从 TaskSelectorPanel 触发）
        public void returnToHome() {
            showPanel("HOME");
        }

        // 设置年级并切换到任务选择页面
        public void setGradeAndSwitchToSelector(int grade) {
            // 在此方法中进行年级相关逻辑的处理，比如设置年级并切换到任务选择页
            System.out.println("选定年级：" + grade);
            goToTaskSelector(grade);
        }

        // 重置到首页
        public void resetToHome() {
            // 重置状态或进行其他清理操作
            showPanel("HOME");
        }
}