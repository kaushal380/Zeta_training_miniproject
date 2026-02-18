package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.Project;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectRepository {

    private static final Logger logger = Logger.getLogger(ProjectRepository.class.getName());

    private final String filePath;
    private final Map<String, Project> projects = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    private final Object lock = new Object();

    public ProjectRepository(String filePath) {

        this.filePath = filePath;

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        loadFromFile();
    }

    public ProjectRepository() {
        this("projects.json");
    }

    public boolean addProject(String id, Project project) {

        synchronized (lock) {

            if (projects.containsKey(id)) {
                logger.warning("Duplicate project ID attempt: " + id);
                return false;
            }

            projects.put(id, project);
            saveToFile();
            return true;
        }
    }

    public boolean updateProject(String id, Project updatedProject) {

        synchronized (lock) {

            if (!projects.containsKey(id)) {
                return false;
            }

            projects.put(id, updatedProject);
            saveToFile();
            return true;
        }
    }

    public boolean deleteProject(String id) {

        synchronized (lock) {

            if (!projects.containsKey(id)) {
                return false;
            }

            projects.remove(id);
            saveToFile();
            return true;
        }
    }

    public Project getProjectById(String id) {
        return projects.get(id);
    }

    public Map<String, Project> getAllProjects() {
        return projects;
    }

    private void loadFromFile() {

        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return;
        }

        try {
            Map<String, Project> fileProjects = objectMapper.readValue(file, new TypeReference<Map<String, Project>>() {});

            projects.putAll(fileProjects);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load projects", e);
            throw new RuntimeException("Initialization failed", e);
        }
    }

    private void saveToFile() {

        synchronized (lock) {
            try {
                objectMapper.writeValue(new File(filePath), projects);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to save projects", e);
                throw new RuntimeException("Persist failed", e);
            }
        }
    }
}
