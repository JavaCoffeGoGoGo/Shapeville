package ui.tasks;

import ui.MainFrame;



public class Task2Panel extends AbstractTaskPanel {


    public Task2Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "请判断下方角度的类型";
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