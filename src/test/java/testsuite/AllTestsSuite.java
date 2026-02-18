package testsuite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import testRepositories.TestProjectRepository;
import testRepositories.TestTaskRepository;
import testRepositories.TestUserRepository;
import testServices.TestAssignmentService;
import testServices.TestAuthenticationService;
import testServices.TestProjectService;
import testServices.TestTaskService;

@Suite
@SelectClasses({
        TestUserRepository.class,
        TestAuthenticationService.class,
        TestProjectService.class,
        TestAssignmentService.class,
        TestTaskService.class,
        TestProjectRepository.class,
        TestTaskRepository.class

})

public class AllTestsSuite {
}