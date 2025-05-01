package logic;

// src/logic/GradingSystem.java

public class GradingSystem {

    /**
     * 根据正确题数和题目总数，以及是否是进阶任务，计算得分。
     * @param correctCount 正确题数
     * @param totalCount   总题数
     * @param isAdvanced   是否进阶
     * @return 计算后的分数
     */
    public static int grade(int correctCount, int totalCount, boolean isAdvanced) {
        int baseScore = isAdvanced ? 2 : 1;               // 进阶每题双倍积分
        return correctCount * baseScore;
    }

    /**
     * 获取所有任务累计的总分（用于会话结束统计）。
     */
    public static int getTotalScore() {
        // 从 ProgressTracker 或 UserState 中汇总
        return ProgressTracker.getAllScores().stream().mapToInt(Integer::intValue).sum();
    }
}
