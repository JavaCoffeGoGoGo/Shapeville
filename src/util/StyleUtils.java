package util;
import javax.swing.*;
import java.awt.*;

/**
 * StyleUtils：全局样式工具类
 */


public class StyleUtils {

    //一、先梳理一些基础知识：
    // 1.详见StyleUtils.md文档

    // 2.下面方法部分分别是
        // 对UIManager部分的封装
        // 对一些常见组件直接创建组件对象过程的封装
        // 和一个复杂气泡面板的特殊的“继承组件类 + 匿名内部类”的封装
        // 从而增强ui代码的简洁性

    //1） 初始化字体和色彩设定
        // ========================================
        // 1. 字体设定（Font）—— 定义统一字体规范
        // ========================================
        public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 16); // 默认字体（常规）
        private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);    // 标题字体（加粗）

        // ========================================
        // 2. 色彩设定（Color）—— 定义全局配色方案
        // ========================================
        public static final Color TITLE_COLOR = new Color(254, 97, 0); // 橙色主色调
        public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);  // 背景色：浅灰色，避免刺眼
        public static final Color TEXT_COLOR = new Color(0, 0, 0);              // 文本颜色：黑色




    // 2） 定义方法

        // 1. UIManager全局设置封装
        public static void applyGlobalStyle(Component component) {

            // 1）作用于传入的组件本身，本程序中只用于MainFrame
            // 一般情况下不需要，但是我们不在UIManager里设置关于frame的默认属性
            // 统一在这里设计，使得程序更清晰
            component.setFont(DEFAULT_FONT);               // 设置统一字体
            component.setBackground(BACKGROUND_COLOR);     // 设置统一背景色
            component.setForeground(TEXT_COLOR);           // 设置统一文字颜色

            // 2）如果是 JPanel，则设置默认布局为 BorderLayout
            if (component instanceof JPanel) {
                ((JPanel) component).setLayout(new BorderLayout());
            }

            // 3）设置 UIManager 的默认样式属性，注意是修改默认样式，并不会影响局部自定义
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Label.font", DEFAULT_FONT);
            UIManager.put("Label.foreground", TEXT_COLOR);
            UIManager.put("TextField.font", DEFAULT_FONT);
            UIManager.put("TextArea.font", DEFAULT_FONT);
            UIManager.put("Button.font", DEFAULT_FONT);
            UIManager.put("Button.background", TITLE_COLOR);


            // 3）若是 JComponent，则递归刷新其子组件的 UI 风格
            // 如果传入的是 JComponent（比如 JLabel、JButton、JPanel 等），那就更新它的 UI 树
            // 因为你刚刚设置了全局 UI 样式，它们默认不会马上生效，必须刷新一下
            if (component instanceof JComponent) {
                //updateComponentTreeUI() 会让传入组件的自己和所有子组件都重新绘制并应用新样式
                SwingUtilities.updateComponentTreeUI((JComponent) component);
            }

            // 4）专门刷新 JFrame（顶层窗口）里的全部内容
            // JFrame 是一个顶层容器，不直接是 JComponent
            // JFrame 本身不能直接传进SwingUtilities.updateComponentTreeUI
            // 但是 JFrame 包含一个内部结构叫 根面板（getRootPane()），它是 JComponent 类型
            if (component instanceof JFrame) {
                SwingUtilities.updateComponentTreeUI(((JFrame) component).getRootPane());
                //  •传进来的 component 是个 Component 类型（Java 所有 UI 组件的基类）。
                //	•如果确定这个组件其实是个窗口（JFrame），就可以用强制类型转换把它变回 JFrame
            }
        }

        // ========================================
        // 2. 常见组件直接创建组件对象过程的封装
        // ========================================


            // 1）创建标题标签
            public static JLabel createTitleLabel(String text) {
                // SwingConstants.CENTER 是一个常量，代表水平居中
                JLabel label = new JLabel(text, SwingConstants.CENTER); // 居中构造标签
                label.setFont(TITLE_FONT);                              // 设置标题字体
                label.setForeground(new Color(254, 97, 0)); // 橙色文字
                //  label.setHorizontalAlignment(...) 是第二次设定，作为“保险”
                // 	•	一些 L&F 或 layout 管理器可能不会立即识别构造器设置；
                //	•	明确调用 setHorizontalAlignment() 可以提高兼容性，避免“不居中”的意外情况。
                label.setHorizontalAlignment(SwingConstants.CENTER);    // 强制居中（双重保险）
                return label;
            }

            // 2）进度条
            public static void styleProgressBar(JProgressBar bar, String prefix) {
                bar.setStringPainted(true);
                bar.setForeground(new Color(254, 97, 0)); // 橙色进度条
                bar.setString(prefix + ": 0%");
                bar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            }



        // ========================================
        // 3. 特殊的“继承组件类 + 匿名内部类”的封装
        // ========================================
        //1）橙底白字按钮
        public static JButton createStyledButton(String text) {
            JButton button = new JButton(text) {
                private static final int ARC = 20;
                private static final int SHADOW_SIZE = 4;

                @Override
                protected void paintComponent(Graphics g) {
                    int w = getWidth(), h = getHeight();
                    boolean pressed = getModel().isArmed() || getModel().isPressed();

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 阴影（始终绘制）
                    int shadowOffset = pressed ? 1 : SHADOW_SIZE;
                    g2.setColor(new Color(0, 0, 0, 35));
                    g2.fillRoundRect(shadowOffset, shadowOffset, w - shadowOffset * 2, h - shadowOffset * 2, ARC, ARC);

                    // 背景颜色：普通橙色 vs 深橙色
                    Color bgColor = pressed ? new Color(204, 70, 0) : new Color(254, 97, 0);
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, w - shadowOffset, h - shadowOffset, ARC, ARC);

                    g2.dispose();

                    // super.paintComponent 最后调用，负责绘制文字
                    super.paintComponent(g);
                }
            };

            // 设置按钮基本属性
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("SansSerif", Font.BOLD, 16));

            return button;
        }


        //2）创建气泡风格面板
        public static JPanel createBubblePanel() {
            JPanel panel = new JPanel() {
                private static final int ARC = 30;
                private static final int SHADOW_SIZE = 4;

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g); // 🔍 保证子组件正常绘制

                    int width = getWidth();
                    int height = getHeight();

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 绘制整体阴影（四周模糊感）
                    g2.setColor(new Color(0, 0, 0, 30)); // 更淡的灰色，避免突兀
                    g2.fillRoundRect(SHADOW_SIZE, SHADOW_SIZE, width - SHADOW_SIZE * 2, height - SHADOW_SIZE * 2, ARC, ARC);

                    // 绘制主面板背景（白色半透明）
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.fillRoundRect(0, 0, width - SHADOW_SIZE, height - SHADOW_SIZE, ARC, ARC);

                    // 浅灰描边（内边界框）
                    g2.setColor(new Color(200, 200, 200)); // 浅灰色边框
                    g2.setStroke(new BasicStroke(0.4f)); // 稍微粗一点
                    g2.drawRoundRect(0, 0, width - SHADOW_SIZE, height - SHADOW_SIZE, ARC, ARC);

                    g2.dispose();
                }
            };
            panel.setOpaque(false);
            panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            return panel;
        }

        // 3）创建风格化进度条组件（统一 UI 风格）
        public static JProgressBar createBubbleStyleProgressBar(String prefix) {
            JProgressBar bar = new JProgressBar(0, 100) {
                private static final int ARC = 20;
                private static final int SHADOW_SIZE = 3;

                @Override
                protected void paintComponent(Graphics g) {
                    int width = getWidth();
                    int height = getHeight();

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 背景阴影（偏移产生浮感）
                    g2.setColor(new Color(0, 0, 0, 25));
                    g2.fillRoundRect(SHADOW_SIZE, SHADOW_SIZE, width - SHADOW_SIZE * 2, height - SHADOW_SIZE * 2, ARC, ARC);

                    // 面板背景（白色半透明）
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.fillRoundRect(0, 0, width - SHADOW_SIZE, height - SHADOW_SIZE, ARC, ARC);

                    // 进度部分填充（可自定义主色）
                    int fillWidth = (int) (((double) getValue() / getMaximum()) * (width - SHADOW_SIZE));
                    g2.setColor(new Color(254, 97, 0, 200)); // 橙色填充，带透明度
                    g2.fillRoundRect(0, 0, fillWidth, height - SHADOW_SIZE, ARC, ARC);

                    // 边框（浅灰色）
                    g2.setColor(new Color(200, 200, 200));
                    g2.setStroke(new BasicStroke(0.5f));
                    g2.drawRoundRect(0, 0, width - SHADOW_SIZE, height - SHADOW_SIZE, ARC, ARC);

                    // 文本（居中显示）
                    String text = prefix + ": " + getValue() + "%";
                    g2.setFont(getFont());
                    g2.setColor(Color.DARK_GRAY);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (width - fm.stringWidth(text)) / 2;
                    int textY = (height + fm.getAscent() - fm.getDescent()) / 2 - 1;
                    g2.drawString(text, textX, textY);

                    g2.dispose();
                }
            };

            bar.setPreferredSize(new Dimension(800, 26));
            bar.setFont(DEFAULT_FONT);
            bar.setOpaque(false); // 启用透明背景
            bar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            return bar;
        }


        // 4）自定义会话窗口
        public static JDialog createStyledMessageDialog(Component parent, String title, String message) {
            // —— 样式常量（也可以抽成参数或全局配置） ——
            Color backgroundColor = BACKGROUND_COLOR;      // 比如 StyleUtils.BACKGROUND_COLOR
            Color textColor       = TEXT_COLOR;            // StyleUtils.TEXT_COLOR
            Font  defaultFont     = DEFAULT_FONT;          // StyleUtils.DEFAULT_FONT

            // 构造对话框
            Frame owner = JOptionPane.getFrameForComponent(parent);
            JDialog dialog = new JDialog(owner, title, true);

            // 内容面板
            JPanel panel = new JPanel(new BorderLayout(10,10));
            panel.setBackground(backgroundColor);
            panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            // —— 多行标签，用 HTML 换行 ——
            String html = "<html>" + message.replace("\n", "<br/>") + "</html>";
            JLabel label = new JLabel(html);
            label.setFont(defaultFont);
            label.setForeground(textColor);
            panel.add(label, BorderLayout.CENTER);

            // —— 使用全局风格按钮 ——
            JButton okButton = createStyledButton("确定");
            okButton.addActionListener(e -> dialog.dispose());

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(backgroundColor);
            btnPanel.add(okButton);
            panel.add(btnPanel, BorderLayout.SOUTH);

            dialog.setContentPane(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            return dialog;
        }


    // 5）显示一个自定义风格的“是/否”确认对话框，返回用户选择：YES_OPTION 或 NO_OPTION

    public static int showStyledConfirmDialog(Component parent, String title, String message) {
        Frame owner = JOptionPane.getFrameForComponent(parent);
        JDialog dialog = new JDialog(owner, title, true); // 模态对话框

        // 统一样式
        Color bg = BACKGROUND_COLOR;
        Color text = TEXT_COLOR;
        Font font = DEFAULT_FONT;
        Color accent = TITLE_COLOR; // 橙色强调色

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // HTML 显示消息（支持换行）
        String html = "<html>" + message.replace("\n", "<br><br>") + "</html>";
        JLabel lbl = new JLabel(html);

        lbl.setFont(font);
        lbl.setForeground(text);
        panel.add(lbl, BorderLayout.CENTER);

        // 返回值容器
        final int[] result = {JOptionPane.NO_OPTION};

        // 按钮区域（使用 GridLayout 强制按钮等宽）
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 行 2 列，水平间隔 10
        btnPanel.setBackground(bg);

        // 确认按钮（主按钮样式）
        JButton yesBtn = createStyledButton("确认");
        yesBtn.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        // 取消按钮（白底橙字 + 圆角阴影 + 橙色描边）
        JButton noBtn = new JButton("取消") {
            private static final int ARC = 20;
            private static final int SHADOW_SIZE = 4;

            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 阴影
                g2.setColor(new Color(0, 0, 0, 35));
                g2.fillRoundRect(SHADOW_SIZE, SHADOW_SIZE, w - SHADOW_SIZE * 2, h - SHADOW_SIZE * 2, ARC, ARC);

                // 白色背景
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - SHADOW_SIZE, h - SHADOW_SIZE, ARC, ARC);

                // 橙色边框
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawRoundRect(0, 0, w - SHADOW_SIZE, h - SHADOW_SIZE, ARC, ARC);

                g2.dispose();
                super.paintComponent(g);  // 绘制文字
            }
        };
        noBtn.setContentAreaFilled(false);
        noBtn.setFocusPainted(false);
        noBtn.setBorderPainted(false);
        noBtn.setFont(font);
        noBtn.setForeground(accent);
        noBtn.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        // 添加按钮（GridLayout 会强制等宽）
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return result[0];
    }

    // 6）添加风格化输入框
    public static JTextField createRoundedTextField(int width, int height) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 填充圆角背景
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(StyleUtils.TITLE_COLOR); // 自定义边框颜色
                g2.setStroke(new BasicStroke(0.6f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false; // 为了避免方形背景
            }
        };

        field.setPreferredSize(new Dimension(width, height));
        field.setMaximumSize(new Dimension(width, height));
        field.setFont(StyleUtils.DEFAULT_FONT);
        field.setForeground(StyleUtils.TEXT_COLOR);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 内边距
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }
}