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

public class ProjectDemo {

    public static void main(String[] args) {

        ProjectRepository repository = new ProjectRepository("demo_projects.json");

        ProjectService projectService = new ProjectService(repository);


        User admin = new User();
        admin.setRole(UserRole.ADMIN);
        admin.setEmail("admin@gmail.com");

        User manager = new User();
        manager.setRole(UserRole.PROJECT_MANAGER);
        manager.setEmail("manager@gmail.com");

        User builder = new User();
        builder.setRole(UserRole.BUILDER);
        builder.setEmail("builder@gmail.com");

        System.out.println("========== CREATE PROJECT ==========");

        boolean created = projectService.createProject(
                admin,
                "Skyline Residency",
                "Luxury Apartments",
                LocalDate.now(),
                LocalDate.now().plusMonths(12),
                ProjectType.RESIDENTIAL,
                new Address("Bangalore", "Karnataka", "560001", "India"),
                5_00_00_000
        );

        System.out.println("Project Created by Admin: " + created);

        try {
            projectService.createProject(
                    manager,
                    "Unauthorized Project",
                    "Should fail",
                    LocalDate.now(),
                    LocalDate.now().plusMonths(6),
                    ProjectType.COMMERCIAL,
                    new Address("Mumbai", "MH", "400001", "India"),
                    2_00_00_000
            );
        } catch (Exception e) {
            System.out.println("Manager create project failed as expected.");
        }


        System.out.println("\n========== VIEW ALL PROJECTS ==========");
        Map<String, Project> projects =
                projectService.viewAllProjects();

        projects.forEach((id, p) -> System.out.println(p));

        String projectId = projects.keySet().iterator().next();


        System.out.println("\n========== PARTIAL UPDATE ==========");

        ProjectUpdateRequest updateRequest = new ProjectUpdateRequest();

        updateRequest.setDescription("Updated luxury apartments");
        updateRequest.setEstimatedCost(6_00_00_000.0);

        boolean updated = projectService.updateProject(
                manager,
                projectId,
                updateRequest
        );

        System.out.println("Partial Update Success: " + updated);

        System.out.println("After Update:");
        System.out.println(projectService.viewProject(projectId));


        System.out.println("\n========== STATUS UPDATE ==========");

        boolean statusUpdated = projectService.updateProjectStatus(
                manager,
                projectId,
                ProjectStatus.IN_PROGRESS
        );

        System.out.println("Status Updated: " + statusUpdated);
        System.out.println(projectService.viewProject(projectId));


        System.out.println("\n========== COMPLETE PROJECT ==========");

        projectService.updateProjectStatus(
                manager,
                projectId,
                ProjectStatus.COMPLETED
        );

        System.out.println(projectService.viewProject(projectId));

        try {
            ProjectUpdateRequest failUpdate = new ProjectUpdateRequest();
            failUpdate.setName("Should Not Update");

            projectService.updateProject(
                    manager,
                    projectId,
                    failUpdate
            );
        } catch (Exception e) {
            System.out.println("Cannot edit completed project as expected.");
        }

        try {
            projectService.updateProjectStatus(
                    builder,
                    projectId,
                    ProjectStatus.IN_PROGRESS
            );
        } catch (Exception e) {
            System.out.println("Builder cannot update status as expected.");
        }

        System.out.println("\n========== DELETE PROJECT ==========");

        boolean deleted = projectService.deleteProject(admin, projectId);
        System.out.println("Deleted by Admin: " + deleted);

        System.out.println("\n========== FINAL PROJECT LIST ==========");
        projectService.viewAllProjects().forEach((id, p) -> System.out.println(p));
    }
}
