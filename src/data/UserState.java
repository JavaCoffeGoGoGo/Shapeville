package data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * UserState 用于记录和持久化用户的学习进度与当前任务状态。
 */
public class UserState {
    // 内存中缓存的用户进度：grade -> (taskId -> 完成次数 or 得分)
    private static Map<Integer, Map<String, Integer>> progressMap = new HashMap<>();

    // 当前正在进行的任务
    private static int currentGrade = -1;
    private static String currentTaskId = null;

    // 持久化文件路径（可根据需要更改）
    private static final String STATE_FILE = "user_state.dat";

    /**
     * 设置当前任务，记录到内存中，并可针对该任务初始化进度。
     * @param grade    年级
     * @param taskId   任务标识
     */
    public static void setCurrentTask(int grade, String taskId) {
        currentGrade = grade;
        currentTaskId = taskId;
        // 确保该任务在 map 中有初始记录
        progressMap.computeIfAbsent(grade, g -> new HashMap<>())
                .putIfAbsent(taskId, 0);
    }

    /**
     * 获取当前任务的标识，供恢复或展示使用。
     * @return 当前任务 taskId
     */
    public static String getCurrentTask() {
        return currentTaskId;
    }

    /**
     * 更新指定任务的进度，例如完成次数或累计得分。
     * @param grade    年级
     * @param taskId   任务标识
     * @param value    新的进度值
     */
    public static void updateProgress(int grade, String taskId, int value) {
        progressMap.computeIfAbsent(grade, g -> new HashMap<>())
                .put(taskId, value);
    }

    /**
     * 查询指定任务的当前进度值。
     * @param grade    年级
     * @param taskId   任务标识
     * @return 当前记录的进度值，若无记录则返回 0
     */
    public static int getProgress(int grade, String taskId) {
        return progressMap.getOrDefault(grade, new HashMap<>())
                .getOrDefault(taskId, 0);
    }

    /**
     * 持久化当前进度到磁盘（对象序列化示例）。
     */
    public static void persist() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STATE_FILE))) {
            out.writeObject(progressMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动时加载历史进度到内存。
     */
    @SuppressWarnings("unchecked")
    public static void load() {
        File file = new File(STATE_FILE);
        if (!file.exists()) {
            progressMap = new HashMap<>();
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof Map) {
                progressMap = (Map<Integer, Map<String, Integer>>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            progressMap = new HashMap<>();
        }
    }

    /**
     * 清空所有本地缓存的进度（用于调试或重置）。
     */
    public static void clearAllProgress() {
        progressMap.clear();
    }
}