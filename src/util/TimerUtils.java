package util;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 倒计时工具类，适用于任务倒计时等交互。
 */
public class TimerUtils {
    private static Timer timer;

    /**
     * 启动倒计时。
     *
     * @param seconds 倒计时秒数
     * @param onTick  每秒回调一次，传入剩余时间（单位：秒）
     */
    public static void startCountdown(int seconds, Consumer<Integer> onTick) {
        final int[] remaining = { seconds };
        timer = new Timer(1000, e -> {
            remaining[0]--;
            onTick.accept(remaining[0]);
            if (remaining[0] <= 0) {
                timer.stop();
            }
        });
        timer.start();
    }

    /**
     * 停止倒计时。
     */
    public static void stopCountdown() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}