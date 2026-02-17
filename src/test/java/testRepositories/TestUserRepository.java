package testRepositories;

import models.User;
import models.UserCredential;
import models.Address;
import models.enums.UserRole;
import org.junit.jupiter.api.*;
import repositories.UserRepository;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserRepository {

    private static final String TEST_FILE = "test_users.json";
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        repository = new UserRepository(TEST_FILE);
    }

    @AfterEach
    void tearDown() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private UserCredential createCredential(String email) {
        User user = new User(
                "1",
                "Test User",
                "9999999999",
                email,
                LocalDate.of(2000,1,1),
                new Address("City","State","123","Country"),
                UserRole.ADMIN
        );

        return new UserCredential("password", user);
    }

    @Test
    void testAddUserSuccess() {
        UserCredential credential = createCredential("test@gmail.com");

        boolean result = repository.addUser("test@gmail.com", credential);

        assertTrue(result);
        assertNotNull(repository.getUser("test@gmail.com"));
    }

    @Test
    void testAddUserDuplicate() {
        UserCredential credential = createCredential("dup@gmail.com");

        repository.addUser("dup@gmail.com", credential);
        boolean second = repository.addUser("dup@gmail.com", credential);

        assertFalse(second);
    }

    @Test
    void testGetUser() {
        UserCredential credential = createCredential("get@gmail.com");

        repository.addUser("get@gmail.com", credential);

        UserCredential fetched = repository.getUser("get@gmail.com");

        assertNotNull(fetched);
        assertEquals("password", fetched.getPassword());
    }

    @Test
    void testGetAllUsers() {
        repository.addUser("one@gmail.com", createCredential("one@gmail.com"));
        repository.addUser("two@gmail.com", createCredential("two@gmail.com"));

        assertEquals(2, repository.getAllUsers().size());
    }

    @Test
    void testLoadFromFileWhenFileExists() {
        repository.addUser("load@gmail.com", createCredential("load@gmail.com"));

        UserRepository newRepo = new UserRepository(TEST_FILE);

        assertNotNull(newRepo.getUser("load@gmail.com"));
    }

    @Test
    void testLoadFromFileCorruptedJson() throws Exception {

        File file = new File(TEST_FILE);
        java.nio.file.Files.writeString(file.toPath(), "INVALID JSON");

        assertThrows(RuntimeException.class, () -> {
            new UserRepository(TEST_FILE);
        });
    }

    @Test
    void testSaveToFileFailure() {

        UserRepository badRepo = new UserRepository("/invalid/path/users.json");

        UserCredential credential = createCredential("fail@gmail.com");

        assertThrows(RuntimeException.class, () -> {
            badRepo.addUser("fail@gmail.com", credential);
        });
    }
    @Test
    void testDefaultConstructor() {
        File file = new File("users.json");
        if (file.exists()) {
            file.delete();
        }
        UserRepository repo = new UserRepository();
        assertNotNull(repo);
        assertTrue(repo.getAllUsers().isEmpty());

        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testLoadFromFileWhenFileIsEmpty() throws Exception {

        File file = new File(TEST_FILE);
        file.createNewFile();

        assertEquals(0, file.length());

        UserRepository repo = new UserRepository(TEST_FILE);
        assertTrue(repo.getAllUsers().isEmpty());
    }

}
