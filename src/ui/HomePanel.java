package ui;

import util.StyleUtils;
import logic.GradingSystem;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

/**
 * HomePanel 是应用的主界面面板。
 * 包含标题、等级说明、年级选择按钮、进度条和控制按钮等组件。
 */
public class HomePanel extends JPanel {

    // HomePanel：主页面板，负责展示欢迎界面与导航入口

    // 1.成员变量声明（仅保存引用）
    //  它是一个引用变量（reference variable），本身不是对象，只是个地址位
    //  现在先留个空位置，但已经能承诺是留给MainFrame，即使他不知道MainFrame是啥
    //  之后指向了MainFrame传入的参数，就可以用于页面跳转与状态传递
    private MainFrame mainFrame;



    // 2.构造方法：设置布局结构，初始化所有子面板（顶部、中部、底部）
    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // 1）先设置当前面板整体布局和样式

            // 整体布局为 BorderLayout(即上分上下左右中五大区)
            this.setLayout(new BorderLayout(20,0));
            // 加宽边界
            this.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // 2）然后逐个区域进行初始化并添加

            // 1. 顶部区域：标题文字
                //先准备组件
                JLabel title = StyleUtils.createTitleLabel(" 欢迎来到Shapeville!");
                //再定位添加
                this.add(title, BorderLayout.NORTH);

            // 2. 中部区域：介绍&等级说明&选择指南 + 年级选择按钮
                //1）先准备组件,整体上是一个中间区域的小面板，仍边界布局 BorderLayout 来排列它的子元素
                    //1. 先设置这个小面板的整体样式
                        // 水平/垂直方向都留 10 像素间距，即上下左右中之间
                        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
                        //设置透明背景
                        centerPanel.setOpaque(false);
                        //设置边距
                        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

                    //2. 再添加介绍&等级说明&选择指南
                    //createLevelIntroPanel() 是一个自定义方法，返回一个写有“介绍&等级说明&选择指南”的面板
                    centerPanel.add(this.createLevelIntroPanel(), BorderLayout.NORTH);

                    //3. 再添加年级选择区
                    //createGradeSelectionPanel() 是另一个自定义方法，返回一个面板,共分为四块。
                    centerPanel.add(this.createGradeSelectionPanel(), BorderLayout.CENTER);

                //2）再定位添加到HomePanel面板里
                this.add(centerPanel, BorderLayout.CENTER);

