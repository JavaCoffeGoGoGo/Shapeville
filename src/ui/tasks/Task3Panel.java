package ui.tasks;

import ui.MainFrame;



public class Task3Panel extends AbstractTaskPanel {


    public Task3Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "请下列四种基本图形中选取一种开始进行面积计算";
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