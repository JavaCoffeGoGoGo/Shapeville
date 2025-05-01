package util;

// src/utils/StyleUtils.java

public class StyleUtils {

    // 全局字体设置
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

    // 色盲友好颜色方案（对比强、避免红绿配）
    public static final Color PRIMARY_COLOR = new Color(0, 102, 204);      // 蓝色
    public static final Color ACCENT_COLOR = new Color(255, 153, 0);       // 橙色
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // 浅灰
    public static final Color TEXT_COLOR = Color.BLACK;

    // 应用全局样式到 JFrame
    public static void applyGlobalStyle(JFrame frame) {
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Label.font", DEFAULT_FONT);
        UIManager.put("Button.font", DEFAULT_FONT);
        UIManager.put("TextField.font", DEFAULT_FONT);
        UIManager.put("TextArea.font", DEFAULT_FONT);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);

        // 立即刷新样式
        SwingUtilities.updateComponentTreeUI(frame);
    }

    // 快速创建统一风格按钮
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(DEFAULT_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    // 快速创建统一标题
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    // 创建强调提示标签（如正误反馈）
    public static JLabel createFeedbackLabel(String text, boolean isCorrect) {
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        label.setForeground(isCorrect ? new Color(0, 153, 0) : Color.RED);
        return label;
    }
}
