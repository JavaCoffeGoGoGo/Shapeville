package ui;

import util.StyleUtils;
import logic.ProgressTracker;

import javax.swing.*;
import java.awt.*;

/**
 * HomePanel 是应用的主界面面板。
 * 包含标题、等级说明、年级选择按钮、进度条和控制按钮等组件。
 */
public class HomePanel extends JPanel {

    private MainFrame mainFrame;


    private JProgressBar progressBar;
    private JProgressBar grade12ProgressBar;
    private JProgressBar grade34ProgressBar;

    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // 提前初始化进度条
        grade12ProgressBar = StyleUtils.createBubbleStyleProgressBar("年级 1-2 完成度");
        grade34ProgressBar = StyleUtils.createBubbleStyleProgressBar("年级 3-4 完成度");

        // 设置整体布局和边距
        this.setLayout(new BorderLayout(20, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // 顶部区域：标题
        JLabel title = StyleUtils.createTitleLabel("欢迎来到 Shapeville!");
        this.add(title, BorderLayout.NORTH);

        // 中部区域：介绍 & 等级说明 & 年级选择&进度条
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));

        centerPanel.add(createLevelIntroPanel(), BorderLayout.NORTH);
        centerPanel.add(createGradeSelectionPanel(), BorderLayout.CENTER);
        this.add(centerPanel, BorderLayout.CENTER);

        // 底部区域：控制按钮
        this.add(createBottomControlPanel(), BorderLayout.SOUTH);

