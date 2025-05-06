package logic;

import data.UserState;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 模块说明：
 * GradingSystem 是项目的评分规则核心模块
 */

public class GradingSystem {

    // 1. 给出单个任务不同次尝试的得分：
    public static int grade(int attempt, boolean isAdvanced) {
        // 根据 Basic/Advanced 模式分别取分
        if (isAdvanced) {
            // Advanced 模式：1→6，2→4，3→2，其余→0
            return switch (attempt) {
                case 1 -> 6;
                case 2 -> 4;
                case 3 -> 2;
                default -> 0;
            };
        } else {
            // Basic 模式：1→3，2→2，3→1，其余→0
            return switch (attempt) {
                case 1 -> 3;
                case 2 -> 2;
                case 3 -> 1;
                default -> 0;
            };
        }
    }

    // 2. 获取所有年级累计得分
    public static int getTotalScore() {
        List<Integer> scores = getAllScores();
        // 将 List<Integer> 转为 IntStream 并求和
        return scores.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    // 3. 把所有年级里的所有任务分数，整合成一个大列表
    public static List<Integer> getAllScores() {
        // 从 UserState 中取出所有年级的进度 Map，然后 flatMap 到一个得分流，再收集为 List
        return UserState.getProgressMap()
                .values()
                .stream()//把每个成绩表变成流
                //下面这步达到的效果是：
                //[
                //  { "task1": 90, "task2": 100 },
                //  { "task3": 85, "task4": 95 }
                //]
                //→
                //[
                //  [90, 100],
                //  [85, 95]
                //]
                //→
                //[90, 100, 85, 95]
                .flatMap(map -> map//把变成流的列表中的每个元素（都是一个Map）我们就“临时取名”为 map
                        .values()//然后对它做 map.values().stream()
                        .stream())
                .collect(Collectors.toList());//收集处理后的流，转成 List<Integer>
    }
}