package testServices;

import models.Address;
import models.User;
import models.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.UserRepository;
import services.AuthenticationService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestAuthenticationService {

    private AuthenticationService authService;
    private UserRepository userRepository;

    private Address address;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository("users.json");
        authService = new AuthenticationService(userRepository);

        address = new Address(
                "Bangalore",
                "Karnataka",
                560001,
                "India"
        );
    }

    @Test
    void testRegisterSuccess() {
        boolean result = authService.register(
                "Kaushal",
                "9999999999",
                "test@gmail.com",
                "password",
                LocalDate.of(2000, 1, 1),
                address,
                UserRole.PROJECT_MANAGER
        );

        assertTrue(result);
        assertNotNull(userRepository.getUser("test@gmail.com"));
    }

    @Test
    void testRegisterDuplicateEmail() {
        authService.register(
                "Kaushal",
                "9999999999",
                "dup@gmail.com",
                "password",
                LocalDate.of(2000, 1, 1),
                address,
                UserRole.PROJECT_MANAGER
        );

        boolean secondAttempt = authService.register(
                "Another",
                "8888888888",
                "dup@gmail.com",
                "password",
                LocalDate.of(1999, 1, 1),
                address,
                UserRole.ADMIN
        );

        assertFalse(secondAttempt);
    }

    @Test
    void testLoginSuccess() {
        authService.register(
                "Kaushal",
                "9999999999",
                "login@gmail.com",
                "password",
                LocalDate.of(2000, 1, 1),
                address,
                UserRole.ADMIN
        );

        User user = authService.login("login@gmail.com", "password");

        assertNotNull(user);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testLoginWrongPassword() {
        authService.register(
                "Kaushal",
                "9999999999",
                "wrongpass@gmail.com",
                "password",
                LocalDate.of(2000, 1, 1),
                address,
                UserRole.ADMIN
        );

        User user = authService.login("wrongpass@gmail.com", "wrong");

        assertNull(user);
    }

    @Test
    void testLoginUserNotFound() {
        User user = authService.login("notfound@gmail.com", "password");
        assertNull(user);
    }
}
