package fi.helsinki.cs.tmc.langs.java.gradle;

import fi.helsinki.cs.tmc.langs.abstraction.ValidationError;
import fi.helsinki.cs.tmc.langs.abstraction.ValidationResult;
import fi.helsinki.cs.tmc.langs.domain.CompileResult;
import fi.helsinki.cs.tmc.langs.domain.RunResult;
import fi.helsinki.cs.tmc.langs.utils.TestUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GradlePluginTest {

    private GradlePlugin gradlePlugin;

    public  GradlePluginTest(){
        gradlePlugin = new GradlePlugin();
    }

    @Test
    public void testGetLanguageName(){
        assertEquals("gradle", gradlePlugin.getPluginName());
    }

    @Test
    public void testCheckCodeStyle() {
        Path project = TestUtils.getPath(getClass(), "most_errors");
        ValidationResult result = gradlePlugin.checkCodeStyle(project);
        Map<File, List<ValidationError>> res = result.getValidationErrors();
        assertEquals("Should be one erroneous file", 1, res.size());
        for (File file : res.keySet()) {
            List<ValidationError> errors = res.get(file);
            assertEquals("Should return the right amount of errors", 24, errors.size());
        }
    }

    @Test
    public void testCheckCodeStyleWithUntestableProject() {
        File projectToTest = TestUtils.getPath(getClass(), "dummy_project").toFile();
        ValidationResult result = gradlePlugin.checkCodeStyle(projectToTest.toPath());
        assertNull(result);
    }

    @Test
    public void testPassingGradleBuild(){
        Path project = TestUtils.getPath(getClass(), "gradle_compiling");
        CompileResult result = gradlePlugin.build(project);
        assertEquals("Compile status should be 0 when build passes", 0, result.getStatusCode());
    }

    @Test
    public void testFailingGradleBuild() throws IOException {
        Path project = TestUtils.getPath(getClass(), "gradle_not_compiling");
        CompileResult result = gradlePlugin.build(project);
        assertEquals("Compile status should be 1 when build fails", 1, result.getStatusCode());
    }

    @Test
    public void testRunTestsWhenBuildFailing() {
        Path project = TestUtils.getPath(getClass(), "gradle_not_compiling");
        RunResult runResult = gradlePlugin.runTests(project);
        assertEquals(RunResult.Status.COMPILE_FAILED, runResult.status);
    }

    @Ignore("Test-running not yet working.")
    @Test
    public void testGradleProjectWithFailingTestsCompilesAndFailsTests() {
        Path path = TestUtils.getPath(getClass(), "gradle_failing_tests");
        RunResult result = gradlePlugin.runTests(path);

        assertEquals(RunResult.Status.TESTS_FAILED, result.status);
    }


}