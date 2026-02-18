package consoleUI;

import dto.ProjectUpdateRequest;
import dto.TaskUpdateRequest;
import models.Project;
import models.Task;
import models.User;
import models.enums.ProjectStatus;
import services.AssignmentService;
import services.ProjectService;
import services.TaskService;
import services.UserService;

import java.time.LocalDate;
import java.util.*;

public class ProjectManagerDashboard {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final AssignmentService assignmentService;
    private final UserService userService;
    private final Scanner scanner;

    public ProjectManagerDashboard(ProjectService projectService,
                                   TaskService taskService,
                                   AssignmentService assignmentService,
                                   UserService userService,
                                   Scanner scanner) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.scanner = scanner;
    }

    public void start(User manager) {

        while (true) {

            System.out.println("\n===== PROJECT MANAGER DASHBOARD =====");
            System.out.println("1. View My Projects");
            System.out.println("2. Update Project");
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
                    case "1" -> viewMyProjects(manager);
                    case "2" -> updateProject(manager);
                    case "3" -> updateProjectStatus(manager);
                    case "4" -> assignBuilder(manager);
                    case "5" -> createTask(manager);
                    case "6" -> updateTask(manager);
                    case "7" -> deleteTask(manager);
                    case "8" -> assignTask(manager);
                    case "9" -> viewTasks(manager);
                    case "10" -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private void viewMyProjects(User manager) {

        List<Project> myProjects = getManagerProjects(manager);

        if (myProjects.isEmpty()) {
            System.out.println("No projects assigned.");
            return;
        }

        for (int i = 0; i < myProjects.size(); i++) {
            Project p = myProjects.get(i);
            System.out.println((i + 1) + ". " + p.getName() + " | " + p.getStatus());
        }
    }

    private List<Project> getManagerProjects(User manager) {

        Map<String, Project> projects = projectService.viewAllProjects();
        List<Project> myProjects = new ArrayList<>();

        for (Project p : projects.values()) {
            if (manager.getId().equals(p.getManagerId())) {
                myProjects.add(p);
            }
        }

        return myProjects;
    }

    private Project chooseProject(User manager) {

        List<Project> myProjects = getManagerProjects(manager);

        if (myProjects.isEmpty()) {
            System.out.println("No projects available.");
            return null;
        }

        for (int i = 0; i < myProjects.size(); i++) {
            System.out.println((i + 1) + ". " + myProjects.get(i).getName());
        }

        while (true) {
            System.out.print("Select number: ");
            String input = scanner.nextLine();

            if (input.matches("\\d+")) {
                int index = Integer.parseInt(input);
                if (index >= 1 && index <= myProjects.size()) {
                    return myProjects.get(index - 1);
                }
            }

            System.out.println("Invalid selection.");
        }
    }

    private void createTask(User manager) {

        Project project = chooseProject(manager);
        if (project == null) return;

        System.out.print("Task Name: ");
        String taskName = scanner.nextLine();

        System.out.print("Task Description: ");
        String description = scanner.nextLine();

        LocalDate start = getValidDate("Start Date (yyyy-mm-dd): ");

        LocalDate end;
        while (true) {
            end = getValidDate("End Date (yyyy-mm-dd): ");
            if (end.isAfter(start)) break;
            System.out.println("End date must be after start date.");
        }

        boolean created = taskService.createTask(
                manager,
                taskName,
                description,
                start,
                end,
                project.getId()
        );

        System.out.println(created ? "Task created." : "Creation failed.");
    }

    private List<Task> getAllManagerTasks(User manager) {

        List<Project> projects = getManagerProjects(manager);
        List<Task> allTasks = new ArrayList<>();

        for (Project p : projects) {
            Map<String, Task> tasks = taskService.viewTasksByProject(p.getId());
            allTasks.addAll(tasks.values());
        }

        return allTasks;
    }

    private Task chooseTask(User manager) {

        List<Project> projects = getManagerProjects(manager);
        List<Task> tasks = new ArrayList<>();

        for (Project p : projects) {
            Map<String, Task> projectTasks =
                    taskService.viewTasksByProject(p.getId());
            tasks.addAll(projectTasks.values());
        }

        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return null;
        }

        int counter = 1;
        Map<Integer, Task> indexMap = new HashMap<>();

        for (Project p : projects) {
            Map<String, Task> projectTasks =
                    taskService.viewTasksByProject(p.getId());

            for (Task t : projectTasks.values()) {
                System.out.println(counter + ". "
                        + t.getTaskName()
                        + " | Project: " + p.getName()
                        + " | Status: " + t.getStatus());
                indexMap.put(counter, t);
                counter++;
            }
        }

        while (true) {
            System.out.print("Select number: ");
            String input = scanner.nextLine();

            if (input.matches("\\d+")) {
                int index = Integer.parseInt(input);
                if (indexMap.containsKey(index)) {
                    return indexMap.get(index);
                }
            }

            System.out.println("Invalid selection.");
        }
    }

    private void updateTask(User manager) {

        Task task = chooseTask(manager);
        if (task == null) return;

        TaskUpdateRequest request = new TaskUpdateRequest();

        System.out.print("New Description (Enter to skip): ");
        String desc = scanner.nextLine();
        if (!desc.isBlank()) request.setDescription(desc);

        boolean updated =
                taskService.updateTask(manager, task.getId(), request);

        System.out.println(updated ? "Task updated." : "Update failed.");
    }

    private void deleteTask(User manager) {

        Task task = chooseTask(manager);
        if (task == null) return;

        boolean deleted =
                taskService.deleteTask(manager, task.getId());

        System.out.println(deleted ? "Task deleted." : "Deletion failed.");
    }

    private void assignTask(User manager) {

        Task task = chooseTask(manager);
        if (task == null) return;

        System.out.print("Enter Builder ID: ");
        String builderId = scanner.nextLine();

        boolean assigned =
                taskService.assignTaskToBuilder(manager, task.getId(), builderId);

        System.out.println(assigned ? "Task assigned." : "Assignment failed.");
    }

    private void viewTasks(User manager) {

        Project project = chooseProject(manager);
        if (project == null) return;

        Map<String, Task> tasks =
                taskService.viewTasksByProject(project.getId());

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        for (Task t : tasks.values()) {
            System.out.println(t.getTaskName()
                    + " | " + t.getStatus());
        }
    }

    private void updateProject(User manager) {

        Project project = chooseProject(manager);
        if (project == null) return;

        ProjectUpdateRequest request = new ProjectUpdateRequest();

        System.out.print("New Name (Enter to skip): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) request.setName(name);

        boolean updated =
                projectService.updateProject(manager, project.getId(), request);

        System.out.println(updated ? "Project updated." : "Update failed.");
    }

    private void updateProjectStatus(User manager) {

        Project project = chooseProject(manager);
        if (project == null) return;

        while (true) {
            try {
                System.out.print("Status (UPCOMING/IN_PROGRESS/COMPLETED): ");
                ProjectStatus status =
                        ProjectStatus.valueOf(scanner.nextLine().toUpperCase());

                projectService.updateProjectStatus(manager, project.getId(), status);
                System.out.println("Status updated.");
                return;

            } catch (Exception e) {
                System.out.println("Invalid status.");
            }
        }
    }

    private void assignBuilder(User manager) {

        Project project = chooseProject(manager);
        if (project == null) return;

        System.out.print("Enter Builder ID: ");
        String builderId = scanner.nextLine();

        boolean assigned =
                assignmentService.assignProjectToBuilder(manager, project.getId(), builderId);

        System.out.println(assigned ? "Builder assigned." : "Assignment failed.");
    }

    private LocalDate getValidDate(String message) {

        while (true) {
            try {
                System.out.print(message);
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid date format. Use yyyy-mm-dd.");
            }
        }
    }
}
