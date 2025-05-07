package util;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * 💡模块说明：
 * TimerUtils 是一个 Swing 定时器工具类，用于实现灵活倒计时功能。
 * 支持开始、暂停、恢复与结束回调，适用于任务限时控制与 UI 同步。
 */
public class TimerUtils {
    private static Timer timer;
    private static int[] remaining; // 剩余时间（秒）

    /**
     * 启动倒计时。
     * @param seconds 倒计时总秒数
     * @param onTick 每秒触发回调（传入剩余秒数）
     * @param onFinish 倒计时结束时触发的回调（可选，可为 null）
     */
    public static void startCountdown(int seconds, Consumer<Integer> onTick, Runnable onFinish) {
        stopCountdown(); // 若已有计时器，先停掉
        remaining = new int[]{seconds};

        timer = new Timer(1000, e -> {
            remaining[0]--;
            onTick.accept(remaining[0]);
            if (remaining[0] <= 0) {
                timer.stop();
                if (onFinish != null) {
                    onFinish.run();
                }
            }
        });

        timer.start();
    }

    /**
     * 主动停止倒计时（例如任务中断、跳题等）。
     */
    public static void stopCountdown() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * 获取当前剩余秒数（适用于保存状态/展示）。
     */
    public static int getRemainingSeconds() {
        return remaining != null ? remaining[0] : 0;
    }

    /**
     * 暂停倒计时（可用于用户临时离开/切换页面）。
     */
    public static void pauseCountdown() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * 恢复倒计时（配合 pause 使用）。
     * @param onTick 每秒回调
     * @param onFinish 倒计时结束回调
     */
    public static void resumeCountdown(Consumer<Integer> onTick, Runnable onFinish) {
        if (remaining == null || remaining[0] <= 0) return;

        timer = new Timer(1000, e -> {
            remaining[0]--;
            onTick.accept(remaining[0]);
            if (remaining[0] <= 0) {
                timer.stop();
                if (onFinish != null) {
                    onFinish.run();
                }
            }
        });

        timer.start();
    }
}