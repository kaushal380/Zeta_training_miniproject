package testServices;

import models.Project;
import models.User;
import models.UserCredential;
import models.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.ProjectRepository;
import repositories.UserRepository;
import services.AssignmentService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestAssignmentService {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private AssignmentService assignmentService;

    private User admin;
    private User manager;
    private User builder;

    @BeforeEach
    void setUp() {

        projectRepository = Mockito.mock(ProjectRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        assignmentService = new AssignmentService(projectRepository, userRepository);

        admin = new User();
        admin.setRole(UserRole.ADMIN);
        admin.setEmail("admin@test.com");

        manager = new User();
        manager.setRole(UserRole.PROJECT_MANAGER);
        manager.setEmail("manager@test.com");
        manager = new User("M1", "Manager", "", "", null, null, UserRole.PROJECT_MANAGER);
        builder = new User("B1", "Builder", "", "", null, null, UserRole.BUILDER);
    }

    @Test
    void assignProjectToManagerSuccess() {

        Project project = new Project();
        project.setId("P1");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        Map<String, UserCredential> users = new HashMap<>();
        users.put("manager@test.com", new UserCredential("pass", manager));

        when(userRepository.getAllUsers()).thenReturn(users);

        boolean result = assignmentService.assignProjectToManager(admin, "P1", "M1");

        assertTrue(result);
        assertEquals("M1", project.getManagerId());
    }

    @Test
    void assignProjectToManagerUnauthorized() {

        User notAdmin = new User();
        notAdmin.setRole(UserRole.BUILDER);

        assertThrows(RuntimeException.class, () -> assignmentService.assignProjectToManager(notAdmin, "P1", "M1"));
    }

    @Test
    void assignProjectToManagerProjectNotFound() {

        when(projectRepository.getProjectById("P1")).thenReturn(null);

        boolean result = assignmentService.assignProjectToManager(admin, "P1", "M1");

        assertFalse(result);
    }

    @Test
    void assignProjectToManagerInvalidManager() {

        Project project = new Project();
        project.setId("P1");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        when(userRepository.getAllUsers()).thenReturn(new HashMap<>());

        boolean result = assignmentService.assignProjectToManager(admin, "P1", "M1");

        assertFalse(result);
    }

    @Test
    void assignProjectToBuilderSuccess() {

        Project project = new Project();
        project.setId("P1");
        project.setManagerId("M1");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        Map<String, UserCredential> users = new HashMap<>();
        users.put("builder@test.com", new UserCredential("pass", builder));

        when(userRepository.getAllUsers()).thenReturn(users);

        boolean result = assignmentService.assignProjectToBuilder(manager, "P1", "B1");

        assertTrue(result);
        assertTrue(project.getBuilderIds().contains("B1"));
    }

    @Test
    void assignProjectToBuilderUnauthorizedRole() {

        User notManager = new User();
        notManager.setRole(UserRole.BUILDER);

        assertThrows(RuntimeException.class, () -> assignmentService.assignProjectToBuilder(notManager, "P1", "B1"));
    }

    @Test
    void assignProjectToBuilderProjectNotFound() {

        when(projectRepository.getProjectById("P1")).thenReturn(null);

        boolean result = assignmentService.assignProjectToBuilder(manager, "P1", "B1");

        assertFalse(result);
    }

    @Test
    void assignProjectToBuilderManagerNotOwner() {

        Project project = new Project();
        project.setId("P1");
        project.setManagerId("OTHER_ID");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        boolean result = assignmentService.assignProjectToBuilder(manager, "P1", "B1");

        assertFalse(result);
    }

    @Test
    void assignProjectToBuilderInvalidBuilder() {

        Project project = new Project();
        project.setId("P1");
        project.setManagerId("M1");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        when(userRepository.getAllUsers()).thenReturn(new HashMap<>());

        boolean result = assignmentService.assignProjectToBuilder(manager, "P1", "B1");

        assertFalse(result);
    }

    @Test
    void assignProjectToManagerWrongRole() {

        Project project = new Project();
        project.setId("P1");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        User wrongRoleUser = new User("X1", "Wrong", "", "", null, null, UserRole.BUILDER);

        Map<String, UserCredential> users = new HashMap<>();
        users.put("wrong@test.com", new UserCredential("pass", wrongRoleUser));

        when(userRepository.getAllUsers()).thenReturn(users);

        boolean result = assignmentService.assignProjectToManager(admin, "P1", "X1");

        assertFalse(result);
    }

    @Test
    void assignProjectToBuilderWrongRole() {

        Project project = new Project();
        project.setId("P1");
        project.setManagerId("M1");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        User wrongRoleUser = new User("X2", "Wrong", "", "", null, null, UserRole.ADMIN);

        Map<String, UserCredential> users = new HashMap<>();
        users.put("wrong@test.com", new UserCredential("pass", wrongRoleUser));

        when(userRepository.getAllUsers()).thenReturn(users);

        boolean result = assignmentService.assignProjectToBuilder(manager, "P1", "X2");

        assertFalse(result);
    }

    @Test
    void getUserByIdReturnsNullWhenIdDoesNotMatch() {

        User someUser = new User("REAL_ID", "User", "", "", null, null, UserRole.BUILDER);

        Map<String, UserCredential> users = new HashMap<>();
        users.put("test@test.com", new UserCredential("pass", someUser));

        when(userRepository.getAllUsers()).thenReturn(users);

        Project project = new Project();
        project.setId("P1");
        project.setManagerId("M1");

        when(projectRepository.getProjectById("P1")).thenReturn(project);

        boolean result = assignmentService.assignProjectToBuilder(manager, "P1", "NON_EXISTING_ID");

        assertFalse(result);
    }
}
