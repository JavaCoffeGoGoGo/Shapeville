
# Shapeville 项目进度条设计（得分驱动逻辑）

## 🎯 总体目标

设计一个基于用户得分的进度条逻辑，用于在 Shapeville 应用首页展示用户在整个 Session 中的学习进度。该进度条应实时更新并反映用户当前在所有任务中获得的得分与总得分上限之间的比例。

---

## 📊 得分结构

所有任务根据“评分等级（Basic / Advanced）”与“尝试次数”赋予不同分值。

### ✅ 评分机制：

| 尝试次数 | Basic 分值 | Advanced 分值 |
|----------|-------------|----------------|
| 第一次正确 | 3         | 6             |
| 第二次正确 | 2         | 4             |
| 第三次正确 | 1         | 2             |
| 全部错误   | 0         | 0             |

---

## 📚 各任务最大得分统计

| 任务名                            | 题量 | 等级     | 每题满分 | 总分值 |
|----------------------------------|------|----------|----------|--------|
| Task 1: 2D 图形识别               | 11   | Basic    | 3        | 33     |
| Task 1: 3D 图形识别               | 8    | Advanced | 6        | 48     |
| Task 2: 角类型识别                | 4    | Basic    | 3        | 12     |
| Task 3: 图形面积计算              | 4    | Basic    | 3        | 12     |
| Task 4: 圆的面积/周长计算         | 4    | Basic    | 3        | 12     |
| Bonus 1: 复合图形面积计算         | 9    | Advanced | 6        | 54     |
| Bonus 2: 扇形面积和弧长计算       | 8    | Advanced | 6        | 48     |
| **总计**                          | 48   | 混合     |          | **219** |

---

## 🧮 算法逻辑

### 方法签名（Java 示例）：

```java
/**
 * 计算当前用户得分进度百分比
 * @param currentScore 当前得分（通过各题记录动态累加）
 * @param maxScore 总分（固定为219）
 * @return 进度百分比（0~100）
 */
public int calculateScoreBasedProgress(int currentScore, int maxScore) {
    if (maxScore <= 0) return 0;
    double percentage = ((double) currentScore / maxScore) * 100;
    return (int) Math.min(percentage, 100); // 防止超过100%
}


⸻

🗃️ 示例得分数据结构（建议）

class QuestionAttempt {
    boolean isCorrect;
    int attemptCount; // 1~3
    boolean isAdvanced;
    
    int getScore() {
        if (!isCorrect) return 0;
        if (isAdvanced) {
            return switch (attemptCount) {
                case 1 -> 6;
                case 2 -> 4;
                case 3 -> 2;
                default -> 0;
            };
        } else {
            return switch (attemptCount) {
                case 1 -> 3;
                case 2 -> 2;
                case 3 -> 1;
                default -> 0;
            };
        }
    }
}

```
---

#####  🧩 进度条使用建议
1. 进度条应每完成一题后实时刷新。
2. 提供悬停提示（Tooltip）：“当前得分 / 总分（219）”
3. 可配合颜色/表情提示鼓励，如：
* < 30% → 红色 + “继续努力！”
* 30%~70% → 黄色 + “进展不错！”
* \> 70% → 绿色 + “棒极了！”

---

✅ 示例前端伪代码（简化）

```java
JProgressBar progressBar = new JProgressBar();
progressBar.setMinimum(0);
progressBar.setMaximum(100);
progressBar.setValue(calculateScoreBasedProgress(currentScore, 219));
progressBar.setString(currentScore + " / 219");
progressBar.setStringPainted(true);
```



---

* 📝 注意事项
* 	•	若有 Bonus 任务未解锁，需动态调整 maxScore。
* 	•	可在 session 开始时记录哪些任务解锁，用于动态设置总分上限。
* 	•	可拓展为展示多个进度条（例如 Task1 专属、Bonus 专属等）。

