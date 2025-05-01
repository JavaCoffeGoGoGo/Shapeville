package logic;

// src/logic/ProgressTracker.java

public class ProgressTracker {
    // 内部存储：Map<grade, Map<taskId, score>>
    private static Map<Integer, Map<String, Integer>> progressMap = new HashMap<>();

    /**
     * 保存单个任务的分数。
     */
    public static void saveProgress(int grade, String taskId, int score) {
        progressMap
                .computeIfAbsent(grade, g -> new HashMap<>())
                .put(taskId, score);
        UserState.persist(progressMap);
    }

    /** 返回指定年级总完成度（0~100） */
    public static int getGradeProgress(int grade) {
        Map<String, Integer> m = progressMap.getOrDefault(grade, Map.of());
        int sum = m.values().stream().mapToInt(Integer::intValue).sum();
        int max = m.size() * GradingSystem.grade(1,1,false);
        return max == 0 ? 0 : sum * 100 / max;
    }

    /** 返回所有任务分数列表 */
    public static List<Integer> getAllScores() {
        return progressMap.values().stream()
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toList());
    }

    /** 获取总体进度（所有年级平均） */
    public static int getOverallProgress() {
        List<Integer> scores = getAllScores();
        if (scores.isEmpty()) return 0;
        // 假设每题1分，满分 = 任务数
        return scores.stream().mapToInt(Integer::intValue).sum()
                * 100 / (scores.size() * GradingSystem.grade(1,1,false));
    }

    /** 是否完成过某任务 */
    public static boolean isTaskCompleted(int grade, String taskId) {
        return progressMap.getOrDefault(grade, Map.of()).containsKey(taskId);
    }
}
