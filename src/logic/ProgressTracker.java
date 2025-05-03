package logic;

import data.UserState;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ProgressTracker 负责封装与进度相关的读取、保存与计算逻辑。
 */
public class ProgressTracker {
    // 内部缓存的进度数据（grade -> (taskId -> score)）
    private static Map<Integer, Map<String, Integer>> progressMap = new HashMap<>();

    // 单题最高分，用于计算进度百分比
    private static final int MAX_SCORE_PER_TASK;

    static {
        // 初始化：加载持久化进度到 UserState，然后从中拷贝一份到本地缓存
        UserState.load();
        // 假设在 UserState 中有方法获取整个进度 Map（如果没有，可直接从文件或 UserState 内部补充）
        // 这里简化：progressMap 与 UserState 共享
        // （若 UserState 提供 getAllProgress()，则直接引用即可）
        // 由于我们之前在 UserState 中没提供此方法，暂以空 Map 为起点
        progressMap = new HashMap<>();
        // 单题最大分数，假设 grade=1, attempts=1 且 isAdvanced=false 得到最低案例
        MAX_SCORE_PER_TASK = GradingSystem.grade(1, 1, false);
    }

    /**
     * 保存单个任务的分数，并立即持久化。
     * @param grade   年级编号
     * @param taskId  任务标识
     * @param score   本次得分
     */
    public static void saveProgress(int grade, String taskId, int score) {
        progressMap
                .computeIfAbsent(grade, g -> new HashMap<>())
                .put(taskId, score);
        // 同步到 UserState 并持久化
        UserState.updateProgress(grade, taskId, score);
        UserState.persist();
    }

    /**
     * 返回指定年级的完成度百分比（0~100）。
     * 计算方式：该年级所有任务实际得分之和 / （任务数 * 单题最大分） * 100。
     * @param grade 年级编号
     * @return 完成度百分比
     */
    public static int getGradeProgress(int grade) {
        Map<String, Integer> m = progressMap.getOrDefault(grade, Collections.emptyMap());
        if (m.isEmpty()) return 0;
        int sum = m.values().stream().mapToInt(Integer::intValue).sum();
        int max = m.size() * MAX_SCORE_PER_TASK;
        return max == 0 ? 0 : sum * 100 / max;
    }

    /**
     * 获取所有任务的得分列表。
     * @return 所有任务分数的列表
     */
    public static List<Integer> getAllScores() {
        return progressMap.values().stream()
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toList());
    }

    /**
     * 计算整体进度（所有年级平均）。
     * 计算方式：所有任务得分总和 / （任务总数 * 单题最大分） * 100。
     * @return 整体进度百分比
     */
    public static int getOverallProgress() {
        List<Integer> scores = getAllScores();
        if (scores.isEmpty()) return 0;
        int totalScore = scores.stream().mapToInt(Integer::intValue).sum();
        int taskCount = scores.size();
        int maxTotal = taskCount * MAX_SCORE_PER_TASK;
        return maxTotal == 0 ? 0 : totalScore * 100 / maxTotal;
    }

    /**
     * 判断指定任务是否已有记录（即是否完成过）。
     * @param grade  年级编号
     * @param taskId 任务标识
     * @return true if completed, false otherwise
     */
    public static boolean isTaskCompleted(int grade, String taskId) {
        return progressMap.getOrDefault(grade, Collections.emptyMap()).containsKey(taskId);
    }
}