        // 构造完成后，首次刷新所有进度条
        refreshAllProgressBars();
    }

    /**
     * 每次切回首页或需要刷新时调用，重新从 ProgressTracker 获取进度并更新 UI
     */
    public void refreshAllProgressBars() {
        int p1 = ProgressTracker.getProgressKS1();
        grade12ProgressBar.setValue(p1);
        grade12ProgressBar.setString("年级 1-2 完成度：" + p1 + "%");

        int p2 = ProgressTracker.getProgressKS2();
        grade34ProgressBar.setValue(p2);
        grade34ProgressBar.setString("年级 3-4 完成度：" + p2 + "%");
    }



    //—— 创建“欢迎”气泡 ——————————————————————————————————————
    private JPanel createWelcomeBubble() {
        JPanel bubble = StyleUtils.createBubblePanel();
        bubble.setLayout(new BorderLayout(0, 10));

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

    //—— 创建“得分机制说明”气泡 —————————————————————————————————
    private JPanel createScoreIntroBubble() {
        JPanel bubble = StyleUtils.createBubblePanel();
        bubble.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("任务得分机制说明", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        bubble.add(title, BorderLayout.NORTH);

        String[][] data = {
                {"挑战次数", "标准模式得分", "进阶模式得分"},
                {"首次挑战", "3 分", "6 分"},
                {"第二次挑战", "2 分", "4 分"},
                {"第三次挑战", "1 分", "2 分"}
        };
        JPanel table = new JPanel(new GridLayout(data.length, data[0].length));
        table.setOpaque(false);
        Font cellFont = new Font("SansSerif", Font.PLAIN, 15);
        for (int r = 0; r < data.length; r++) {
            for (int c = 0; c < data[r].length; c++) {
                JLabel lbl = new JLabel(data[r][c], SwingConstants.CENTER);
                lbl.setFont(cellFont);
                int top    = (r == 0 ? 1 : 0);
                int left   = (c == 0 ? 1 : 0);
                int bottom = 1;
                int right  = 1;
                lbl.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                table.add(lbl);
            }
        }
        bubble.add(table, BorderLayout.CENTER);
        return bubble;
    }

    //—— 创建“年级任务指南”气泡 —————————————————————————————————
    private JPanel createGradeTaskIntroBubble() {
        JPanel bubble = StyleUtils.createBubblePanel();
        bubble.setLayout(new BorderLayout(20, 20));

        JLabel title = new JLabel("年级与任务选择指南", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        bubble.add(title, BorderLayout.NORTH);

        String html =
                "<html><div style='text-align:center; font-family:SansSerif; font-size:12px;'>"
                        + "<div style='display:inline-block; text-align:left;'>"
                        + "<b>适用年级：Grade 1~2</b><br/>"
                        + "• Task1（图形识别）—— 平面 & 立体<br/>"
                        + "• Task2（角度分类）<br/><br/>"
                        + "<b>适用年级：Grade 3~4</b><br/>"
                        + "• Task3（常见图形面积计算）<br/>"
                        + "• Task4（圆的面积/周长）<br/>"
                        + "• Bonus1（复合图形计算）<br/>"
                        + "• Bonus2（扇形计算）"
                        + "</div></div></html>";

        JLabel content = new JLabel(html, SwingConstants.CENTER);
        content.setVerticalAlignment(SwingConstants.TOP);
        bubble.add(content, BorderLayout.CENTER);
        return bubble;
    }

    //—— 组合前三个气泡 —————————————————————————————————————————
    private JPanel createLevelIntroPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.add(createWelcomeBubble());
        container.add(Box.createVerticalStrut(15));
        container.add(createScoreIntroBubble());
        container.add(Box.createVerticalStrut(15));
        container.add(createGradeTaskIntroBubble());
        return container;
    }

    //—— 年级选择按钮区 —————————————————————————————————————————
    private JPanel createGradeSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        JLabel label = StyleUtils.createTitleLabel("请选择你的年级");
        panel.add(label, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 2, 10, 10));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder());

        // 统一尺寸
        Dimension buttonSize = new Dimension(400, 110);
        Dimension progressSize = new Dimension(400, 30);

        // 年级 1-2 区块
        JPanel cell1 = new JPanel();
        cell1.setLayout(new BoxLayout(cell1, BoxLayout.Y_AXIS));
        cell1.setOpaque(false);
        cell1.setBorder(BorderFactory.createEmptyBorder());

        JButton btn12 = StyleUtils.createStyledButton("年级 1-2");
        btn12.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn12.setMaximumSize(buttonSize);
        btn12.setPreferredSize(buttonSize);

        grade12ProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        grade12ProgressBar.setMaximumSize(progressSize);
        grade12ProgressBar.setPreferredSize(progressSize);

        btn12.addActionListener(e -> mainFrame.setGradeAndSwitchToSelector(1, 2));
        cell1.add(btn12);
        cell1.add(Box.createVerticalStrut(8));
        cell1.add(grade12ProgressBar);

        // 年级 3-4 区块
        JPanel cell2 = new JPanel();
        cell2.setLayout(new BoxLayout(cell2, BoxLayout.Y_AXIS));
        cell2.setOpaque(false);
        cell2.setBorder(BorderFactory.createEmptyBorder());

        JButton btn34 = StyleUtils.createStyledButton("年级 3-4");
        btn34.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn34.setMaximumSize(buttonSize);
        btn34.setPreferredSize(buttonSize);

        grade34ProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        grade34ProgressBar.setMaximumSize(progressSize);
        grade34ProgressBar.setPreferredSize(progressSize);

        btn34.addActionListener(e -> mainFrame.setGradeAndSwitchToSelector(3, 4));
        cell2.add(btn34);
        cell2.add(Box.createVerticalStrut(8));
        cell2.add(grade34ProgressBar);

        grid.add(cell1);
        grid.add(cell2);
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }


    //—— 底部控制区：结束按钮 —————————————————————————————————
    private JPanel createBottomControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(0,0));
        panel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton endButton = StyleUtils.createStyledButton("结束会话");

        endButton.setPreferredSize(new Dimension(800, 50));

        endButton.addActionListener(e -> {
            StyleUtils.applyGlobalStyle(SwingUtilities.getWindowAncestor(this));
            // 取原始分和百分比
            int raw1 = ProgressTracker.getRawScoreKS1();
            int pct1 = ProgressTracker.getProgressKS1();
            int raw2 = ProgressTracker.getRawScoreKS2();
            int pct2 = ProgressTracker.getProgressKS2();

            // 拼消息
            String msg = String.format(
                    "🎉 会话结束！\n\n" +
                            "Key Stage 1：%d / 93 分（%d%%）\n" +
                            "Key Stage 2：%d / 126 分（%d%%）\n\n" +
                            "感谢参与，Goodbye!",
                    raw1, pct1, raw2, pct2
            );

            // 弹出最终成绩
            JDialog dialog = StyleUtils.createStyledMessageDialog(this, "本次成绩", msg);
            dialog.setVisible(true);

            // 关闭窗口
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });

        buttonPanel.add(endButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }
}