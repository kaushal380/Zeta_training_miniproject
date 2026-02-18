package consoleUI;

import models.Project;
import models.User;
import services.AssignmentService;
import services.ProjectService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientDashboard {

    private final ProjectService projectService;
    private final AssignmentService assignmentService;
    private final Scanner scanner;

    public ClientDashboard(ProjectService projectService,
                           AssignmentService assignmentService,
                           Scanner scanner) {
        this.projectService = projectService;
        this.assignmentService = assignmentService;
        this.scanner = scanner;
    }

    public void start(User client) {

        while (true) {

            System.out.println("\n==================================");
            System.out.println("        CLIENT DASHBOARD");
            System.out.println("==================================");
            System.out.println("1. View All Projects");
            System.out.println("2. View Purchased Projects");
            System.out.println("3. Purchase Project");
            System.out.println("0. Logout");
            System.out.println("==================================");

            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> viewAllProjects();
                case "2" -> viewPurchasedProjects(client);
                case "3" -> purchaseProject(client);
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void viewAllProjects() {

        Map<String, Project> projects = projectService.viewAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        System.out.println("\n=========== ALL PROJECTS ===========");

        int index = 1;

        for (Project project : projects.values()) {
            System.out.println(index + ". "
                    + project.getName()
                    + " | Type: " + project.getType()
                    + " | Status: " + project.getStatus()
                    + " | Cost: " + project.getEstimatedCost());
            index++;
        }

        System.out.println("====================================");
    }

    private void viewPurchasedProjects(User client) {

        List<Project> purchased = new ArrayList<>();

        for (Project project : projectService.viewAllProjects().values()) {
            if (project.getClientIds().contains(client.getId())) {
                purchased.add(project);
            }
        }

        if (purchased.isEmpty()) {
            System.out.println("You have not purchased any projects yet.");
            return;
        }

        System.out.println("\n=========== YOUR PROJECTS ===========");

        int index = 1;
        for (Project project : purchased) {
            System.out.println(index + ". "
                    + project.getName()
                    + " | Status: " + project.getStatus()
                    + " | Type: " + project.getType());
            index++;
        }

        System.out.println("====================================");
    }

    private void purchaseProject(User client) {

        List<Project> available = new ArrayList<>();

        for (Project project : projectService.viewAllProjects().values()) {
            if (!project.getClientIds().contains(client.getId())) {
                available.add(project);
            }
        }

        if (available.isEmpty()) {
            System.out.println("No projects available for purchase.");
            return;
        }

        System.out.println("\n=========== AVAILABLE PROJECTS ===========");

        for (int i = 0; i < available.size(); i++) {
            Project p = available.get(i);
            System.out.println((i + 1) + ". "
                    + p.getName()
                    + " | Type: " + p.getType()
                    + " | Cost: " + p.getEstimatedCost());
        }

        int choice;

        while (true) {
            System.out.print("Select project number to purchase: ");
            String input = scanner.nextLine().trim();

            if (input.matches("\\d+")) {
                choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= available.size()) {
                    break;
                }
            }
            System.out.println("Invalid selection. Try again.");
        }

        Project selectedProject = available.get(choice - 1);

        try {
            boolean purchased = assignmentService
                    .purchaseProject(client, selectedProject.getId());

            if (purchased) {
                System.out.println("Project purchased successfully!");
            } else {
                System.out.println("Purchase failed.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
