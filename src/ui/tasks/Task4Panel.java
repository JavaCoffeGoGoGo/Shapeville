package ui.tasks;

import ui.MainFrame;



public class Task4Panel extends AbstractTaskPanel {


    public Task4Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "请从面积计算/周长计算中选取一种开始作答";
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