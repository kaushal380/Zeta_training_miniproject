package consoleUI;

import dto.ProjectUpdateRequest;
import dto.TaskUpdateRequest;
import models.Project;
import models.Task;
import models.User;
import models.enums.ProjectStatus;
import models.enums.UserRole;
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
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> viewMyProjects(manager);
                    case "2" -> updateProject(manager);
                    case "3" -> updateProjectStatus(manager);
                    case "4" -> assignBuilderToProject(manager);
                    case "5" -> createTask(manager);
                    case "6" -> updateTask(manager);
                    case "7" -> deleteTask(manager);
                    case "8" -> assignTaskToBuilder(manager);
                    case "9" -> viewTasksByProject(manager);
                    case "10" -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private List<Project> getManagerProjects(User manager) {
        List<Project> result = new ArrayList<>();
        for (Project p : projectService.viewAllProjects().values()) {
            if (manager.getId().equals(p.getManagerId())) {
                result.add(p);
            }
        }
        return result;
    }

    private Project selectProject(User manager) {
        List<Project> projects = getManagerProjects(manager);

        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return null;
        }

        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". "
                    + projects.get(i).getName()
                    + " | " + projects.get(i).getStatus());
        }

        return projects.get(selectIndex(projects.size()));
    }

    private Task selectTask(User manager) {
        List<Project> projects = getManagerProjects(manager);
        Map<Integer, Task> taskMap = new HashMap<>();
        int counter = 1;

        for (Project p : projects) {
            for (Task t : taskService.viewTasksByProject(p.getId()).values()) {
                System.out.println(counter + ". "
                        + t.getTaskName()
                        + " | Project: " + p.getName()
                        + " | Status: " + t.getStatus());
                taskMap.put(counter, t);
                counter++;
            }
        }

        if (taskMap.isEmpty()) {
            System.out.println("No tasks available.");
            return null;
        }

        int selectedIndex = selectIndex(taskMap.size()) + 1;
        return taskMap.get(selectedIndex);
    }

    private User selectBuilder() {
        List<User> builders = userService.getUsersByRole(UserRole.BUILDER);

        if (builders.isEmpty()) {
            System.out.println("No builders available.");
            return null;
        }

        for (int i = 0; i < builders.size(); i++) {
            System.out.println((i + 1) + ". "
                    + builders.get(i).getName()
                    + " | ID: " + builders.get(i).getId());
        }

        return builders.get(selectIndex(builders.size()));
    }

    private int selectIndex(int size) {
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

    private void viewMyProjects(User manager) {
        List<Project> projects = getManagerProjects(manager);

        if (projects.isEmpty()) {
            System.out.println("No projects assigned.");
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". "
                    + projects.get(i).getName()
                    + " | " + projects.get(i).getStatus());
        }
    }

    private void createTask(User manager) {
        Project project = selectProject(manager);
        if (project == null) return;

        System.out.print("Task Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Task Description: ");
        String description = scanner.nextLine().trim();

        LocalDate start = getValidDate("Start Date (yyyy-mm-dd): ");

        LocalDate end;
        while (true) {
            end = getValidDate("End Date (yyyy-mm-dd): ");
            if (end.isAfter(start)) break;
            System.out.println("End date must be after start date.");
        }

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            System.out.println("The project is marked completed, cannot create new tasks");
            return;
        } else if (project.getStatus() == ProjectStatus.UPCOMING) {
            project.setStatus(ProjectStatus.IN_PROGRESS);
        }

        boolean created = taskService.createTask(
                manager, name, description, start, end, project.getId()
        );

        System.out.println(created ? "Task created." : "Creation failed.");
    }

    private void updateTask(User manager) {
        Task task = selectTask(manager);
        if (task == null) return;

        TaskUpdateRequest request = new TaskUpdateRequest();

        System.out.print("New Description (Enter to skip): ");
        String desc = scanner.nextLine().trim();
        if (!desc.isBlank()) request.setDescription(desc);

        boolean updated = taskService.updateTask(manager, task.getId(), request);
        System.out.println(updated ? "Task updated." : "Update failed.");
    }

    private void deleteTask(User manager) {
        Task task = selectTask(manager);
        if (task == null) return;

        boolean deleted = taskService.deleteTask(manager, task.getId());
        System.out.println(deleted ? "Task deleted." : "Deletion failed.");
    }

    private void assignTaskToBuilder(User manager) {
        Task task = selectTask(manager);
        if (task == null) return;

        User builder = selectBuilder();
        if (builder == null) return;

        boolean assigned = taskService.assignTaskToBuilder(
                manager, task.getId(), builder.getId()
        );

        if (assigned) {
            assignmentService.assignProjectToBuilder(manager, task.getProjectId(), builder.getId());
            System.out.println("Task Assigned");
        } else {
            System.out.println("Assignment failed");
        }
    }

    private void assignBuilderToProject(User manager) {
        Project project = selectProject(manager);
        if (project == null) return;

        User builder = selectBuilder();
        if (builder == null) return;

        boolean assigned = assignmentService.assignProjectToBuilder(
                manager, project.getId(), builder.getId()
        );

        System.out.println(assigned ? "Builder assigned." : "Assignment failed.");
    }

    private void viewTasksByProject(User manager) {
        Project project = selectProject(manager);
        if (project == null) return;

        Map<String, Task> tasks = taskService.viewTasksByProject(project.getId());

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        for (Task t : tasks.values()) {
            System.out.println(t.getTaskName() + " | " + t.getStatus());
        }
    }

    private void updateProject(User manager) {
        Project project = selectProject(manager);
        if (project == null) return;

        ProjectUpdateRequest request = new ProjectUpdateRequest();

        System.out.print("New Name (Enter to skip): ");
        String name = scanner.nextLine().trim();
        if (!name.isBlank()) request.setName(name);

        boolean updated = projectService.updateProject(manager, project.getId(), request);
        System.out.println(updated ? "Project updated." : "Update failed.");
    }

    private void updateProjectStatus(User manager) {
        Project project = selectProject(manager);
        if (project == null) return;

        while (true) {
            try {

                ProjectStatus status = getProjectStatus();

                if (status == ProjectStatus.COMPLETED || status == ProjectStatus.UPCOMING) {
                    Map<String, Task> viewTasksByProject = taskService.viewTasksByProject(project.getId());
                    viewTasksByProject.forEach((taskid, task) -> taskService.deleteTask(manager, taskid));
                }

                projectService.updateProjectStatus(manager, project.getId(), status);
                System.out.println("Status updated.");
                return;

            } catch (Exception e) {
                System.out.println("Invalid status.");
            }
        }
    }

    private ProjectStatus getProjectStatus() {

        while (true) {
            System.out.println("""
                    Select Project Status:
                    1. UPCOMING
                    2. IN_PROGRESS
                    3. COMPLETED
                    """);

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    return ProjectStatus.UPCOMING;
                }
                case "2" -> {
                    return ProjectStatus.IN_PROGRESS;
                }
                case "3" -> {
                    return ProjectStatus.COMPLETED;
                }
                default -> System.out.println("Invalid selection.");
            }
        }
    }

    private LocalDate getValidDate(String message) {
        while (true) {
            try {
                System.out.print(message);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid date format. Use yyyy-mm-dd.");
            }
        }
    }
}