            // 3. 底部区域：进度条 + 控制按钮（回首页、结束会话）
                //准备和添加同时进行
                this.add(this.createBottomControlPanel(), BorderLayout.SOUTH);
    }



    // 3.子面板构建方法（独立区域的构建逻辑）

    //  centerPanel顶部等级说明区

    // ================= 1. 先创建一系列气泡 =================
            // ================= 1. 1）再建造Shapeville 简短介绍气泡 =================
            private JPanel createWelcomeBubble() {
                JPanel bubble = StyleUtils.createBubblePanel();
                bubble.setLayout(new BorderLayout(0, 10));

                // HTML 一次写完段落，并自动换行
                String html = "<html><div style='text-align:center; font-family:SansSerif; font-size:12px;'>"
                        + "<b>Shapeville</b> 是一款专为 <b>1~4 年级</b>学生打造的<b>趣味数学游戏</b>，"
                        + "通过<b>互动任务</b>帮助孩子们掌握<b>图形识别</b>、<b>面积计算</b>和<b>角度分类</b>等核心知识。<br/>"
                        + "每完成一个任务，都会有<b>温馨的即时反馈</b>，让学习变得<b>轻松有趣</b>！"
                        + "</div></html>";

                JLabel content = new JLabel(html, SwingConstants.CENTER);
                content.setVerticalAlignment(SwingConstants.TOP);
                bubble.add(content, BorderLayout.CENTER);

                return bubble;
            }
            // ================= 1. 2）再建造得分机制说明气泡 =================
            private JPanel createScoreIntroBubble() {
                JPanel bubble = StyleUtils.createBubblePanel();
                bubble.setLayout(new BorderLayout(0, 10));

                JLabel title = new JLabel("任务得分机制说明", SwingConstants.CENTER);
                title.setFont(new Font("SansSerif", Font.BOLD, 18));
                bubble.add(title, BorderLayout.NORTH);

                String[][] data = {
                        // 更友好一点的标题
                        {"挑战次数", "标准模式得分", "进阶模式得分"},
                        // 首次挑战给满分鼓励
                        {"首次挑战", "3 分", "6 分"},
                        // 第二次稍有扣减
                        {"第二次挑战", "2 分", "4 分"},
                        // 第三次再接再厉
                        {"第三次挑战", "1 分", "2 分"}
                };
                JPanel table = new JPanel(new GridLayout(data.length, data[0].length));
                table.setOpaque(false);
                Font cellFont = new Font("SansSerif", Font.PLAIN, 15);
                int rows = data.length;
                int cols = data[0].length;

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        JLabel lbl = new JLabel(data[r][c], SwingConstants.CENTER);
                        lbl.setFont(cellFont);

                        // 计算四边是否要画线
                        int top    = (r == 0         ? 1 : 0);
                        int left   = (c == 0         ? 1 : 0);
                        int bottom = (r == rows - 1  ? 1 : 0);
                        int right  = (c == cols - 1  ? 1 : 0);

                        // 内部所有水平线／垂直线都画
                        // （不论是否在边缘，右和下总是画）
                        right  = 1;
                        bottom = 1;

                        lbl.setBorder(BorderFactory.createMatteBorder(
                                top, left, bottom, right, Color.BLACK
                        ));

                        table.add(lbl);
                    }
                }
                bubble.add(table, BorderLayout.CENTER);

                return bubble;
            }

            // ================= 1. 3）再建造年级与任务选择指南气泡 =================
            private JPanel createGradeTaskIntroBubble() {
                JPanel bubble = StyleUtils.createBubblePanel();
                bubble.setLayout(new BorderLayout(20, 20));

                JLabel title = new JLabel("年级与任务选择指南", SwingConstants.CENTER);
                title.setFont(new Font("SansSerif", Font.BOLD, 18));
                bubble.add(title, BorderLayout.NORTH);

                // 使用 HTML 让文字居中、分段更清晰
                String html =
                        "<html>"
                                + "<div style='text-align:center; font-family:SansSerif; font-size:12px;'>"
                                +   "<div style='display:inline-block; text-align:left;'>"
                                +     "<b>适用年级：Grade 1~2</b><br/>"
                                +     "• <b>Task1（图形识别）</b> —— 又分为两个子任务</b><br/>"
                                +     "&nbsp;&nbsp;- 标准模式：随机平面图形<br/>"
                                +     "&nbsp;&nbsp;- 进阶模式：随机立体图形<br/>"
                                +     "• <b>Task2（角度分类）</b> —— 标准模式<br/><br/>"
                                +     "<b>适用年级：Grade 3~4</b><br/>"
                                +     "• <b>Task3（常见图形面积计算）</b> —— 标准模式</b><br/>"
                                +     "• <b>Task4（圆的面积或周长计算）</b> —— 标准模式</b><br/>"
                                +     "• <b>Bonus1（复合图形计算）</b> —— 进阶模式<br/>"
                                +     "• <b>Bonus2（扇形计算）</b> —— 进阶模式"
                                +   "</div>"
                                + "</div>"
                                + "</html>";

                JLabel content = new JLabel(html, SwingConstants.CENTER);
                content.setVerticalAlignment(SwingConstants.TOP);
                bubble.add(content, BorderLayout.CENTER);

                return bubble;
            }

        // ================= 2. 最后三个气泡合一块 =================
        private JPanel createLevelIntroPanel() {
            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setOpaque(false);

            // ① 欢迎介绍气泡
            container.add(createWelcomeBubble());
            container.add(Box.createVerticalStrut(15));

            // ② 得分机制说明气泡
            container.add(createScoreIntroBubble());
            container.add(Box.createVerticalStrut(15));

            // ③ 年级任务指南气泡
            container.add(createGradeTaskIntroBubble());

            return container;
        }



    // centerPanel中部年级选择区（按钮绑定跳转逻辑）
    private JPanel createGradeSelectionPanel() {

        // 1. 整体布局设置
        // 采用边界布局（上下结构：上是提示文字，下是按钮区域）
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false); // 背景透明

        //2. 组件添加
        // 1）顶部文字提示部分
            //1. 设置组件
            JLabel label = StyleUtils.createTitleLabel(" 请选择你的年级");
            //2. 添加组件
            panel.add(label, BorderLayout.NORTH); // 添加至上方区域

        // 2）下方按钮网格部分（1行2列）
            // 1. 设置中间按钮区整体布置
                    JPanel buttonGrid = new JPanel(new GridLayout(1, 2, 10, 10));
                    buttonGrid.setOpaque(false); // 背景透明
            // 2. 创建“年级 1-2”按钮
                    JButton grade12Button = StyleUtils.createStyledButton("年级 1-2");
                    grade12Button.addActionListener(e -> {
                        mainFrame.setGradeAndSwitchToSelector(1, 2);
                    });
            // 3. 创建“年级 3-4”按钮
                    JButton grade34Button = StyleUtils.createStyledButton("年级 3-4");
                    grade34Button.addActionListener(e -> {
                        mainFrame.setGradeAndSwitchToSelector(3, 4);
                    });
            // 4. 添加到网格
                    buttonGrid.add(grade12Button);
                    buttonGrid.add(grade34Button);
            // 5. 按钮网格放入面板中心区域
                    panel.add(buttonGrid, BorderLayout.CENTER);
        // 返回构建好的选择面板
        return panel;
    }



    // HomePanel底部控制区（进度条 + 两个控制按钮）
    private JPanel createBottomControlPanel() {

        //1. 整体设置
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false); // 设置透明背景

        //2. 组件设置与添加

        // 1）中部区域：进度条（用于显示整体完成进度）
            JProgressBar progressBar = new JProgressBar(); // 创建进度条对象
            int progress = ProgressTracker.getOverallProgress(); // 设置当前进度值（从逻辑模块获取）
            progressBar.setValue(progress);
            progressBar.setString("当前整体完成度：" + progress + "%"); // 设置百分比文字
            progressBar.setStringPainted(true); // 显示文本
            progressBar.setForeground(StyleUtils.TITLE_COLOR); // 设置主色调（样式统一）
            panel.add(progressBar, BorderLayout.CENTER); // 添加到中间区域

        // 2）右部区域：按钮区（包含“回首页”和“结束会话”）

            //1. 设置按钮区整体布局
            JPanel buttonPanel = new JPanel(new FlowLayout()); // 流式布局（横向排布）
            buttonPanel.setOpaque(false); // 背景透明

            //2. 按钮设置
            // 整体配置

            // “结束会话”按钮（弹窗展示得分）
            JButton endButton = StyleUtils.createStyledButton("结束会话");

            endButton.addActionListener(e -> {
                int score = GradingSystem.getTotalScore(); // 获取得分
                // 使用确认弹窗
                int option = JOptionPane.showConfirmDialog(this,
                        "本次总得分：" + score + "\n继续加油！\n\n点击“确定”将关闭窗口。",
                        "会话结束", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (option == JOptionPane.OK_OPTION || option == JOptionPane.CLOSED_OPTION) {
                    Window window = SwingUtilities.getWindowAncestor(this); // 获取当前窗口
                    if (window != null) {
                        window.dispose(); // 关闭窗口
                    }
                }
            });

            // “重置会话”按钮（确认后跳转首页 + 关闭窗口）
            JButton resetButton = StyleUtils.createStyledButton("重置任务进度");

            resetButton.addActionListener(e -> {
                int option = JOptionPane.showConfirmDialog(this,
                        "确认重置所有信息并返回首页？",
                        "重置确认", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option == JOptionPane.OK_OPTION || option == JOptionPane.CLOSED_OPTION) {
                    mainFrame.resetToHome(); // 跳转首页并重置状态
                }
            });

            //3. 添加两个按钮到右部区域
            buttonPanel.add(endButton);
            buttonPanel.add(resetButton);

            //4. 最后整个按钮区添加到底部右侧
            panel.add(buttonPanel, BorderLayout.EAST);

        // 返回构建好的底部控制面板
        return panel;
    }

}