package util;

// src/util/TimerUtils.java

public class TimerUtils {
    private static javax.swing.Timer timer;

    /**
     * 启动倒计时，回调参数为剩余秒数。
     */
    public static void startCountdown(int seconds, Consumer<Integer> onTick) {
        final int[] remaining = { seconds };
        timer = new javax.swing.Timer(1000, e -> {
            remaining[0]--;
            onTick.accept(remaining[0]);
            if (remaining[0] <= 0) {
                timer.stop();
            }
        });
        timer.start();
    }

    /** 停止当前倒计时 */
    public static void stopCountdown() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}
