package services;

import dto.TaskUpdateRequest;
import models.Task;
import models.User;
import models.enums.TaskStatus;
import models.enums.UserRole;
import repositories.TaskRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class TaskService {

    private static final Logger logger = Logger.getLogger(TaskService.class.getName());

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean createTask(User user,
                              String taskName,
                              String description,
                              LocalDate startDate,
                              LocalDate endDate,
                              String projectId) {

        if (user.getRole() != UserRole.PROJECT_MANAGER) {
            throw new RuntimeException("Only Project Manager can create tasks");
        }


        String id = UUID.randomUUID().toString();

        Task task = new Task();
        task.setId(id);
        task.setTaskName(taskName);
        task.setDescription(description);
        task.setStartDate(startDate);
        task.setEndDate(endDate);
        task.setProjectId(projectId);
        task.setStatus(TaskStatus.PENDING);

        return taskRepository.addTask(id, task);
    }


    public boolean updateTask(User user, String taskId, TaskUpdateRequest request) {

        if (user.getRole() != UserRole.PROJECT_MANAGER) {
            throw new RuntimeException("Only Project Manager can update tasks");
        }

        Task task = taskRepository.getTaskById(taskId);

        if (task == null) {
            return false;
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            task.setEndDate(request.getEndDate());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());

            if (request.getStatus() == TaskStatus.COMPLETED) {
                task.setEndDate(LocalDate.now());
            }
        }
        return taskRepository.updateTask(taskId, task);
    }


    public boolean deleteTask(User user, String taskId) {

        if (user.getRole() != UserRole.PROJECT_MANAGER) {
            throw new RuntimeException("Only Project Manager can delete tasks");
        }

        return taskRepository.deleteTask(taskId);
    }

    public boolean assignTaskToBuilder(User user, String taskId, String builderId) {

        if (user.getRole() != UserRole.PROJECT_MANAGER) {
            throw new RuntimeException("Only Project Manager can assign tasks");
        }

        Task task = taskRepository.getTaskById(taskId);

        if (task == null) {
            return false;
        }

        task.setAssignedBuilderId(builderId);

        return taskRepository.updateTask(taskId, task);
    }

    public boolean updateTaskStatus(User user, String taskId, TaskStatus status) {

        if (user.getRole() != UserRole.BUILDER) {
            throw new RuntimeException("Only Builder can update task status");
        }

        Task task = taskRepository.getTaskById(taskId);

        if (task == null) {
            return false;
        }

        task.setStatus(status);

        if (status == TaskStatus.COMPLETED) {
            task.setEndDate(LocalDate.now());
        }

        return taskRepository.updateTask(taskId, task);
    }

    public Map<String, Task> viewTasksByProject(String projectId) {
        return taskRepository.getTasksByProjectId(projectId);
    }

    public Map<String, Task> viewTasksByBuilder(String builderId) {
        return taskRepository.getTasksByBuilderId(builderId);
    }
}
