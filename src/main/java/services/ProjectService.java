package services;

import models.Project;
import models.User;
import models.enums.ProjectStatus;
import models.enums.UserRole;
import repositories.ProjectRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ProjectService {

    private static final Logger logger =
            Logger.getLogger(ProjectService.class.getName());

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public boolean createProject(User user,
                                 String name,
                                 String description,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 String type,
                                 double estimatedCost) {

        if (user.getRole() != UserRole.ADMIN) {
            logger.warning("Unauthorized project creation attempt by: " + user.getEmail());
            throw new RuntimeException("Only Admin can create projects");
        }

        String projectId = UUID.randomUUID().toString();

        Project project = new Project();
        project.setId(projectId);
        project.setName(name);
        project.setDescription(description);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setType(type);
        project.setEstimatedCost(estimatedCost);
        project.setStatus(ProjectStatus.UPCOMING);

        boolean added = projectRepository.addProject(projectId, project);

        if (added) {
            logger.info("Project created successfully: " + projectId);
            return true;
        }

        logger.warning("Failed to create project: " + projectId);
        return false;
    }

    public boolean updateProject(User user,
                                 String projectId,
                                 String name,
                                 String description,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 double estimatedCost) {

        if (user.getRole() != UserRole.ADMIN &&
                user.getRole() != UserRole.PROJECT_MANAGER) {

            logger.warning("Unauthorized update attempt by: " + user.getEmail());
            throw new RuntimeException("Only Admin or Manager can update projects");
        }

        Project project = projectRepository.getProjectById(projectId);

        if (project == null) {
            logger.warning("Project not found: " + projectId);
            return false;
        }

        project.setName(name);
        project.setDescription(description);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setEstimatedCost(estimatedCost);

        projectRepository.updateProject(projectId, project);

        logger.info("Project updated successfully: " + projectId);
        return true;
    }

    public boolean updateProjectStatus(User user,
                                       String projectId,
                                       ProjectStatus status) {

        if (user.getRole() != UserRole.PROJECT_MANAGER) {
            logger.warning("Unauthorized status update attempt by: " + user.getEmail());
            throw new RuntimeException("Only Project Manager can update project status");
        }

        Project project = projectRepository.getProjectById(projectId);

        if (project == null) {
            logger.warning("Project not found: " + projectId);
            return false;
        }

        project.setStatus(status);

        if (status == ProjectStatus.COMPLETED) {
            project.setEndDate(LocalDate.now());
        }

        projectRepository.updateProject(projectId, project);

        logger.info("Project status updated to " + status + " for project: " + projectId);
        return true;
    }

    public boolean deleteProject(User user, String projectId) {

        if (user.getRole() != UserRole.ADMIN &&
                user.getRole() != UserRole.PROJECT_MANAGER) {

            logger.warning("Unauthorized delete attempt by: " + user.getEmail());
            throw new RuntimeException("Only Admin or Manager can delete projects");
        }

        boolean deleted = projectRepository.deleteProject(projectId);

        if (deleted) {
            logger.info("Project deleted: " + projectId);
            return true;
        }

        logger.warning("Failed to delete project: " + projectId);
        return false;
    }

    public Project viewProject(String projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public Map<String, Project> viewAllProjects() {
        return projectRepository.getAllProjects();
    }
}
