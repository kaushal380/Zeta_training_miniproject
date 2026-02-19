package services;

import dto.TaskUpdateRequest;
import models.Address;
import models.Task;
import models.User;
import models.enums.Priority;
import models.enums.TaskStatus;
import models.enums.UserRole;
import repositories.TaskRepository;
import repositories.UserRepository;

import java.time.LocalDate;
import java.util.Map;

public class TaskDemo {

    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository("users_demo.json");

        TaskRepository taskRepository = new TaskRepository("tasks_demo.json");

        AuthenticationService authService = new AuthenticationService(userRepository);

        TaskService taskService = new TaskService(taskRepository);

        System.out.println("===== REGISTER USERS =====");

        authService.register("Manager User", "9999999999", "manager@gmail.com", "manager123", LocalDate.of(1990, 1, 1), new Address("City", "State", "123456", "Country"), UserRole.PROJECT_MANAGER);

        authService.register("Builder User", "8888888888", "builder@gmail.com", "builder123", LocalDate.of(1995, 1, 1), new Address("City", "State", "654321", "Country"), UserRole.BUILDER);

        System.out.println("===== LOGIN USERS =====");

        User manager = authService.login("manager@gmail.com", "manager123");

        User builder = authService.login("builder@gmail.com", "builder123");

        if (manager == null || builder == null) {
            System.out.println("Login failed!");
            return;
        }

        System.out.println("Manager ID (UUID): " + manager.getId());
        System.out.println("Builder ID (UUID): " + builder.getId());

        System.out.println("\n===== CREATE TASK =====");

        boolean created = taskService.createTask(manager, "Foundation Work", "Foundation phase work", LocalDate.now(), LocalDate.now().plusDays(7), "P1");

        System.out.println("Task Created: " + created);

        Map<String, Task> tasks = taskService.viewTasksByProject("P1");

        if (tasks.isEmpty()) {
            System.out.println("No tasks found!");
            return;
        }

        String taskId = tasks.keySet().iterator().next();

        System.out.println("Created Task ID: " + taskId);

        System.out.println("\n===== UPDATE TASK =====");

        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setPriority(Priority.HIGH);
        updateRequest.setDescription("Foundation + Pillar setup");

        boolean updated = taskService.updateTask(manager, taskId, updateRequest);

        System.out.println("Task Updated: " + updated);

        System.out.println("\n===== ASSIGN TASK TO BUILDER =====");

        boolean assigned = taskService.assignTaskToBuilder(manager, taskId, builder.getId());

        System.out.println("Task Assigned: " + assigned);

        System.out.println("\n===== BUILDER STARTS TASK =====");

        boolean started = taskService.updateTaskStatus(builder, taskId, TaskStatus.IN_PROGRESS);

        System.out.println("Task Started: " + started);

        System.out.println("\n===== BUILDER COMPLETES TASK =====");

        boolean completed = taskService.updateTaskStatus(builder, taskId, TaskStatus.COMPLETED);

        System.out.println("Task Completed: " + completed);

        System.out.println("\n===== VIEW BUILDER TASKS =====");

        Map<String, Task> builderTasks = taskService.viewTasksByBuilder(builder.getId());

        builderTasks.forEach((id, task) -> System.out.println("Task ID: " + id + " | Status: " + task.getStatus() + " | End Date: " + task.getEndDate()));
    }
}
