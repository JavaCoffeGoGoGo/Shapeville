package util;

import java.awt.*;

/**
 * 图形绘制工具类，用于任务中的图形可视化（如评估题或辅助学习）。
 */
public class DrawingUtils {

    /**
     * 在指定的 Graphics2D 上绘制图形。
     *
     * @param g2d   绘图上下文
     * @param type  图形类型（如 "square", "rectangle", "circle", "triangle"）
     * @param params 参数：
     *               - square: 边长
     *               - rectangle: 宽, 高
     *               - circle: 半径
     *               - triangle: 底边, 高度
     */
    public static void drawShape(Graphics2D g2d, String type, double... params) {
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLUE);

        switch (type.toLowerCase()) {
            case "square":
                g2d.drawRect(0, 0, (int) params[0], (int) params[0]);
                break;

            case "rectangle":
                g2d.drawRect(0, 0, (int) params[0], (int) params[1]);
                break;

            case "circle":
                int r = (int) params[0];
                g2d.drawOval(0, 0, 2 * r, 2 * r);
                break;

            case "triangle":
                int base = (int) params[0];
                int height = (int) params[1];
                Polygon triangle = new Polygon();
                triangle.addPoint(0, height);
                triangle.addPoint(base / 2, 0);
                triangle.addPoint(base, height);
                g2d.drawPolygon(triangle);
                break;

            // 可扩展更多图形类型，如 trapezoid, pentagon 等
            default:
                g2d.drawString("未知图形: " + type, 10, 20);
                break;
        }
    }
}