package ui.tasks;

import ui.MainFrame;



public class Bonus2Panel extends AbstractTaskPanel {


    public Bonus2Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "请选择一个开始：弧长计算/面积计算";
    }

    @Override
    protected void startTask() {

    }

    @Override
    protected void onSubmit() {

    }

    @Override
    protected void saveAndFinish() {

    }

    @Override
    protected String getEncouragement() {
        return "没关系，重来一次就会更好～";
    }

}