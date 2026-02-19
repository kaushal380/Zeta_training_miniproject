package consoleUI;

import models.Project;
import models.Task;
import models.User;
import models.enums.TaskStatus;
import services.ProjectService;
import services.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BuilderDashboard {

    public static void start(User builder, TaskService taskService, ProjectService projectService, Scanner scanner) {

        while (true) {

            System.out.println("\n===== BUILDER DASHBOARD =====");
            System.out.println("1. View My Projects");
            System.out.println("2. View My Tasks");
            System.out.println("3. Update Task Status");
            System.out.println("4. Exit");

            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> viewMyProjects(builder, projectService);
                    case "2" -> viewMyTasks(builder, taskService);
                    case "3" -> updateTaskStatus(builder, taskService, scanner);
                    case "4" -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void viewMyProjects(User builder, ProjectService projectService) {

        List<Project> myProjects = new ArrayList<>();

        for (Project p : projectService.viewAllProjects().values()) {
            if (p.getBuilderIds().contains(builder.getId())) {
                myProjects.add(p);
            }
        }

        if (myProjects.isEmpty()) {
            System.out.println("No projects assigned.");
            return;
        }

        System.out.println("\n===== MY PROJECTS =====");

        for (int i = 0; i < myProjects.size(); i++) {
            Project p = myProjects.get(i);
            System.out.println((i + 1) + ". " + p.getName() + " | Status: " + p.getStatus());
        }
    }

    private static List<Task> getMyTasks(User builder, TaskService taskService) {

        return new ArrayList<>(taskService.viewTasksByBuilder(builder.getId()).values());
    }

    private static void viewMyTasks(User builder, TaskService taskService) {

        List<Task> tasks = getMyTasks(builder, taskService);

        if (tasks.isEmpty()) {
            System.out.println("No tasks assigned.");
            return;
        }

        System.out.println("\n===== MY TASKS =====");

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            System.out.println((i + 1) + ". " + t.getDescription() + " | Status: " + t.getStatus());
        }
    }

    private static void updateTaskStatus(User builder, TaskService taskService, Scanner scanner) {

        List<Task> tasks = getMyTasks(builder, taskService);

        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        System.out.println("\n===== SELECT TASK =====");

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            System.out.println((i + 1) + ". "
                    + t.getDescription()
                    + " | Status: " + t.getStatus());
        }

        int index = selectIndex(scanner, tasks.size());

        Task selectedTask = tasks.get(index);

        TaskStatus status = selectStatus(scanner);

        boolean updated = taskService.updateTaskStatus(builder, selectedTask.getId(), status);

        if (updated) {
            System.out.println("Task status updated successfully.");
        } else {
            System.out.println("Update failed.");
        }
    }

    private static int selectIndex(Scanner scanner, int size) {

        while (true) {
            System.out.print("Select number: ");
            String input = scanner.nextLine().trim();

            if (input.matches("\\d+")) {
                int index = Integer.parseInt(input);
                if (index >= 1 && index <= size) {
                    return index - 1;
                }
            }

            System.out.println("Invalid selection.");
        }
    }

    private static TaskStatus selectStatus(Scanner scanner) {

        while (true) {

            System.out.println("""
                    Select Status:
                    1. IN_PROGRESS
                    2. COMPLETED
                    """);

            System.out.print("Choose option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    return TaskStatus.IN_PROGRESS;
                }
                case "2" -> {
                    return TaskStatus.COMPLETED;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
