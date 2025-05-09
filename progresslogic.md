
# Shapeville 项目进度设计（按学段区分）

## 📊 得分结构

评分机制不变，依然按任务难度等级和尝试次数计分：

| 尝试次数       | Basic 分值 | Advanced 分值 |
|----------------|------------|---------------|
| 第一次正确     | 3          | 6             |
| 第二次正确     | 2          | 4             |
| 第三次正确     | 1          | 2             |
| 全部错误       | 0          | 0             |

⸻

## 📚 各学段任务及最大得分统计

### 🟦 Key Stage 1（1-2 年级）

| 任务名                 | 题量 | 等级  | 每题满分 | 总分值    |
|------------------------|------|-------|----------|--------|
| Task 1: 2D 图形识别    | 11   | Basic | 3        | 33     |
| Task 1: 3D 图形识别        | 8    | Advanced | 6        | 48     |
| Task 2: 角类型识别     | 4    | Basic | 3        | 12     |
| **小计**               | 15   | Basic |          | **93** |

⸻

### 🟨 Key Stage 2（3-4 年级）

| 任务名                     | 题量 | 等级     | 每题满分 | 总分值 |
|----------------------------|------|----------|----------|--------|

| Task 3: 图形面积计算       | 4    | Basic    | 3        | 12     |
| Task 4: 圆的面积/周长计算  | 4    | Basic    | 3        | 12     |
| Bonus 1: 复合图形面积计算  | 9    | Advanced | 6        | 54     |
| Bonus 2: 扇形面积和弧长计算| 8    | Advanced | 6        | 48     |
| **小计**                   | 33   | 混合     |          | **126** |

⸻

## 🧮 分学段进度算法设计

提供两个独立的计算方法用于首页显示 Key Stage 1 和 Key Stage 2 的进度：

```java
// KS1（1-2年级）进度计算
public int calculateKS1Progress(int ks1Score) {
    final int KS1_MAX_SCORE = 45;
    if (KS1_MAX_SCORE <= 0) return 0;
    double percentage = ((double) ks1Score / KS1_MAX_SCORE) * 100;
    return (int) Math.min(percentage, 100);
}

// KS2（3-4年级）进度计算
public int calculateKS2Progress(int ks2Score) {
    final int KS2_MAX_SCORE = 174;
    if (KS2_MAX_SCORE <= 0) return 0;
    double percentage = ((double) ks2Score / KS2_MAX_SCORE) * 100;
    return (int) Math.min(percentage, 100);
}
```

⸻

## 🖥️ 首页进度条设计建议

- 显示两个独立进度条：
  ```
  🔵 Key Stage 1 进度：███▓▓▓▓▓▓ 33%
  🟡 Key Stage 2 进度：██████▓▓ 72%
  ```

- 点击"End Session"时，分别展示学段得分，例如：
  ```
  🎉 您本次成绩：
  Key Stage 1：已获得 36 分（满分 45）
  Key Stage 2：已获得 108 分（满分 174）
  感谢您的参与，Goodbye!
  ```
