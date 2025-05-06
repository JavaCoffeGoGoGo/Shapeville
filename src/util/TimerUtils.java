package util;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 💡模块说明：
 * TimerUtils 是一个 Swing 定时器工具类，用于实现简单的倒计时功能。
 * 常用于任务模块中限时答题、节奏控制等交互场景。
 */
public class TimerUtils {
    // Swing 计时器（每秒触发一次 ActionEvent）
    private static Timer timer;

    //1. 启动倒计时任务。

    public static void startCountdown(int seconds, Consumer<Integer> onTick) {
        // 使用数组包裹整型变量，以便在 lambda 表达式中修改（Java 限制）
        final int[] remaining = { seconds };

        // 创建定时器，每隔 1000 毫秒执行一次
        timer = new Timer(1000, e -> {
            remaining[0]--;                     // 每秒递减剩余时间
            onTick.accept(remaining[0]);       // 执行回调逻辑（例如更新 UI 显示）
            if (remaining[0] <= 0) {
                timer.stop();                  // 倒计时结束后停止定时器
            }
        });

        timer.start(); // 启动倒计时
    }

    // 2. 主动停止倒计时（例如在任务提前结束或强制终止时调用）。
    public static void stopCountdown() {
        // 确保计时器存在且仍在运行
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}