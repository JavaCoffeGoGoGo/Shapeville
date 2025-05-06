package util;

import java.awt.*;
import javax.swing.*;

public class DrawingAngleUtils {

    // 该方法绘制给定角度的角形，角度单位为度（0-360）
    public static void drawAngle(Graphics g, int angle) {
        // 创建一个旋转的图形角
        int width = 200; // 绘制区域宽度
        int height = 200; // 绘制区域高度
        int centerX = width / 2; // 图形中心x坐标
        int centerY = height / 2; // 图形中心y坐标

        // 计算角度的起始和结束位置
        double radianStart = Math.toRadians(0); // 从X轴开始
        double radianEnd = Math.toRadians(angle); // 角度转换为弧度

        // 设置绘图的颜色
        g.setColor(Color.BLACK);

        // 绘制半圆的弧线部分
        g.drawArc(centerX - 100, centerY - 100, 200, 200, 0, angle);

        // 绘制两个边的线条
        int x1 = centerX + (int) (100 * Math.cos(radianStart));
        int y1 = centerY - (int) (100 * Math.sin(radianStart));

        int x2 = centerX + (int) (100 * Math.cos(radianEnd));
        int y2 = centerY - (int) (100 * Math.sin(radianEnd));

        g.drawLine(centerX, centerY, x1, y1);
        g.drawLine(centerX, centerY, x2, y2);

        // 绘制角度标签（角度）
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString(angle + "°", centerX + 10, centerY - 10);
    }
}