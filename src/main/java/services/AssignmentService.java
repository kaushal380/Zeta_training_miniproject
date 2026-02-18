package services;

import models.Project;
import models.User;
import models.UserCredential;
import models.enums.UserRole;
import repositories.ProjectRepository;
import repositories.UserRepository;

import java.util.logging.Logger;

public class AssignmentService {

    private static final Logger logger = Logger.getLogger(AssignmentService.class.getName());

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public AssignmentService(ProjectRepository projectRepository, UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public boolean assignProjectToManager(User admin, String projectId, String managerId) {

        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only Admin can assign project to manager");
        }

        Project project = projectRepository.getProjectById(projectId);

        if (project == null) {
            logger.warning("Project not found: " + projectId);
            return false;
        }

        User manager = getUserById(managerId);

        if (manager == null || manager.getRole() != UserRole.PROJECT_MANAGER) {

            logger.warning("Invalid manager ID: " + managerId);
            return false;
        }

        if (project.getManagerId() != null) {

            if (project.getManagerId().equals(managerId)) {
                logger.info("Manager already assigned to this project.");
                return true;
            }

            logger.warning("Project already has a manager assigned.");
            return false;
        }

        project.setManagerId(managerId);
        projectRepository.updateProject(projectId, project);

        logger.info("Project " + projectId + " assigned to Manager " + manager.getName());

        return true;
    }

    public boolean assignProjectToBuilder(User manager, String projectId, String builderId) {

        if (manager.getRole() != UserRole.PROJECT_MANAGER) {
            throw new RuntimeException("Only Project Manager can assign project to builder");
        }

        Project project = projectRepository.getProjectById(projectId);

        if (project == null) {
            logger.warning("Project not found: " + projectId);
            return false;
        }

        if (!manager.getId().equals(project.getManagerId())) {
            logger.warning("Manager not authorized for this project");
            return false;
        }

        User builder = getUserById(builderId);

        if (builder == null || builder.getRole() != UserRole.BUILDER) {

            logger.warning("Invalid builder ID: " + builderId);
            return false;
        }

        if (project.getBuilderIds().contains(builderId)) {
            logger.info("Builder already assigned to this project.");
            return true;
        }

        project.getBuilderIds().add(builderId);
        projectRepository.updateProject(projectId, project);

        logger.info("Project " + projectId + " assigned to Builder " + builder.getName());

        return true;
    }

    private User getUserById(String userId) {

        for (UserCredential credential : userRepository.getAllUsers().values()) {

            User user = credential.getUser();

            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
}
