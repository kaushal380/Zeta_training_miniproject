package consoleUI;

import consoleUI.inputValidators.InputValidator;
import dto.ProjectUpdateRequest;
import models.Address;
import models.Project;
import models.User;
import models.enums.ProjectType;
import models.enums.UserRole;
import services.AssignmentService;
import services.AuthenticationService;
import services.ProjectService;
import services.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdminDashboard {

    private final ProjectService projectService;
    private final AuthenticationService authService;
    private final AssignmentService assignmentService;
    private final UserService userService;
    private final Scanner scanner;

    public AdminDashboard(ProjectService projectService, AuthenticationService authService, AssignmentService assignmentService, UserService userService, Scanner scanner) {
        this.projectService = projectService;
        this.authService = authService;
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.scanner = scanner;
    }

    public void start(User admin) {
        while (true) {

            System.out.println("\n====================================");
            System.out.println("            ADMIN PANEL");
            System.out.println("====================================");
            System.out.println("1. View Projects");
            System.out.println("2. Create Project");
            System.out.println("3. Update Project");
            System.out.println("4. Delete Project");
            System.out.println("5. Register User");
            System.out.println("6. Assign Project to Manager");
            System.out.println("7. View All Users");
            System.out.println("8. Delete User");
            System.out.println("0. Logout");
            System.out.println("====================================");

            int choice = InputValidator.getValidChoice(scanner, "Select an option");

            switch (choice) {
                case 1 -> viewAllProjects();
                case 2 -> createProject(admin);
                case 3 -> updateProject(admin);
                case 4 -> deleteProject(admin);
                case 5 -> registerUser();
                case 6 -> assignProject(admin);
                case 7 -> viewAllUsers(admin);
                case 8 -> deleteUser(admin);
                case 0 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Please select a valid option.");
            }
        }
    }

    private void viewAllProjects() {

        Map<String, Project> projects = projectService.viewAllProjects();

        if (projects.isEmpty()) {
            System.out.println("\nNo projects found.");
            return;
        }

        System.out.println("\n========== PROJECT LIST ==========");

        int index = 1;
        for (Project project : projects.values()) {
            System.out.println(index++ + ". " + project.getName() + " | " + project.getStatus() + " | " + project.getType() + " | Cost: " + project.getEstimatedCost());
        }
    }

    private void createProject(User admin) {

        System.out.println("\n========== CREATE PROJECT ==========");

        String name = getNonEmptyInput("Project Name");
        String description = getNonEmptyInput("Project Description");

        LocalDate startDate = getValidDate("Start Date (YYYY-MM-DD)");
        LocalDate endDate;

        while (true) {
            endDate = getValidDate("End Date (YYYY-MM-DD)");
            if (endDate.isAfter(startDate)) break;
            System.out.println("End date must be after start date.");
        }

        ProjectType type = getProjectType();

        double cost = getPositiveDouble("Estimated Cost");

        System.out.println("\nEnter Project Location Details");
        String city = getNonEmptyInput("City");
        String state = getNonEmptyInput("State");
        String zip = getValidZip();
        String country = getNonEmptyInput("Country");

        Address location = new Address(city, state, zip, country);

        try {
            boolean created = projectService.createProject(
                    admin, name, description, startDate, endDate, type, location, cost);

            System.out.println(created ? "Project created successfully." : "Project creation failed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProject(User admin) {

        List<Project> projectList = new ArrayList<>(projectService.viewAllProjects().values());

        if (projectList.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        Project project = selectProject(projectList, "UPDATE");

        ProjectUpdateRequest request = new ProjectUpdateRequest();

        System.out.print("New Name (press Enter to skip): ");
        String name = scanner.nextLine().trim();
        if (!name.isBlank()) {
            request.setName(name);
        }

        System.out.print("New Description (press Enter to skip): ");
        String desc = scanner.nextLine().trim();
        if (!desc.isBlank()) {
            request.setDescription(desc);
        }

        System.out.print("New End Date (YYYY-MM-DD) (press Enter to skip): ");
        String endInput = scanner.nextLine().trim();

        if (!endInput.isBlank()) {
            try {
                LocalDate newEndDate = LocalDate.parse(endInput);

                LocalDate referenceStart = project.getStartDate();

                if (!newEndDate.isAfter(referenceStart)) {
                    System.out.println("End date must be after start date. Skipping update.");
                } else {
                    request.setEndDate(newEndDate);
                }

            } catch (Exception e) {
                System.out.println("Invalid date format. Skipping end date update.");
            }
        }

        System.out.print("New Estimated Cost (press Enter to skip): ");
        String costInput = scanner.nextLine().trim();

        if (!costInput.isBlank()) {
            try {
                double cost = Double.parseDouble(costInput);
                if (cost > 0) {
                    request.setEstimatedCost(cost);
                } else {
                    System.out.println("Cost must be positive. Skipping update.");
                }
            } catch (Exception e) {
                System.out.println("Invalid cost value. Skipping update.");
            }
        }

        try {
            boolean updated = projectService.updateProject(admin, project.getId(), request);

            System.out.println(updated ? "Project updated successfully." : "Update failed.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteProject(User admin) {

        List<Project> projectList = new ArrayList<>(projectService.viewAllProjects().values());

        if (projectList.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        Project project = selectProject(projectList, "DELETE");

        try {
            boolean deleted = projectService.deleteProject(admin, project.getId());
            System.out.println(deleted ? "Project deleted." : "Deletion failed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void assignProject(User admin) {

        List<Project> projectList = new ArrayList<>(projectService.viewAllProjects().values());

        if (projectList.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        Project project = selectProject(projectList, "ASSIGN");

        List<User> managers = userService.getUsersByRole(UserRole.PROJECT_MANAGER);

        if (managers.isEmpty()) {
            System.out.println("No managers available.");
            return;
        }

        System.out.println("\nSelect Manager:");
        for (int i = 0; i < managers.size(); i++) {
            System.out.println((i + 1) + ". " + managers.get(i).getName());
        }

        int choice = InputValidator.getValidChoice(scanner, "Enter manager number");

        if (choice < 1 || choice > managers.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        User manager = managers.get(choice - 1);

        try {
            assignmentService.assignProjectToManager(admin, project.getId(), manager.getId());
            System.out.println("Project assigned successfully.");
        } catch (Exception e) {
            System.out.println("Assignment failed: " + e.getMessage());
        }
    }

    private void viewAllUsers(User admin) {

        List<User> users = userService.getAllUsers(admin);

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.printf("%-36s | %-20s | %-15s%n", "ID", "Name", "Role");
        System.out.println("--------------------------------------------------------------------");

        for (User user : users) {
            System.out.printf("%-36s | %-20s | %-15s%n", user.getId(), user.getName(), user.getRole());
        }
    }

    private void deleteUser(User admin) {

        String email;

        while (true) {
            System.out.print("Enter user email to delete: ");
            email = scanner.nextLine().trim();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
                break;

            System.out.println("Invalid email format.");
        }

        if (!userService.emailExists(email)) {
            System.out.println("No user found with this email.");
            return;
        }

        try {
            userService.deleteUser(email, admin);
            System.out.println("User deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerUser() {
        User user = AuthenticationDashboard.handleRegister(authService, scanner, true);
        if (user != null) {
            System.out.println("User registered successfully.");
        }
    }

    private Project selectProject(List<Project> list, String action) {

        System.out.println("\nSelect project to " + action + ":");

        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i).getName());
        }

        while (true) {
            System.out.print("Enter number: ");
            String input = scanner.nextLine().trim();

            if (input.matches("\\d+")) {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= list.size())
                    return list.get(choice - 1);
            }

            System.out.println("Invalid selection.");
        }
    }

    private String getNonEmptyInput(String field) {
        while (true) {
            System.out.print(field + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println(field + " cannot be empty.");
        }
    }

    private LocalDate getValidDate(String label) {
        while (true) {
            try {
                System.out.print(label + ": ");
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }
    }

    private double getPositiveDouble(String label) {
        while (true) {
            try {
                System.out.print(label + ": ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
            } catch (Exception ignored) {
            }
            System.out.println("Please enter a valid positive number.");
        }
    }

    private String getValidZip() {
        while (true) {
            System.out.print("Zip Code (6 digits): ");
            String zip = scanner.nextLine().trim();
            if (zip.matches("[0-9]{6}")) return zip;
            System.out.println("Invalid zip code.");
        }
    }

    private ProjectType getProjectType() {

        while (true) {
            System.out.println("""
                    Select Project Type:
                    1. RESIDENTIAL
                    2. COMMERCIAL
                    3. GOVERNMENT
                    4. INDUSTRIAL
                    """);

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    return ProjectType.RESIDENTIAL;
                }
                case "2" -> {
                    return ProjectType.COMMERCIAL;
                }
                case "3" -> {
                    return ProjectType.GOVERNMENT;
                }
                case "4" -> {
                    return ProjectType.INDUSTRIAL;
                }
                default -> System.out.println("Invalid selection.");
            }
        }
    }
}