package services;

import dto.ProjectUpdateRequest;
import models.Address;
import models.Project;
import models.User;
import models.enums.ProjectStatus;
import models.enums.ProjectType;
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
                                 ProjectType type,
                                 Address location,
                                 double estimatedCost) {

        if (user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only Admin can create projects");
        }

        String projectId = UUID.randomUUID().toString();

        Project project = new Project(
                projectId,
                name,
                description,
                startDate,
                endDate,
                ProjectStatus.UPCOMING,
                type,
                location,
                estimatedCost
        );

        return projectRepository.addProject(projectId, project);
    }

    public boolean updateProject(User user,
                                 String projectId,
                                 ProjectUpdateRequest request) {

        if (user.getRole() != UserRole.ADMIN &&
                user.getRole() != UserRole.PROJECT_MANAGER) {

            throw new RuntimeException("Only Admin or Manager can update projects");
        }

        Project project = projectRepository.getProjectById(projectId);

        if (project == null) {
            logger.warning("Project not found: " + projectId);
            return false;
        }

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new RuntimeException("Cannot modify completed project");
        }

        if (request.getName() != null)
            project.setName(request.getName());

        if (request.getDescription() != null)
            project.setDescription(request.getDescription());

        if (request.getStartDate() != null)
            project.setStartDate(request.getStartDate());

        if (request.getEndDate() != null)
            project.setEndDate(request.getEndDate());

        if (request.getEstimatedCost() != null)
            project.setEstimatedCost(request.getEstimatedCost());

        if (request.getType() != null)
            project.setType(request.getType());

        if (request.getLocation() != null)
            project.setLocation(request.getLocation());

        projectRepository.updateProject(projectId, project);

        logger.info("Project updated successfully: " + projectId);
        return true;
    }

    public boolean updateProjectStatus(User user,
                                       String projectId,
                                       ProjectStatus status) {

        if (user.getRole() != UserRole.PROJECT_MANAGER) {
            throw new RuntimeException("Only Project Manager can update status");
        }

        Project project = projectRepository.getProjectById(projectId);

        if (project == null)
            return false;

        project.setStatus(status);

        if (status == ProjectStatus.COMPLETED) {
            project.setEndDate(LocalDate.now());
        }

        projectRepository.updateProject(projectId, project);
        return true;
    }

    public boolean deleteProject(User user, String projectId) {

        if (user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only Admin can delete projects");
        }

        return projectRepository.deleteProject(projectId);
    }

    public Project viewProject(String projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public Map<String, Project> viewAllProjects() {
        return projectRepository.getAllProjects();
    }
}
