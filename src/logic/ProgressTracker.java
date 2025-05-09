package logic;

import data.UserState;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ProgressTracker：用户任务进度核心管理类
 */
public class ProgressTracker {

    // —— 之前定义的每个 taskId 的满分映射不变 ——
    private static final Map<String,Integer> TASK_MAX_SCORE = Map.ofEntries(
            Map.entry("g12_2D_shape",    11 * 3),
            Map.entry("g12_3D_shape",     8 * 6),
            Map.entry("g12_angle",        4 * 3),
            Map.entry("g34_area_basic",   4 * 3),
            Map.entry("g34_circle",       4 * 3),
            Map.entry("g34_area_complex", 9 * 6),
            Map.entry("g34_src",          8 * 6)
    );

    // KS1 / KS2 各自的 taskId 集合
    private static final Set<String> KS1_TASKS = Set.of(
            "g12_2D_shape", "g12_3D_shape", "g12_angle"
    );
    private static final Set<String> KS2_TASKS = Set.of(
            "g34_area_basic", "g34_circle", "g34_area_complex", "g34_src"
    );

    // （原有）全局满分
    private static final int TOTAL_MAX_SCORE =
            TASK_MAX_SCORE.values().stream().mapToInt(i->i).sum();

    static { UserState.load(); }

    public static void saveProgress(int grade, String taskId, int score) {
        UserState.updateProgress(grade, taskId, score);
        UserState.persist();
    }

    /** 计算指定 taskId 集合中的进度百分比 */
    private static int calcProgressForTasks(Set<String> taskIds) {
        // 1) 当前得分（只统计这些 taskIds）
        int totalScore = UserState.getProgressMap().values().stream()
                .flatMap(m -> m.entrySet().stream())
                .filter(e -> taskIds.contains(e.getKey()))
                .mapToInt(Map.Entry::getValue)
                .sum();

        // 2) 理论满分（只加这些 taskIds 对应的满分）
        int maxScore = taskIds.stream()
                .mapToInt(id -> TASK_MAX_SCORE.getOrDefault(id, 0))
                .sum();

        if (maxScore == 0) return 0;
        int pct = totalScore * 100 / maxScore;
        return Math.min(pct, 100);
    }

    /** Grade 1-2 的整体进度 */
    public static int getProgressKS1() {
        return calcProgressForTasks(KS1_TASKS);
    }

    /** Grade 3-4 的整体进度 */
    public static int getProgressKS2() {
        return calcProgressForTasks(KS2_TASKS);
    }

    /** （保留）全局进度 */
    public static int getOverallProgress() {
        int totalScore = UserState.getProgressMap().values().stream()
                .flatMap(m -> m.values().stream()).mapToInt(i->i).sum();
        int pct = totalScore * 100 / TOTAL_MAX_SCORE;
        return Math.min(pct, 100);
    }
    /** Grade 1-2 的原始总分（0 – 45） */
    public static int getRawScoreKS1() {
        return UserState.getProgressMap().values().stream()
                .flatMap(m -> m.entrySet().stream())
                .filter(e -> KS1_TASKS.contains(e.getKey()))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    /** Grade 3-4 的原始总分（0 – 174） */
    public static int getRawScoreKS2() {
        return UserState.getProgressMap().values().stream()
                .flatMap(m -> m.entrySet().stream())
                .filter(e -> KS2_TASKS.contains(e.getKey()))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }
}