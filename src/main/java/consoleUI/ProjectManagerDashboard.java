package consoleUI;

import dto.ProjectUpdateRequest;
import dto.TaskUpdateRequest;
import models.Project;
import models.Task;
import models.User;
import models.enums.ProjectStatus;
import models.enums.Priority;
import models.enums.TaskStatus;
import services.AssignmentService;
import services.ProjectService;
import services.TaskService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

public class ProjectManagerDashboard {

    public static void start(User manager, ProjectService projectService, TaskService taskService, AssignmentService assignmentService, Scanner scanner) {

        while (true) {

            System.out.println("\n===== PROJECT MANAGER DASHBOARD =====");
            System.out.println("1. View My Projects");
            System.out.println("2. Update Project Details");
            System.out.println("3. Update Project Status");
            System.out.println("4. Assign Builder to Project");
            System.out.println("5. Create Task");
            System.out.println("6. Update Task");
            System.out.println("7. Delete Task");
            System.out.println("8. Assign Task to Builder");
            System.out.println("9. View Tasks by Project");
            System.out.println("10. Exit");

            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {

                    case "1" -> viewMyProjects(manager, projectService);

                    case "2" -> updateProject(manager, projectService, scanner);

                    case "3" -> updateProjectStatus(manager, projectService, scanner);

                    case "4" -> assignBuilder(manager, projectService, assignmentService, scanner);

                    case "5" -> createTask(manager, taskService, scanner);

                    case "6" -> updateTask(manager, taskService, scanner);

                    case "7" -> deleteTask(manager, taskService, scanner);

                    case "8" -> assignTask(manager, taskService, scanner);

                    case "9" -> viewTasks(manager, taskService, projectService, scanner);

                    case "10" -> {
                        System.out.println("Exiting Manager Dashboard...");
                        return;
                    }

                    default -> System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ========================= PROJECT METHODS ==============================

    private static void viewMyProjects(User manager, ProjectService service) {

        Map<String, Project> projects = service.viewAllProjects();

        System.out.println("\nYour Projects:");
        projects.values().stream().filter(p -> manager.getId().equals(p.getManagerId())).forEach(p -> System.out.println("ID: " + p.getId() +" | Name: " + p.getName() + " | Status: " + p.getStatus()));
    }

    private static String chooseProject(User manager, ProjectService service, Scanner scanner) {

        viewMyProjects(manager, service);

        System.out.print("Enter Project ID: ");
        return scanner.nextLine();
    }

    private static void updateProject(User manager, ProjectService service, Scanner scanner) {

        String id = chooseProject(manager, service, scanner);

        ProjectUpdateRequest request = new ProjectUpdateRequest();

        System.out.print("New Name (Press Enter to skip): ");
        String name = scanner.nextLine();
        if (!name.isBlank())
            request.setName(name);

        System.out.print("New Description (Press Enter to skip): ");
        String desc = scanner.nextLine();
        if (!desc.isBlank())
            request.setDescription(desc);

        System.out.print("New Start Date (yyyy-mm-dd) or Enter to skip: ");
        String startInput = scanner.nextLine();
        if (!startInput.isBlank())
            request.setStartDate(LocalDate.parse(startInput));

        System.out.print("New End Date (yyyy-mm-dd) or Enter to skip: ");
        String endInput = scanner.nextLine();
        if (!endInput.isBlank())
            request.setEndDate(LocalDate.parse(endInput));

        System.out.print("New Estimated Cost (Enter to skip): ");
        String cost = scanner.nextLine();
        if (!cost.isBlank())
            request.setEstimatedCost(Double.parseDouble(cost));

        boolean updated = service.updateProject(manager, id, request);

        System.out.println(updated ? "Project updated successfully." : "Project not found.");
    }

    private static void updateProjectStatus(User manager, ProjectService service, Scanner scanner) {

        String id = chooseProject(manager, service, scanner);

        System.out.print("Status (UPCOMING / IN_PROGRESS / COMPLETED): ");
        ProjectStatus status = ProjectStatus.valueOf(scanner.nextLine().toUpperCase());

        boolean updated = service.updateProjectStatus(manager, id, status);

        System.out.println(updated ? "Status updated." : "Project not found.");
    }

    private static void assignBuilder(User manager, ProjectService projectService, AssignmentService assignmentService, Scanner scanner) {

        String projectId = chooseProject(manager, projectService, scanner);

        System.out.print("Enter Builder ID: ");
        String builderId = scanner.nextLine();

        boolean assigned = assignmentService.assignProjectToBuilder(manager, projectId, builderId);

        System.out.println(assigned ? "Builder assigned." : "Failed.");
    }

    private static void createTask(User manager, TaskService service, Scanner scanner) {

        System.out.print("Project ID: ");
        String projectId = scanner.nextLine();

        System.out.print("Task Description: ");
        String description = scanner.nextLine();

        System.out.print("Start Date (yyyy-mm-dd): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.print("End Date (yyyy-mm-dd): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        boolean created = service.createTask(manager, description, start, end, projectId);

        System.out.println(created ? "Task created." : "Failed.");
    }

    private static void updateTask(User manager, TaskService service, Scanner scanner) {

        System.out.print("Task ID: ");
        String taskId = scanner.nextLine();

        TaskUpdateRequest request = new TaskUpdateRequest();

        System.out.print("New Description (Press Enter to skip): ");
        String desc = scanner.nextLine();
        if (!desc.isBlank())
            request.setDescription(desc);

        System.out.print("New Priority (LOW/MEDIUM/HIGH) or Enter to skip: ");
        String priority = scanner.nextLine();
        if (!priority.isBlank())
            request.setPriority(Priority.valueOf(priority.toUpperCase()));

        System.out.print("New Status (PENDING/IN_PROGRESS/COMPLETED) or Enter to skip: ");
        String status = scanner.nextLine();
        if (!status.isBlank())
            request.setStatus(TaskStatus.valueOf(status.toUpperCase()));

        boolean updated = service.updateTask(manager, taskId, request);

        System.out.println(updated ? "Task updated." : "Task not found.");
    }

    private static void deleteTask(User manager, TaskService service, Scanner scanner) {

        System.out.print("Task ID: ");
        String id = scanner.nextLine();

        boolean deleted = service.deleteTask(manager, id);

        System.out.println(deleted ? "Task deleted." : "Task not found.");
    }

    private static void assignTask(User manager, TaskService service, Scanner scanner) {

        System.out.print("Task ID: ");
        String taskId = scanner.nextLine();

        System.out.print("Builder ID: ");
        String builderId = scanner.nextLine();

        boolean assigned = service.assignTaskToBuilder(manager, taskId, builderId);

        System.out.println(assigned ? "Task assigned." : "Task not found.");
    }

    private static void viewTasks(User manager, TaskService taskService, ProjectService projectService, Scanner scanner) {

        String projectId = chooseProject(manager, projectService, scanner);

        Map<String, Task> tasks = taskService.viewTasksByProject(projectId);

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        tasks.values().forEach(task ->
                System.out.println("Task ID: " + task.getId() + " | Description: " + task.getDescription() + " | Status: " + task.getStatus()));
    }
}
