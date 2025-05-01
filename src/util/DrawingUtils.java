package util;

// src/util/DrawingUtils.java

public class DrawingUtils {

    /**
     * 在给定 Graphics2D 上绘制标准几何图形。
     * @param g2d  绘图上下文
     * @param type 图形类型，如 "square","circle","triangle"
     * @param params 参数（边长、半径、高度等）
     */
    public static void drawShape(Graphics2D g2d, String type, double... params) {
        g2d.setStroke(new BasicStroke(3));
        switch (type) {
            case "square":
                g2d.drawRect(0,0,(int)params[0],(int)params[0]);
                break;
            case "rectangle":
                g2d.drawRect(0,0,(int)params[0],(int)params[1]);
                break;
            case "circle":
                g2d.drawOval(0,0,(int)(2*params[0]),(int)(2*params[0]));
                break;
            case "triangle":
                // 根据底和高绘制三角形
                break;
            // … 其他类型
        }
    }
}