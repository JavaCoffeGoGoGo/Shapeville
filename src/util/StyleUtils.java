package util;

import javax.swing.*;
import java.awt.*;

/**
 * 全局样式工具类，用于统一设置 Swing 元素样式，增强可访问性与风格一致性。
 */
public class StyleUtils {

    // 全局字体设置
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

    // 色盲友好颜色方案（对比强、避免红绿配）
    public static final Color PRIMARY_COLOR = new Color(0, 102, 204);      // 蓝色
    public static final Color ACCENT_COLOR = new Color(255, 153, 0);       // 橙色
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // 浅灰
    public static final Color TEXT_COLOR = Color.BLACK;

    /**
     * 应用全局样式到任意 AWT/Swing 组件，包括 JFrame、JPanel 等。
     */
    public static void applyGlobalStyle(Component component) {
        // 通用属性：字体、背景、前景色
        component.setFont(DEFAULT_FONT);
        component.setBackground(BACKGROUND_COLOR);
        component.setForeground(TEXT_COLOR);

        // 如果是容器，设置默认布局
        if (component instanceof JPanel) {
            ((JPanel) component).setLayout(new BorderLayout());
        }

        // 使用 UIManager 设置所有 Swing 组件的默认样式
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Label.font", DEFAULT_FONT);
        UIManager.put("Button.font", DEFAULT_FONT);
        UIManager.put("TextField.font", DEFAULT_FONT);
        UIManager.put("TextArea.font", DEFAULT_FONT);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);

        // 刷新整个组件树的 UI（针对 Swing 组件）
        if (component instanceof JComponent) {
            SwingUtilities.updateComponentTreeUI((JComponent) component);
        }

        // 如果根组件是 JFrame，还需要刷新它的子组件
        if (component instanceof JFrame) {
            SwingUtilities.updateComponentTreeUI(((JFrame) component).getRootPane());
        }
    }

    /**
     * 快速创建统一风格按钮。
     */
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(DEFAULT_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    /**
     * 快速创建标题标签。
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }
    /**
     * 创建任务说明标签，通常位于面板顶部，与返回按钮并列。
     */
    public static JLabel createInstructionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }
    /**
     * 创建用于正误反馈的标签（绿色为正确，红色为错误）。
     */
    public static JLabel createFeedbackLabel(String text, boolean isCorrect) {
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        label.setForeground(isCorrect ? new Color(0, 153, 0) : Color.RED);
        return label;
    }
}