package consoleUI;

import consoleUI.inputValidators.InputValidator;
import dto.ProjectUpdateRequest;
import models.Address;
import models.Project;
import models.User;
import models.enums.ProjectType;
import services.ProjectService;
import services.AuthenticationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdminDashboard {

    private final ProjectService projectService;
    private final AuthenticationService authService;
    private final Scanner scanner;

    public AdminDashboard(ProjectService projectService,
                          AuthenticationService authService,
                          Scanner scanner) {
        this.projectService = projectService;
        this.authService = authService;
        this.scanner = scanner;
    }

    // ================================
    // MAIN DASHBOARD LOOP
    // ================================

    public void show(User admin) {

        while (true) {

            System.out.println("\n==================================");
            System.out.println("        ADMIN DASHBOARD");
            System.out.println("==================================");
            System.out.println("1. View All Projects");
            System.out.println("2. Create Project");
            System.out.println("3. Update Project");
            System.out.println("4. Delete Project");
            System.out.println("5. Register User");
            System.out.println("6. Assign Project to Manager");
            System.out.println("7. View All Managers");
            System.out.println("8. View All Builders");
            System.out.println("9. View All Clients");
            System.out.println("0. Logout");
            System.out.println("==================================");

            int choice = InputValidator.getValidChoice(scanner, "Enter choice");
            switch (choice) {

                case 1 -> viewAllProjects();
                case 2 -> createProject(admin);
                case 3 -> updateProject(admin);
                case 4 -> deleteProject(admin);
                case 5 -> addUser();
                case 6 -> assignProjectToManager();
                case 7 -> viewAllManagers();
                case 8 -> viewAllBuilders();
                case 9 -> viewAllClients();
                case 0 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ================================
    // OPTION METHODS
    // ================================

    private void viewAllProjects() {

        Map<String, Project> projects = projectService.viewAllProjects();

        if (projects.isEmpty()) {
            System.out.println("\nNo projects available.");
            return;
        }

        System.out.println("\n=========== ALL PROJECTS ===========");

        int index = 1;

        for (Project project : projects.values()) {
            System.out.println(index + ". "
                    + project.getName()
                    + " | Status: " + project.getStatus()
                    + " | Type: " + project.getType()
                    + " | Cost: " + project.getEstimatedCost());
            index++;
        }

        System.out.println("====================================");
    }





    private void createProject(User admin) {

        System.out.println("\n=========== CREATE PROJECT ===========");

        String name;
        while (true) {
            System.out.print("Enter Project Name        : ");
            name = scanner.next().trim();
            if (!name.isEmpty()) break;
            System.out.println("Project name cannot be empty.");
        }

        System.out.print("Enter Description         : ");
        String description = scanner.next().trim();

        LocalDate startDate;
        while (true) {
            try {
                System.out.print("Enter Start Date (YYYY-MM-DD): ");
                startDate = LocalDate.parse(scanner.next());
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }

        LocalDate endDate;
        while (true) {
            try {
                System.out.print("Enter End Date (YYYY-MM-DD)  : ");
                endDate = LocalDate.parse(scanner.next());

                if (!endDate.isAfter(startDate)) {
                    System.out.println("End date must be after start date.");
                    continue;
                }

                break;

            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }

        ProjectType type = null;

        while (true) {
            String msg = """
                Select Project Type:
                1. RESIDENTIAL
                2. COMMERCIAL
                3. GOVERNMENT
                4. INDUSTRIAL
                """;

            int input = InputValidator.getValidChoice(scanner, msg);

            switch (input) {
                case 1 -> type = ProjectType.RESIDENTIAL;
                case 2 -> type = ProjectType.COMMERCIAL;
                case 3 -> type = ProjectType.GOVERNMENT;
                case 4 -> type = ProjectType.INDUSTRIAL;
                default -> {
                    System.out.println("Invalid selection.");
                    continue;
                }
            }
            break;
        }

        double estimatedCost;
        while (true) {
            try {
                System.out.print("Enter Estimated Cost      : ");
                estimatedCost = Double.parseDouble(scanner.next());

                if (estimatedCost <= 0) {
                    System.out.println("Cost must be positive.");
                    continue;
                }
                break;

            } catch (Exception e) {
                System.out.println("Invalid cost value.");
            }
        }

        System.out.println("\n----------- Project Location -----------");

        System.out.print("Enter City         : ");
        String city = scanner.nextLine();

        System.out.print("Enter State        : ");
        String state = scanner.nextLine();

        String zip;
        while (true) {
            System.out.print("Enter Zip Code     : ");
            zip = scanner.nextLine();
            if (zip.matches("[0-9]{6}")) break;
            System.out.println("Invalid zip. Must be 6 digits.");
        }

        System.out.print("Enter Country      : ");
        String country = scanner.nextLine();

        Address location = new Address(city, state, zip, country);

        // 8️⃣ Call Service
        try {
            boolean created = projectService.createProject(
                    admin,
                    name,
                    description,
                    startDate,
                    endDate,
                    type,
                    location,
                    estimatedCost
            );

            if (created) {
                System.out.println("\nProject created successfully!");
            } else {
                System.out.println("\nFailed to create project.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProject(User admin) {

        Map<String, Project> projects = projectService.viewAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects available to update.");
            return;
        }

        List<Project> projectList = new ArrayList<>(projects.values());

        System.out.println("\n=========== SELECT PROJECT TO UPDATE ===========");

        for (int i = 0; i < projectList.size(); i++) {
            Project p = projectList.get(i);
            System.out.println((i + 1) + ". "
                    + p.getName()
                    + " | Status: " + p.getStatus()
                    + " | Type: " + p.getType());
        }

        int choice;

        while (true) {
            System.out.print("Select project number: ");
            String input = scanner.next();

            if (input.matches("\\d+")) {
                choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= projectList.size()) {
                    break;
                }
            }
            System.out.println("Invalid selection. Try again.");
        }

        Project selectedProject = projectList.get(choice - 1);
        String projectId = selectedProject.getId();

        System.out.println("\nUpdating Project: " + selectedProject.getName());

        ProjectUpdateRequest request = new ProjectUpdateRequest();

        System.out.print("New Name (Enter to skip): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) {
            request.setName(name);
        }

        System.out.print("New Description (Enter to skip): ");
        String desc = scanner.nextLine();
        if (!desc.isBlank()) {
            request.setDescription(desc);
        }

        System.out.print("New Start Date (YYYY-MM-DD) or Enter to skip: ");
        String startInput = scanner.nextLine();
        if (!startInput.isBlank()) {
            try {
                request.setStartDate(LocalDate.parse(startInput));
            } catch (Exception e) {
                System.out.println("Invalid date format. Skipping start date.");
            }
        }

        System.out.print("New End Date (YYYY-MM-DD) or Enter to skip: ");
        String endInput = scanner.nextLine();
        if (!endInput.isBlank()) {
            try {
                request.setEndDate(LocalDate.parse(endInput));
            } catch (Exception e) {
                System.out.println("Invalid date format. Skipping end date.");
            }
        }

        System.out.print("New Estimated Cost or Enter to skip: ");
        String costInput = scanner.nextLine();
        if (!costInput.isBlank()) {
            try {
                double cost = Double.parseDouble(costInput);
                if (cost > 0) {
                    request.setEstimatedCost(cost);
                } else {
                    System.out.println("Cost must be positive. Skipping cost.");
                }
            } catch (Exception e) {
                System.out.println("Invalid cost value. Skipping cost.");
            }
        }

        System.out.println("""
            Select New Project Type (or press Enter to skip):
            1. RESIDENTIAL
            2. COMMERCIAL
            3. GOVERNMENT
            4. INDUSTRIAL
            """);

        String typeInput = scanner.nextLine();
        if (!typeInput.isBlank()) {
            switch (typeInput) {
                case "1" -> request.setType(ProjectType.RESIDENTIAL);
                case "2" -> request.setType(ProjectType.COMMERCIAL);
                case "3" -> request.setType(ProjectType.GOVERNMENT);
                case "4" -> request.setType(ProjectType.INDUSTRIAL);
                default -> System.out.println("Invalid type. Skipping.");
            }
        }

        System.out.print("Update location? (yes/no): ");
        String locChoice = scanner.nextLine();

        if (locChoice.equalsIgnoreCase("yes")) {

            System.out.print("City: ");
            String city = scanner.nextLine();

            System.out.print("State: ");
            String state = scanner.nextLine();

            String zip;
            while (true) {
                System.out.print("Zip Code (6 digits): ");
                zip = scanner.nextLine();
                if (zip.matches("[0-9]{6}")) break;
                System.out.println("Invalid zip.");
            }

            System.out.print("Country: ");
            String country = scanner.nextLine();

            request.setLocation(new Address(city, state, zip, country));
        }

        try {
            boolean updated = projectService.updateProject(admin, projectId, request);

            if (updated) {
                System.out.println("Project updated successfully!");
            } else {
                System.out.println("Update failed.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void deleteProject(User admin) {
        System.out.println("Deleting project...");

        // ask projectId + call service
    }

    private void addUser() {
        System.out.println("Adding new user...");
        // call authentication logic
    }

    private void assignProjectToManager() {
        System.out.println("Assigning project to manager...");
        // call assignment service
    }

    private void viewAllManagers() {
        System.out.println("Viewing all managers...");
        // fetch users by role
    }

    private void viewAllBuilders() {
        System.out.println("Viewing all builders...");
    }

    private void viewAllClients() {
        System.out.println("Viewing all clients...");
    }
}
