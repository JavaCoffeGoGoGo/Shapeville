package data;

// src/data/FanShapeConfig.java

public class FanShapeConfig {
    private String name;
    private String imagePath;
    private double radius;
    private double angle;  // 单位：度

    public FanShapeConfig(String name, String imagePath, double radius, double angle) {
        this.name = name;
        this.imagePath = imagePath;
        this.radius = radius;
        this.angle = angle;
    }

    public double calculateArea() {
        return Math.PI * radius * radius * angle / 360.0;
    }
    public double calculateArcLength() {
        return 2 * Math.PI * radius * angle / 360.0;
    }
    public String getAreaFormula() {
        return String.format("π×%.1f²×%.1f/360 = %.1f", radius, angle, calculateArea());
    }
    public String getArcLengthFormula() {
        return String.format("2π×%.1f×%.1f/360 = %.1f", radius, angle, calculateArcLength());
    }
    public String getImagePath() { return imagePath; }
}
