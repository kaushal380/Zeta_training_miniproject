package testServices;

import models.User;
import models.UserCredential;
import models.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.UserRepository;
import services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TestUserService {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User admin;
    private User manager;
    private User client;

    private UserCredential adminCredential;
    private UserCredential managerCredential;
    private UserCredential clientCredential;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        admin = new User();
        admin.setId("1");
        admin.setRole(UserRole.ADMIN);

        manager = new User();
        manager.setId("2");
        manager.setRole(UserRole.PROJECT_MANAGER);

        client = new User();
        client.setId("3");
        client.setRole(UserRole.CLIENT);

        adminCredential = new UserCredential();
        adminCredential.setUser(admin);

        managerCredential = new UserCredential();
        managerCredential.setUser(manager);

        clientCredential = new UserCredential();
        clientCredential.setUser(client);
    }

    @Test
    void shouldReturnUserByEmail() {

        when(userRepository.getUser("admin@test.com"))
                .thenReturn(adminCredential);

        User result = userService.getUserByEmail("admin@test.com");

        assertNotNull(result);
        assertEquals(UserRole.ADMIN, result.getRole());
    }

    @Test
    void shouldReturnNullIfUserNotFound() {

        when(userRepository.getUser("notfound@test.com"))
                .thenReturn(null);

        User result = userService.getUserByEmail("notfound@test.com");

        assertNull(result);
    }

    @Test
    void shouldReturnUsersByRole() {

        Map<String, UserCredential> map = new HashMap<>();
        map.put("a", adminCredential);
        map.put("b", managerCredential);
        map.put("c", clientCredential);

        when(userRepository.getAllUsers()).thenReturn(map);

        List<User> managers = userService.getUsersByRole(UserRole.PROJECT_MANAGER);

        assertEquals(1, managers.size());
        assertEquals(UserRole.PROJECT_MANAGER, managers.get(0).getRole());
    }

    @Test
    void shouldReturnAllUsersIfAdmin() {

        Map<String, UserCredential> map = new HashMap<>();
        map.put("a", adminCredential);
        map.put("b", managerCredential);

        when(userRepository.getAllUsers()).thenReturn(map);

        List<User> users = userService.getAllUsers(admin);

        assertEquals(2, users.size());
    }

    @Test
    void shouldThrowIfNonAdminTriesToViewAllUsers() {

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getAllUsers(manager)
        );

        assertEquals("Only admin can view all the users", exception.getMessage());
    }

    @Test
    void shouldReturnTrueIfEmailExists() {

        when(userRepository.getUser("admin@test.com"))
                .thenReturn(adminCredential);

        assertTrue(userService.emailExists("admin@test.com"));
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {

        when(userRepository.getUser("unknown@test.com"))
                .thenReturn(null);

        assertFalse(userService.emailExists("unknown@test.com"));
    }

    @Test
    void shouldDeleteUserIfAdmin() {

        Map<String, UserCredential> map = new HashMap<>();
        map.put("admin@test.com", adminCredential);

        when(userRepository.getAllUsers()).thenReturn(map);

        boolean result = userService.deleteUser("admin@test.com", admin);

        assertTrue(result);
        assertFalse(map.containsKey("admin@test.com"));
    }

    @Test
    void shouldReturnFalseIfDeletingNonExistingUser() {

        Map<String, UserCredential> map = new HashMap<>();

        when(userRepository.getAllUsers()).thenReturn(map);

        boolean result = userService.deleteUser("missing@test.com", admin);

        assertFalse(result);
    }

    @Test
    void shouldThrowIfNonAdminTriesToDeleteUser() {

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.deleteUser("admin@test.com", manager)
        );

        assertEquals("Only admin can delete a user", exception.getMessage());
    }
}
