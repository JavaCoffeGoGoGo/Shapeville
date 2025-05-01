package data;

// src/data/UserState.java

public class UserState {
    /**
     * 将进度持久化（如写入文件或数据库），格式可为 JSON。
     */
    public static void persist(Map<Integer, Map<String, Integer>> progressMap) {
        // TODO: 将 progressMap 序列化到磁盘
    }

    /** 启动时加载历史进度 */
    public static Map<Integer, Map<String, Integer>> load() {
        // TODO: 反序列化文件内容到 Map
        return new HashMap<>();
    }
}
