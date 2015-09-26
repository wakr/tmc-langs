package fi.helsinki.cs.tmc.langs.java.gradle;

import fi.helsinki.cs.tmc.langs.abstraction.ValidationError;
import fi.helsinki.cs.tmc.langs.abstraction.ValidationResult;
import fi.helsinki.cs.tmc.langs.domain.CompileResult;
import fi.helsinki.cs.tmc.langs.utils.TestUtils;
import org.junit.Test;

import java.io.File;
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
        File projectToTest = new File("src/test/resources/dummy_project/");
        ValidationResult result = gradlePlugin.checkCodeStyle(projectToTest.toPath());
        assertNull(result);
    }

    @Test
    public void testPassingGradleBuild(){
        Path project = TestUtils.getPath(getClass(), "gradle_failing_one");
        CompileResult result = gradlePlugin.build(project);
        assertEquals("Compile status should be 0 when build passes", 0, result.getStatusCode());
    }


}