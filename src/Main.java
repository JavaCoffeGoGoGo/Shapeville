// 导入自定义的 MainFrame 类
import ui.MainFrame;

/* ⚠️导入 SwingUtilities 类，它属于 Java Swing GUI 库
 用于管理事件分发线程（EDT），这是 Swing 控件更新和事件处理的主线程
 */
import javax.swing.SwingUtilities;

/**
 * 程序入口类
 */
public class Main {
    public static void main(String[] args) {
        /* ⚠️SwingUtilities.invokeLater(...)
        把任务交给 Swing 的事件调度线程执行（防止线程冲突）
        */
        /* ⚠️new Runnable() { ... }
        创建了一个匿名内部类，实现了 Runnable 接口
        */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame(); // 创建主界面窗口
                frame.setVisible(true);            // 显示窗口
            }
        });
    }
}