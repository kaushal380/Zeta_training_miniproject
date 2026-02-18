package services;

import models.Address;
import models.Project;
import models.User;
import models.enums.ProjectStatus;
import models.enums.ProjectType;
import models.enums.UserRole;
import repositories.ProjectRepository;
import repositories.UserRepository;

import java.time.LocalDate;
import java.util.Map;

public class AssignmentDemo {

    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository("users_assignment_demo.json");

        ProjectRepository projectRepository = new ProjectRepository("projects_assignment_demo.json");

        AuthenticationService authService = new AuthenticationService(userRepository);

        ProjectService projectService = new ProjectService(projectRepository);

        AssignmentService assignmentService = new AssignmentService(projectRepository, userRepository);

        System.out.println("===== REGISTER USERS =====");

        authService.register("Admin User", "9999999999", "admin@gmail.com", "admin123", LocalDate.of(1985, 1, 1), new Address("City", "State", "123456", "Country"), UserRole.ADMIN);

        authService.register("Manager User", "8888888888", "manager@gmail.com", "manager123", LocalDate.of(1990, 1, 1), new Address("City", "State", "123456", "Country"), UserRole.PROJECT_MANAGER);

        authService.register("Builder User", "7777777777", "builder@gmail.com", "builder123", LocalDate.of(1995, 1, 1), new Address("City", "State", "654321", "Country"), UserRole.BUILDER);

        System.out.println("===== LOGIN USERS =====");

        User admin = authService.login("admin@gmail.com", "admin123");
        User manager = authService.login("manager@gmail.com", "manager123");
        User builder = authService.login("builder@gmail.com", "builder123");

        if (admin == null || manager == null || builder == null) {
            System.out.println("Login failed. Exiting...");
            return;
        }

        System.out.println("Admin ID: " + admin.getId());
        System.out.println("Manager ID: " + manager.getId());
        System.out.println("Builder ID: " + builder.getId());

        System.out.println("\n===== ADMIN CREATES PROJECT =====");

        boolean created = projectService.createProject(admin, "Mall Construction", "City center mall project", LocalDate.now(), LocalDate.now().plusMonths(6), ProjectType.COMMERCIAL, new Address("Bangalore", "Karnataka", "560001", "India"), 1_000_000);

        System.out.println("Project Created: " + created);

        Map<String, Project> projects = projectService.viewAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        String projectId = projects.keySet().iterator().next();

        System.out.println("Created Project ID: " + projectId);

        System.out.println("\n===== ADMIN ASSIGNS PROJECT TO MANAGER =====");

        boolean assignedToManager = assignmentService.assignProjectToManager(admin, projectId, manager.getId());

        System.out.println("Assigned To Manager: " + assignedToManager);

        System.out.println("\n===== MANAGER ASSIGNS PROJECT TO BUILDER =====");

        boolean assignedToBuilder = assignmentService.assignProjectToBuilder(manager, projectId, builder.getId());

        System.out.println("Assigned To Builder: " + assignedToBuilder);

        System.out.println("\n===== FINAL PROJECT DETAILS =====");

        Project updatedProject = projectService.viewProject(projectId);

        System.out.println("Project ID: " + updatedProject.getId());
        System.out.println("Manager ID: " + updatedProject.getManagerId());
        System.out.println("Builder IDs: " + updatedProject.getBuilderIds());
        System.out.println("Status: " + updatedProject.getStatus());
    }
}
