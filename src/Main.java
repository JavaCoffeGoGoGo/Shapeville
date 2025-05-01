public class Main {
    public static void main(String[] args) {
        // 启动程序
        SwingUtilities.invokeLater(() -> {
            // 创建主窗体并显示
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}