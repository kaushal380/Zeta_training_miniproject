package testRepositories;

import models.Address;
import models.Project;
import models.enums.ProjectStatus;
import models.enums.ProjectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.ProjectRepository;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestProjectRepository {

    private static final String TEST_FILE = "test_projects.json";
    private ProjectRepository repository;

    @BeforeEach
    void setUp() {

        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        repository = new ProjectRepository(TEST_FILE);
    }

    @AfterEach
    void tearDown() {

        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private Project createProject(String id) {

        return new Project(id, "Test Project", "Description", LocalDate.now(), LocalDate.now().plusDays(10), ProjectStatus.UPCOMING, ProjectType.RESIDENTIAL, new Address("City", "State", "123", "India"), 5000.0);
    }

    @Test
    void testAddProjectSuccess() {

        Project project = createProject("1");

        boolean result = repository.addProject("1", project);

        assertTrue(result);
        assertNotNull(repository.getProjectById("1"));
    }

    @Test
    void testAddProjectDuplicate() {

        Project project = createProject("1");

        repository.addProject("1", project);
        boolean second = repository.addProject("1", project);

        assertFalse(second);
    }

    @Test
    void testUpdateProjectSuccess() {

        Project project = createProject("1");
        repository.addProject("1", project);

        project.setName("Updated");

        boolean updated = repository.updateProject("1", project);

        assertTrue(updated);
        assertEquals("Updated", repository.getProjectById("1").getName());
    }

    @Test
    void testUpdateProjectNotFound() {

        Project project = createProject("1");

        boolean updated = repository.updateProject("1", project);

        assertFalse(updated);
    }

    @Test
    void testDeleteProjectSuccess() {

        Project project = createProject("1");
        repository.addProject("1", project);

        boolean deleted = repository.deleteProject("1");

        assertTrue(deleted);
        assertNull(repository.getProjectById("1"));
    }

    @Test
    void testDeleteProjectNotFound() {

        boolean deleted = repository.deleteProject("999");
        assertFalse(deleted);
    }

    @Test
    void testGetProjectById() {

        Project project = createProject("1");
        repository.addProject("1", project);
        assertNotNull(repository.getProjectById("1"));
    }

    @Test
    void testGetAllProjects() {

        repository.addProject("1", createProject("1"));
        repository.addProject("2", createProject("2"));

        Map<String, Project> all = repository.getAllProjects();

        assertEquals(2, all.size());
    }

    @Test
    void testLoadFromFileWhenFileExists() {

        repository.addProject("1", createProject("1"));
        ProjectRepository newRepo = new ProjectRepository(TEST_FILE);
        assertNotNull(newRepo.getProjectById("1"));
    }

    @Test
    void testLoadFromFileEmptyFile() throws Exception {
        new File(TEST_FILE).createNewFile();
        ProjectRepository repo = new ProjectRepository(TEST_FILE);
        assertNotNull(repo);
    }

    @Test
    void testLoadFromFileCorruptedJson() throws Exception {
        Files.writeString(new File(TEST_FILE).toPath(), "INVALID_JSON");
        assertThrows(RuntimeException.class, () -> new ProjectRepository(TEST_FILE));
    }

    @Test
    void testSaveToFileFailure() {

        ProjectRepository badRepo = new ProjectRepository("/invalid/path/projects.json");
        Project project = createProject("1");
        assertThrows(RuntimeException.class, () -> badRepo.addProject("1", project));
    }
}
