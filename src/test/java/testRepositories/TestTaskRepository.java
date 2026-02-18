package testRepositories;

import models.Task;
import models.enums.Priority;
import models.enums.TaskStatus;
import org.junit.jupiter.api.*;
import repositories.TaskRepository;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestTaskRepository {

    private static final String TEST_FILE = "test_tasks.json";
    private TaskRepository repository;

    @BeforeEach
    void setUp() {

        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        repository = new TaskRepository(TEST_FILE);
    }

    @AfterEach
    void tearDown() {

        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private Task createTask(String id, String projectId, String builderId) {

        Task task = new Task();
        task.setId(id);
        task.setDescription("Test Task");
        task.setProjectId(projectId);
        task.setAssignedBuilderId(builderId);
        task.setStartDate(LocalDate.now());
        task.setEndDate(LocalDate.now().plusDays(2));
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(Priority.HIGH);

        return task;
    }

    @Test
    void testAddTaskSuccess() {

        Task task = createTask("1", "P1", "B1");

        boolean result = repository.addTask("1", task);

        assertTrue(result);
        assertNotNull(repository.getTaskById("1"));
    }

    @Test
    void testAddTaskDuplicate() {

        Task task = createTask("1", "P1", "B1");

        repository.addTask("1", task);
        boolean second = repository.addTask("1", task);

        assertFalse(second);
    }

    @Test
    void testUpdateTaskSuccess() {

        Task task = createTask("1", "P1", "B1");
        repository.addTask("1", task);

        task.setDescription("Updated");

        boolean updated = repository.updateTask("1", task);

        assertTrue(updated);
        assertEquals("Updated", repository.getTaskById("1").getDescription());
    }

    @Test
    void testUpdateTaskNotFound() {

        Task task = createTask("1", "P1", "B1");

        boolean updated = repository.updateTask("1", task);

        assertFalse(updated);
    }

    @Test
    void testDeleteTaskSuccess() {

        Task task = createTask("1", "P1", "B1");
        repository.addTask("1", task);

        boolean deleted = repository.deleteTask("1");

        assertTrue(deleted);
        assertNull(repository.getTaskById("1"));
    }

    @Test
    void testDeleteTaskNotFound() {

        boolean deleted = repository.deleteTask("999");

        assertFalse(deleted);
    }

    @Test
    void testGetTasksByProjectId() {

        repository.addTask("1", createTask("1", "P1", "B1"));
        repository.addTask("2", createTask("2", "P2", "B2"));
        repository.addTask("3", createTask("3", "P1", "B3"));

        Map<String, Task> result = repository.getTasksByProjectId("P1");

        assertEquals(2, result.size());
    }

    @Test
    void testGetTasksByBuilderId() {

        repository.addTask("1", createTask("1", "P1", "B1"));
        repository.addTask("2", createTask("2", "P2", "B2"));
        repository.addTask("3", createTask("3", "P3", "B1"));

        Map<String, Task> result = repository.getTasksByBuilderId("B1");

        assertEquals(2, result.size());
    }

    @Test
    void testLoadFromFileWhenFileExists() {

        repository.addTask("1", createTask("1", "P1", "B1"));

        TaskRepository newRepo = new TaskRepository(TEST_FILE);

        assertNotNull(newRepo.getTaskById("1"));
    }

    @Test
    void testLoadFromFileCorruptedJson() throws Exception {

        Files.writeString(new File(TEST_FILE).toPath(), "INVALID_JSON");

        assertThrows(RuntimeException.class, () -> {new TaskRepository(TEST_FILE);});
    }

    @Test
    void testSaveToFileFailure() {

        TaskRepository badRepo = new TaskRepository("/invalid/path/tasks.json");

        Task task = createTask("1", "P1", "B1");

        assertThrows(RuntimeException.class, () -> {badRepo.addTask("1", task);});
    }
}