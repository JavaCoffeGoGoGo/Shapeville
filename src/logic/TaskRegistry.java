package logic;

import ui.MainFrame;
import ui.tasks.*;
import javax.swing.*;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * 任务注册表：使用 taskId 作为 key，按需实例化任务面板。
 */
public class TaskRegistry {
    // key = TaskConfig.getId(), value = 面板构造函数 (frame, grade) -> JPanel
    private static final Map<String, BiFunction<MainFrame, Integer, JPanel>> registry = Map.of(
            // 基础任务
            "g1_shape",    Task1Panel::new
//            "g1_area",     Task2Panel::new,
//            "g2_angle",    Task3Panel::new,
//            "g2_composite",Bonus1Panel::new,
//            // … 请根据实际 panel 类补全
//            "g3_sector",   Task4Panel::new,
//            "g3_compare",  Task5Panel::new,
//            "g4_advanced", Bonus2Panel::new
    );

    /** 返回所有已注册的 taskId 集合 */
    public static Set<String> getAllTaskIds() {
        return registry.keySet();
    }

    /**
     * 根据 taskId、主窗体和年级，按需实例化对应的任务面板。
     * @param taskId  如 "g1_shape"
     * @param frame   MainFrame 实例
     * @param grade   当前年级
     */
    public static JPanel createTaskPanel(String taskId, MainFrame frame, int grade) {
        BiFunction<MainFrame, Integer, JPanel> ctor = registry.get(taskId);
        if (ctor != null) {
            return ctor.apply(frame, grade);
        } else {
            // 未注册的任务，返回一个空面板或提示面板
            JPanel panel = new JPanel();
            panel.add(new JLabel("未找到任务: " + taskId));
            return panel;
        }
    }
}