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

    private static final Logger logger =
            Logger.getLogger(ProjectRepository.class.getName());

    private final String filePath;
    private final Map<String, Project> projects =
            new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

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

        if (projects.containsKey(id)) {
            logger.warning("Duplicate project ID attempt: " + id);
            return false;
        }

        projects.put(id, project);
        saveToFile();

        logger.info("Project added: " + id);
        return true;
    }

    public boolean updateProject(String id, Project updatedProject) {

        if (!projects.containsKey(id)) {
            logger.warning("Project not found for update: " + id);
            return false;
        }

        projects.put(id, updatedProject);
        saveToFile();

        logger.info("Project updated: " + id);
        return true;
    }

    public boolean deleteProject(String id) {

        if (!projects.containsKey(id)) {
            logger.warning("Project not found for deletion: " + id);
            return false;
        }

        projects.remove(id);
        saveToFile();

        logger.info("Project deleted: " + id);
        return true;
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
            logger.info("Project file missing or empty. Starting clean.");
            return;
        }

        try {
            Map<String, Project> fileProjects =
                    objectMapper.readValue(
                            file,
                            new TypeReference<Map<String, Project>>() {}
                    );

            projects.putAll(fileProjects);
            logger.info("Loaded " + fileProjects.size() + " projects.");

        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Failed to load projects from file: " + filePath, e);
            throw new RuntimeException("ProjectRepository initialization failed", e);
        }
    }

    private void saveToFile() {

        try {
            objectMapper.writeValue(new File(filePath), projects);
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Failed to save projects to file: " + filePath, e);
            throw new RuntimeException("Failed to persist projects", e);
        }
    }
}
