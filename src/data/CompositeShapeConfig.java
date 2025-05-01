package data;

// src/data/CompositeShapeConfig.java

public class CompositeShapeConfig {
    private String name;
    private String imagePath;
    private List<ShapeData> parts;  // 各部分基本形状

    public CompositeShapeConfig(String name, String imagePath, List<ShapeData> parts) {
        this.name = name;
        this.imagePath = imagePath;
        this.parts = parts;
    }

    /** 计算复合图形总面积 */
    public double calculateArea() {
        return parts.stream().mapToDouble(ShapeData::calculateArea).sum();
    }

    /** 返回公式解析文本 */
    public String getFormulaExplanation() {
        StringBuilder sb = new StringBuilder();
        for (ShapeData p : parts) {
            sb.append(p.getType()).append("：").append(p.getFormula()).append("\n");
        }
        return sb.toString();
    }

    public String getImagePath() { return imagePath; }
}
