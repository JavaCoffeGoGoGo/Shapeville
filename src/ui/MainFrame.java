package ui;

import logic.TaskRegistry;
import data.TaskConfig;
import util.StyleUtils;

//导入 Java Swing 和 AWT 库中常用的类，比如 JFrame、JPanel、CardLayout
import javax.swing.*;
import java.awt.*;

//  主窗口类 MainFrame：整个图形程序的“舞台”
public class MainFrame extends JFrame {
    // ===============================
    //  一、成员变量定义（舞台准备）
    // ===============================

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


    // ===============================
    //  二、构造函数：主舞台初始化
    // ===============================


    public MainFrame() {

        //1. 首先，是初始化窗口的基本属性（搭建剧院房子本身）
        setTitle("Shapeville");//设置标题
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置关闭行为（关闭窗口时退出程序）
        setSize(1000, 1000);//设置窗口尺寸
        setLocationRelativeTo(null);//居中显示

        //2. 之后，进一步确定整体剧院风格颜色等样式（作用于整个 MainFrame，包括mainPanel 的“舞台布景”）
        StyleUtils.applyGlobalStyle(this);//因为this 传入的是 MainFrame 对象本身，也就是继承了 JFrame 的窗口

        //3. 然后，初始化主面板，可以容纳多个子卡片（如homePanel）,相当于搭建舞台
            // 先提供多卡片（面板）翻叠结构（但是一次只能显示一张）
            // 也是对前面变量的的正式赋值
            cardLayout = new CardLayout();
            //真正初始化的同时，确定整体舞台多卡片（面板）翻叠结构
            mainPanel = new JPanel(cardLayout);
            //放入首个卡片
                //创建首页面板
                homePanel = new HomePanel(this);
                //容纳到主面板，并给首页一个名字叫 “HOME”，方便用 showPanel("HOME") 来切换
                //但注意，此时仅是把“首页”这个子卡片放进舞台的卡片堆里
                mainPanel.add(homePanel, "HOME");

        //4. 接着，主面板 mainPanel放进 MainFrame 里（正式摆放好舞台）
        add(mainPanel);

        //5. 最后，切换到首页homepanel卡片（正式拉开帷幕）
        showPanel("HOME");
    }

    // ===============================
    //  三、面板切换与页面跳转方法
    // ===============================

    //3.接下来定义一系列方法（对外暴露的操作接口，定义“这个窗口可以干什么”）


        // ----------------------------------
        // 1） 基础方法：卡片切换工具
        // ----------------------------------
            //底层公共方法，负责显示某个命名面板（使用 CardLayout 的 show 方法）。
            public void showPanel(String name) {
                cardLayout.show(mainPanel, name);
            }

        // ----------------------------------
        // 2） 返回首页的两种方式
        // ----------------------------------

            // 重置整个界面状态并返回首页（暂未实现，留待后续拓展）
            public void resetToHome() {
                showPanel("HOME");
            }

            // 简单返回首页
            public void returnToHome() {
                showPanel("HOME");
            }

        // ----------------------------------
        // 3） 年级选择跳转逻辑
        // ---------------------------------

            public void setGradeAndSwitchToSelector(int startGrade, int endGrade) {

                //1.如果之前已经创建过任务选择页（说明之前进入过一次），就先将其移除
                if (taskSelectorPanel != null) {
                    mainPanel.remove(taskSelectorPanel); // 移除旧面板
                }
                //2.创建新的任务选择页面
                //this 代表当前 MainFrame 实例，用于TaskSelectorPanel安排任务，比如点击按钮的反应
                //grade 是你要跳转的“年级数”，用于TaskSelectorPanel灵活创建面板
                taskSelectorPanel = new TaskSelectorPanel(this, startGrade, endGrade);
                //3.加入主面板（舞台）并显示
                mainPanel.add(taskSelectorPanel, "SELECTOR");
                showPanel("SELECTOR");
            }

        // ----------------------------------
        // 4） 进入任务界面的方法
        // ----------------------------------
            public void startTask(int grade, TaskConfig task) {
                   // 获取任务的唯一标识 taskId
                           String taskId = task.getId();
                    // 通过 TaskRegistry 工厂方法创建该任务的界面
                            JPanel taskPanel = TaskRegistry.createTaskPanel(taskId, this, grade);
                    // 把这个新生成的任务界面，加入到主舞台 mainPanel 的卡片堆中
                            mainPanel.add(taskPanel, taskId);
                    // 正式切换到这个任务界面，相当于拉开了任务舞台的帷幕
                    showPanel(taskId);
                }
}