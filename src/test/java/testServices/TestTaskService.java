package testServices;

import dto.TaskUpdateRequest;
import models.Task;
import models.User;
import models.enums.Priority;
import models.enums.TaskStatus;
import models.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.TaskRepository;
import services.TaskService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestTaskService {

    private TaskRepository taskRepository;
    private TaskService taskService;
    private User manager;
    private User builder;
    private User admin;

    @BeforeEach
    void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);

        manager = new User();
        manager.setRole(UserRole.PROJECT_MANAGER);

        builder = new User();
        builder.setRole(UserRole.BUILDER);

        admin = new User();
        admin.setRole(UserRole.ADMIN);
    }

    @Test
    void createTaskSuccess() {

        when(taskRepository.addTask(anyString(), any(Task.class))).thenReturn(true);

        boolean result = taskService.createTask(manager, "Test Task", "new test task", LocalDate.now(), LocalDate.now().plusDays(5), "project1");

        assertTrue(result);
    }

    @Test
    void createTaskUnauthorized() {

        assertThrows(RuntimeException.class, () -> taskService.createTask(builder, "Task", "test task", LocalDate.now(), LocalDate.now(), "project1"));
    }

    @Test
    void updateTaskSuccessPartial() {

        Task task = new Task();
        task.setId("1");

        when(taskRepository.getTaskById("1")).thenReturn(task);
        when(taskRepository.updateTask(eq("1"), any(Task.class))).thenReturn(true);

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setDescription("Updated");

        boolean result = taskService.updateTask(manager, "1", request);

        assertTrue(result);
        assertEquals("Updated", task.getDescription());
    }

    @Test
    void updateTaskFullUpdate() {

        Task task = new Task();
        task.setId("1");

        when(taskRepository.getTaskById("1")).thenReturn(task);
        when(taskRepository.updateTask(eq("1"), any(Task.class))).thenReturn(true);

        TaskUpdateRequest request = new TaskUpdateRequest("Desc", LocalDate.now(), LocalDate.now().plusDays(2), Priority.HIGH, TaskStatus.IN_PROGRESS);

        boolean result = taskService.updateTask(manager, "1", request);

        assertTrue(result);
        assertEquals(Priority.HIGH, task.getPriority());
    }

    @Test
    void updateTaskNotFound() {

        when(taskRepository.getTaskById("1")).thenReturn(null);

        TaskUpdateRequest request = new TaskUpdateRequest();
        boolean result = taskService.updateTask(manager, "1", request);

        assertFalse(result);
    }

    @Test
    void updateTaskUnauthorized() {

        TaskUpdateRequest request = new TaskUpdateRequest();

        assertThrows(RuntimeException.class, () -> taskService.updateTask(builder, "1", request));
    }

    @Test
    void deleteTaskSuccess() {

        when(taskRepository.deleteTask("1")).thenReturn(true);

        boolean result = taskService.deleteTask(manager, "1");

        assertTrue(result);
    }

    @Test
    void deleteTaskUnauthorized() {

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(builder, "1"));
    }

    @Test
    void assignTaskSuccess() {

        Task task = new Task();
        task.setId("1");

        when(taskRepository.getTaskById("1")).thenReturn(task);
        when(taskRepository.updateTask(eq("1"), any(Task.class))).thenReturn(true);

        boolean result = taskService.assignTaskToBuilder(manager, "1", "builder1");

        assertTrue(result);
        assertEquals("builder1", task.getAssignedBuilderId());
    }

    @Test
    void assignTaskNotFound() {

        when(taskRepository.getTaskById("1")).thenReturn(null);

        boolean result = taskService.assignTaskToBuilder(manager, "1", "builder1");

        assertFalse(result);
    }

    @Test
    void updateTaskStatusSuccess() {

        Task task = new Task();
        task.setId("1");

        when(taskRepository.getTaskById("1")).thenReturn(task);
        when(taskRepository.updateTask(eq("1"), any(Task.class))).thenReturn(true);

        boolean result = taskService.updateTaskStatus(builder, "1", TaskStatus.IN_PROGRESS);

        assertTrue(result);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateTaskStatusCompletedSetsEndDate() {

        Task task = new Task();
        task.setId("1");

        when(taskRepository.getTaskById("1")).thenReturn(task);
        when(taskRepository.updateTask(eq("1"), any(Task.class))).thenReturn(true);

        boolean result = taskService.updateTaskStatus(builder, "1", TaskStatus.COMPLETED);

        assertTrue(result);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getEndDate());
    }

    @Test
    void updateTaskStatusUnauthorized() {

        assertThrows(RuntimeException.class, () -> taskService.updateTaskStatus(manager, "1", TaskStatus.COMPLETED));
    }

    @Test
    void viewTasksByProject() {

        Map<String, Task> map = new HashMap<>();
        when(taskRepository.getTasksByProjectId("p1")).thenReturn(map);
        assertEquals(map, taskService.viewTasksByProject("p1"));
    }

    @Test
    void viewTasksByBuilder() {

        Map<String, Task> map = new HashMap<>();
        when(taskRepository.getTasksByBuilderId("b1")).thenReturn(map);
        assertEquals(map, taskService.viewTasksByBuilder("b1"));
    }
}
