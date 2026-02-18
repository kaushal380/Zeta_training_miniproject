package consoleUI;

import models.User;
import models.enums.UserRole;
import repositories.ProjectRepository;
import repositories.TaskRepository;
import repositories.UserRepository;
import services.*;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // Repositories
    private static final UserRepository userRepository =
            new UserRepository("users.json");

    private static final ProjectRepository projectRepository =
            new ProjectRepository("projects.json");

    private static final TaskRepository taskRepository =
            new TaskRepository("tasks.json");

    // Services
    private static final AuthenticationService authService =
            new AuthenticationService(userRepository);

    private static final ProjectService projectService =
            new ProjectService(projectRepository);

    private static final TaskService taskService =
            new TaskService(taskRepository);

    private static final AssignmentService assignmentService =
            new AssignmentService(projectRepository, userRepository);


    private static final UserService userService = new UserService(userRepository);


    public static void main(String[] args) {

        System.out.println("===== BUILDER PORTFOLIO APPLICATION =====");

        User user =
                AuthenticationDashboard.authenticate(authService, scanner);

        if (user == null) {
            System.out.println("Authentication failed.");
            return;
        }

        System.out.println("\nLogin Successful!");
        System.out.println(user);

        switch (user.getRole()) {

            case ADMIN ->
                    new AdminDashboard(projectService, authService, assignmentService, userService,  scanner).start(
                            user
                    );

            case PROJECT_MANAGER ->
                    ProjectManagerDashboard.start(
                            user,
                            projectService,
                            taskService,
                            assignmentService,
                            scanner
                    );

            case BUILDER ->
                    BuilderDashboard.start(
                            user,
                            taskService,
                            projectService,
                            scanner
                    );

            case CLIENT -> new ClientDashboard(projectService, assignmentService, scanner).start(user);

            default ->
                    System.out.println("Role not supported.");
        }
    }
}
