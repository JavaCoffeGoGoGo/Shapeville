package ui;


import data.TaskConfig;
import ui.tasks.*;
import util.StyleUtils;


import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.BiFunction;


//导入 Java Swing 和 AWT 库中常用的类，比如 JFrame、JPanel、CardLayout


//  主窗口类 MainFrame：整个图形程序的“舞台”
public class MainFrame extends JFrame {
    // ===============================
    //  一、成员变量定义（舞台准备）
    // ===============================

        // ================= 成员变量 =================
        private CardLayout cardLayout;
        private JPanel mainPanel;
        private HomePanel homePanel;
        private TaskSelectorPanel taskSelectorPanel;
        private JPanel lastPanelBeforeCurrent = null;

        // ================= 任务注册表 =================
        private static final Map<String, BiFunction<MainFrame, Integer, JPanel>> TASK_REGISTRY = Map.of(
    "g12_shape", (frame, grade) -> new ShapeSubSelectorPanel(frame, grade),
    "g12_2D_shape", (frame, grade) -> new Task1TwoDPanel(frame, grade, "g12_2D_shape"),
    "g12_3D_shape", (frame, grade) -> new Task1ThreeDPanel(frame, grade, "g12_3D_shape"),
    "g12_angle", (frame, grade) -> new Task2Panel(frame, grade, "g12_angle"),
    "g34_area_basic", (frame, grade) -> new Task3Panel(frame, grade, "g34_area_basic"),
    "g34_circle", (frame, grade) -> new Task4Panel(frame, grade, "g34_circle"),
    "g34_area_complex", (frame, grade) -> new Bonus1Panel(frame, grade, "g34_area_complex"),
    "g34_src", (frame, grade) -> new Bonus2Panel(frame, grade, "g34_src")
        );


    // ===============================
    //  二、构造函数：主舞台初始化
    // ===============================


    public MainFrame() {

        //1. 首先，是初始化窗口的基本属性（搭建剧院房子本身）
        setTitle("Shapeville");//设置标题
        setSize(1000, 1000);//设置窗口尺寸
        setLocationRelativeTo(null);//居中显示

        //2. 之后，进一步确定整体剧院风格颜色等样式（作用于整个 MainFrame）
        StyleUtils.applyGlobalStyle(this);

        //3. 然后，初始化主面板，可以容纳多个子卡片（如homePanel）,相当于搭建舞台

            // 先提供多卡片（面板）翻叠结构
            cardLayout = new CardLayout();
            // 初始化并确定整体舞台多卡片（面板）翻叠结构
            mainPanel = new JPanel(cardLayout);
            // 放入首个卡片
                //创建首页面板
                homePanel = new HomePanel(this);
                //把“首页”这个子卡片放进舞台的卡片堆里，给一个名字叫 “HOME”，方便用 showPanel("HOME") 来切换
                mainPanel.add(homePanel, "HOME");

        //4. 接着，主面板 mainPanel放进 MainFrame 里（正式摆放好舞台）
        add(mainPanel);

        //5. 最后，切换到首页homepanel卡片（正式拉开帷幕）
        showPanel("HOME");
    }

    // ===============================
    //  三、面板切换与页面跳转方法
    // ===============================


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
                    mainPanel.remove(taskSelectorPanel);
                }
                //2.创建新的任务选择页面
                taskSelectorPanel = new TaskSelectorPanel(this, startGrade, endGrade);
                //3.加入主面板（舞台）并显示
                mainPanel.add(taskSelectorPanel, "SELECTOR");
                showPanel("SELECTOR");
            }

        // ----------------------------------
        // 4） 进入任务界面的方法
        // ----------------------------------
            // 1. 任务启动逻辑
        public void enterTask(int grade, TaskConfig task) {
            String taskId = task.getId();
            JPanel taskPanel = createTaskPanel(taskId, grade);

            mainPanel.add(taskPanel, taskId);
            showPanel(taskId);
        }

            // 2. 整合的任务注册方法
            private JPanel createTaskPanel(String taskId, int grade) {
                BiFunction<MainFrame, Integer, JPanel> creator = TASK_REGISTRY.get(taskId);
                if (creator != null) {
                    return creator.apply(this, grade);
                } else {
                    JPanel errorPanel = new JPanel();
                    errorPanel.add(new JLabel("未找到任务: " + taskId));
                    return errorPanel;
                }
            }


}