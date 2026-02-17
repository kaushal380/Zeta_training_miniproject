package testServices;

import dto.ProjectUpdateRequest;
import models.Address;
import models.Project;
import models.User;
import models.enums.ProjectStatus;
import models.enums.ProjectType;
import models.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.ProjectRepository;
import services.ProjectService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestProjectService {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectService projectService;

    private User admin;
    private User manager;
    private User builder;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository);

        admin = new User();
        admin.setRole(UserRole.ADMIN);
        admin.setEmail("admin@test.com");

        manager = new User();
        manager.setRole(UserRole.PROJECT_MANAGER);
        manager.setEmail("manager@test.com");

        builder = new User();
        builder.setRole(UserRole.BUILDER);
        builder.setEmail("builder@test.com");
    }

    @Test
    void createProject_Admin_Success() {
        when(projectRepository.addProject(anyString(), any(Project.class)))
                .thenReturn(true);

        boolean result = projectService.createProject(
                admin,
                "Test",
                "Desc",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                ProjectType.RESIDENTIAL,
                new Address("City", "State", "123", "India"),
                1000
        );

        assertTrue(result);
        verify(projectRepository).addProject(anyString(), any(Project.class));
    }

    @Test
    void createProject_NonAdmin_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                projectService.createProject(
                        manager,
                        "Test",
                        "Desc",
                        LocalDate.now(),
                        LocalDate.now().plusDays(10),
                        ProjectType.RESIDENTIAL,
                        new Address("City", "State", "123", "India"),
                        1000
                ));
    }

    @Test
    void updateProject_Admin_Success() {

        Project project = new Project();
        project.setStatus(ProjectStatus.UPCOMING);

        when(projectRepository.getProjectById("1"))
                .thenReturn(project);

        when(projectRepository.updateProject(eq("1"), any(Project.class)))
                .thenReturn(true);

        ProjectUpdateRequest request = new ProjectUpdateRequest();
        request.setName("Updated Name");

        boolean result = projectService.updateProject(admin, "1", request);

        assertTrue(result);
        assertEquals("Updated Name", project.getName());
        verify(projectRepository).updateProject(eq("1"), any(Project.class));
    }

    @Test
    void updateProject_Manager_Success() {

        Project project = new Project();
        project.setStatus(ProjectStatus.UPCOMING);

        when(projectRepository.getProjectById("1"))
                .thenReturn(project);

        when(projectRepository.updateProject(eq("1"), any(Project.class)))
                .thenReturn(true);

        ProjectUpdateRequest request = new ProjectUpdateRequest();
        request.setEstimatedCost(5000.0);

        boolean result = projectService.updateProject(manager, "1", request);

        assertTrue(result);
        assertEquals(5000.0, project.getEstimatedCost());
    }

    @Test
    void updateProject_UnauthorizedRole_ThrowsException() {
        ProjectUpdateRequest request = new ProjectUpdateRequest();

        assertThrows(RuntimeException.class, () ->
                projectService.updateProject(builder, "1", request));
    }

    @Test
    void updateProject_ProjectNotFound_ReturnsFalse() {

        when(projectRepository.getProjectById("1"))
                .thenReturn(null);

        ProjectUpdateRequest request = new ProjectUpdateRequest();

        boolean result = projectService.updateProject(admin, "1", request);

        assertFalse(result);
    }

    @Test
    void updateProject_CompletedProject_ThrowsException() {

        Project project = new Project();
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectRepository.getProjectById("1"))
                .thenReturn(project);

        ProjectUpdateRequest request = new ProjectUpdateRequest();

        assertThrows(RuntimeException.class, () ->
                projectService.updateProject(admin, "1", request));
    }

    @Test
    void updateProjectStatus_Manager_Success() {

        Project project = new Project();
        project.setStatus(ProjectStatus.UPCOMING);

        when(projectRepository.getProjectById("1"))
                .thenReturn(project);

        when(projectRepository.updateProject(eq("1"), any(Project.class)))
                .thenReturn(true);

        boolean result = projectService.updateProjectStatus(
                manager,
                "1",
                ProjectStatus.IN_PROGRESS
        );

        assertTrue(result);
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
    }

    @Test
    void updateProjectStatus_Completed_SetsEndDate() {

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.getProjectById("1"))
                .thenReturn(project);

        when(projectRepository.updateProject(eq("1"), any(Project.class)))
                .thenReturn(true);

        projectService.updateProjectStatus(
                manager,
                "1",
                ProjectStatus.COMPLETED
        );

        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
        assertEquals(LocalDate.now(), project.getEndDate());
    }

    @Test
    void updateProjectStatus_Unauthorized_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                projectService.updateProjectStatus(
                        builder,
                        "1",
                        ProjectStatus.IN_PROGRESS
                ));
    }

    @Test
    void updateProjectStatus_ProjectNotFound_ReturnsFalse() {

        when(projectRepository.getProjectById("1"))
                .thenReturn(null);

        boolean result = projectService.updateProjectStatus(
                manager,
                "1",
                ProjectStatus.IN_PROGRESS
        );

        assertFalse(result);
    }

    @Test
    void deleteProject_Admin_Success() {

        when(projectRepository.deleteProject("1"))
                .thenReturn(true);

        boolean result = projectService.deleteProject(admin, "1");

        assertTrue(result);
    }

    @Test
    void deleteProject_Admin_ProjectNotFound_ReturnsFalse() {

        when(projectRepository.deleteProject("1"))
                .thenReturn(false);

        boolean result = projectService.deleteProject(admin, "1");

        assertFalse(result);
    }

    @Test
    void deleteProject_Unauthorized_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                projectService.deleteProject(builder, "1"));
    }

    @Test
    void viewProject_ReturnsProject() {

        Project project = new Project();
        when(projectRepository.getProjectById("1"))
                .thenReturn(project);

        Project result = projectService.viewProject("1");

        assertNotNull(result);
    }

    @Test
    void viewAllProjects_ReturnsMap() {

        Map<String, Project> map = new HashMap<>();
        when(projectRepository.getAllProjects())
                .thenReturn(map);

        Map<String, Project> result =
                projectService.viewAllProjects();

        assertEquals(map, result);
    }
}
