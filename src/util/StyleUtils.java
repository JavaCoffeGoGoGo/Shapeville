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
        private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 16); // 默认字体（常规）
        private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);    // 标题字体（加粗）

        // ========================================
        // 2. 色彩设定（Color）—— 定义全局配色方案
        // ========================================
        public static final Color TITLE_COLOR = new Color(0, 85, 170);          // 主色：深蓝色，强调信息
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
                label.setForeground(TITLE_COLOR);                     // 使用主色（蓝色）作为文字颜色
                //  label.setHorizontalAlignment(...) 是第二次设定，作为“保险”
                // 	•	一些 L&F 或 layout 管理器可能不会立即识别构造器设置；
                //	•	明确调用 setHorizontalAlignment() 可以提高兼容性，避免“不居中”的意外情况。
                label.setHorizontalAlignment(SwingConstants.CENTER);    // 强制居中（双重保险）
                return label;
            }

            // 2）创建任务说明标签
            public static JLabel createInstructionLabel(String text) {
                JLabel label = new JLabel(text);
                label.setFont(DEFAULT_FONT);       // 使用常规字体
                label.setForeground(TEXT_COLOR);   // 黑色文字
                return label;
            }


        // ========================================
        // 3. 一个特殊的“继承组件类 + 匿名内部类”的封装
        // ========================================

        // 1）创建统一风格按钮
        public static JButton createStyledButton(String text) {
            JButton button = new JButton(text) {
                private static final int ARC = 20;
                private static final int SHADOW_SIZE = 3;

                @Override
                protected void paintComponent(Graphics g) {
                    int w = getWidth(), h = getHeight();
                    boolean pressed = getModel().isArmed() || getModel().isPressed();

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 按下时，整体往下右偏移1px，背景颜色加深
                    int offset = pressed ? 1 : 0;
                    Color bgColor = pressed
                            ? new Color(230, 230, 230, 230)  // 按下时稍微深一些
                            : new Color(255, 255, 255, 230); // 默认半透明白

                    // 阴影（始终绘制，但按下时偏移更小，看起来“压低”）
                    int shadowOffset = pressed ? 1 : SHADOW_SIZE;
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(shadowOffset, shadowOffset, w - shadowOffset * 2, h - shadowOffset * 2, ARC, ARC);

                    // 背景
                    g2.setColor(bgColor);
                    g2.fillRoundRect(offset, offset, w - shadowOffset, h - shadowOffset, ARC, ARC);

                    // 描边
                    g2.setColor(new Color(200, 200, 200));
                    g2.setStroke(new BasicStroke(0.6f));
                    g2.drawRoundRect(offset, offset, w - shadowOffset, h - shadowOffset, ARC, ARC);

                    g2.dispose();

                    // 文字和焦点虚线等，用 super 完成（super 内部又会调用 paintBorder）
                    super.paintComponent(g);
                }
            };

            // 关键设置：关闭原生填充和边框，让自绘生效
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            // 保留文字样式
            button.setFont(DEFAULT_FONT);
            button.setForeground(Color.BLACK);

            return button;
        }
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
}