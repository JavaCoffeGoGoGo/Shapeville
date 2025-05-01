package logic;


import ui.MainFrame;

public class TaskRegistry {
    public static List<String> getAllTaskNames() {
        return List.of("Task1", "Task2", "Task3", "Task4", "Bonus1", "Bonus2");
    }

    public static JPanel createTaskPanel(String taskName, MainFrame frame) {
        return switch(taskName) {
            case "Task1" -> new Task1Panel(frame);
            case "Task2" -> new Task2Panel(frame);
            case "Task3" -> new Task3Panel(frame);
            case "Task4" -> new Task4Panel(frame);
            case "Bonus1" -> new Bonus1Panel(frame);
            case "Bonus2" -> new Bonus2Panel(frame);
            default -> new JPanel();  // fallback
        };
    }
}
