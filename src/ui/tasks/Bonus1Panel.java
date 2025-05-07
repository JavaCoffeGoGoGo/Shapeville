package ui.tasks;

import ui.MainFrame;



public class Bonus1Panel extends AbstractTaskPanel {


    public Bonus1Panel(MainFrame mainFrame, int grade, String taskId) {
        super(mainFrame, grade, taskId);
    }

    @Override
    protected String getTaskTitle() {
        return "请从下列九个复合图形中选取一个开始";
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