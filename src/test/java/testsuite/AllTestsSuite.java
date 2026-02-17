package testsuite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import testRepositories.TestUserRepository;
import testServices.TestAuthenticationService;

@Suite
@SelectClasses({
        TestUserRepository.class,
        TestAuthenticationService.class
})

public class AllTestsSuite {
}