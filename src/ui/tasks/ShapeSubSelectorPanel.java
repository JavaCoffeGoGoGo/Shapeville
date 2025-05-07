package ui.tasks;

import data.TaskConfig;
import ui.MainFrame;
import util.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ShapeSubSelectorPanel extends JPanel {

    private MainFrame mainFrame;
    private int grade;

    public ShapeSubSelectorPanel(MainFrame mainFrame, int grade) {
        this.mainFrame = mainFrame;
        this.grade = grade;

        setLayout(new BorderLayout(20,20));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setOpaque(false);

        JLabel title = StyleUtils.createTitleLabel("图形识别任务选择");
        this.add(title, BorderLayout.NORTH);




        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        buttonPanel.setOpaque(false);

        JButton twoDButton = StyleUtils.createStyledButton("平面图形识别");
        twoDButton.addActionListener(e ->
                mainFrame.enterTask(grade, new TaskConfig("g12_2D_shape", "平面图形识别"))
        );

        JButton threeDButton = StyleUtils.createStyledButton("立体图形识别");
        threeDButton.addActionListener(e ->
                mainFrame.enterTask(grade, new TaskConfig("g12_3D_shape", "立体图形识别"))
        );

        buttonPanel.add(twoDButton);
        buttonPanel.add(threeDButton);

        this.add(buttonPanel, BorderLayout.CENTER);






        // 底部返回
        JButton backButton = StyleUtils.createStyledButton("Home");
        backButton.addActionListener(e -> mainFrame.returnToHome());
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setOpaque(false);
        backPanel.add(backButton);

        this.add(backPanel, BorderLayout.SOUTH);
    }
}