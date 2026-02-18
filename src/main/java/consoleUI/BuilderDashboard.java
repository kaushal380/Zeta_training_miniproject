package consoleUI;

import models.Project;
import models.Task;
import models.User;
import models.enums.TaskStatus;
import services.ProjectService;
import services.TaskService;

import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BuilderDashboard {

    public static void start(User builder, TaskService taskService, ProjectService projectService, Scanner scanner) {

        while (true) {

            System.out.println("\n===== BUILDER DASHBOARD =====");
            System.out.println("1. View My Projects");
            System.out.println("2. View My Tasks");
            System.out.println("3. Update Task Status");
            System.out.println("4. Exit");

            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {

                    case "1" -> viewMyProjects(builder, projectService);

                    case "2" -> viewMyTasks(builder, taskService);

                    case "3" -> updateTaskStatus(builder, taskService, scanner);

                    case "4" -> {
                        System.out.println("Exiting Builder Dashboard...");
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

        Map<String, Project> projects = projectService.viewAllProjects();

        var myProjects = projects.values()
                .stream()
                .filter(p -> p.getBuilderIds().contains(builder.getId()))
                .collect(Collectors.toList());

        if (myProjects.isEmpty()) {
            System.out.println("No projects assigned.");
            return;
        }

        System.out.println("\nMy Projects:");
        myProjects.forEach(project -> System.out.println("ID: " + project.getId() + " | Name: " + project.getName() + " | Status: " + project.getStatus()));
    }

    private static void viewMyTasks(User builder, TaskService taskService) {

        Map<String, Task> tasks = taskService.viewTasksByBuilder(builder.getId());

        if (tasks.isEmpty()) {
            System.out.println("No tasks assigned.");
            return;
        }

        System.out.println("\nMy Tasks:");
        tasks.values().forEach(task -> System.out.println("Task ID: " + task.getId() + " | Description: " + task.getDescription() + " | Status: " + task.getStatus() + " | Project ID: " + task.getProjectId()));
    }

    private static void updateTaskStatus(User builder, TaskService taskService, Scanner scanner) {

        viewMyTasks(builder, taskService);

        System.out.print("\nEnter Task ID to update: ");
        String taskId = scanner.nextLine();

        System.out.print("Enter new status (IN_PROGRESS / COMPLETED): ");
        String statusInput = scanner.nextLine();

        TaskStatus status = TaskStatus.valueOf(statusInput.toUpperCase());

        boolean updated = taskService.updateTaskStatus(builder, taskId, status);

        System.out.println(updated ? "Task status updated successfully." : "Task not found.");
    }
}