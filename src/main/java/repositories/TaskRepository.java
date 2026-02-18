package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.Task;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TaskRepository {

    private static final Logger logger = Logger.getLogger(TaskRepository.class.getName());

    private final String filePath;

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    private final Object lock = new Object();

    public TaskRepository(String filePath) {

        this.filePath = filePath;

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        loadFromFile();
    }

    public TaskRepository() {
        this("tasks.json");
    }

    public boolean addTask(String id, Task task) {

        synchronized (lock) {

            if (tasks.containsKey(id)) {
                logger.warning("Duplicate task id: " + id);
                return false;
            }

            tasks.put(id, task);
            saveToFile();
            return true;
        }
    }

    public boolean updateTask(String id, Task task) {

        synchronized (lock) {

            if (!tasks.containsKey(id)) {
                return false;
            }

            tasks.put(id, task);
            saveToFile();
            return true;
        }
    }

    public boolean deleteTask(String id) {

        synchronized (lock) {

            if (!tasks.containsKey(id)) {
                return false;
            }

            tasks.remove(id);
            saveToFile();
            return true;
        }
    }

    public Task getTaskById(String id) {
        return tasks.get(id);
    }

    public Map<String, Task> getTasksByProjectId(String projectId) {

        Map<String, Task> result = new ConcurrentHashMap<>();

        for (Task task : tasks.values()) {

            if (projectId.equals(task.getProjectId())) {
                result.put(task.getId(), task);
            }
        }
        return result;
    }

    public Map<String, Task> getTasksByBuilderId(String builderId) {

        Map<String, Task> result = new ConcurrentHashMap<>();

        for (Task task : tasks.values()) {

            if (builderId.equals(task.getAssignedBuilderId())) {
                result.put(task.getId(), task);
            }
        }
        return result;
    }

    private void loadFromFile() {

        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return;
        }

        synchronized (lock) {
            try {
                Map<String, Task> fileTasks = objectMapper.readValue(file, new TypeReference<Map<String, Task>>() {});
                tasks.putAll(fileTasks);

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load tasks", e);
                throw new RuntimeException("TaskRepository initialization failed", e);
            }
        }
    }

    private void saveToFile() {

        try {
            objectMapper.writeValue(new File(filePath), tasks);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save tasks", e);
            throw new RuntimeException("Failed to persist tasks", e);
        }
    }
}
