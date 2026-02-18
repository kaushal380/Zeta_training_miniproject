package testsuite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import testRepositories.TestUserRepository;
import testServices.TestAuthenticationService;
import testServices.TestProjectService;

@Suite
@SelectClasses({
        TestUserRepository.class,
        TestAuthenticationService.class,
        TestProjectService.class
})

public class AllTestsSuite {
}