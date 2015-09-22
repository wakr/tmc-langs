package fi.helsinki.cs.tmc.langs.java.gradle;

import fi.helsinki.cs.tmc.langs.domain.CompileResult;
import fi.helsinki.cs.tmc.langs.io.StudentFilePolicy;
import fi.helsinki.cs.tmc.langs.io.sandbox.StudentFileAwareSubmissionProcessor;
import fi.helsinki.cs.tmc.langs.io.sandbox.SubmissionProcessor;
import fi.helsinki.cs.tmc.langs.java.AbstractJavaPlugin;
import fi.helsinki.cs.tmc.langs.java.ClassPath;
import fi.helsinki.cs.tmc.langs.java.exception.TestRunnerException;
import fi.helsinki.cs.tmc.langs.java.exception.TestScannerException;
import fi.helsinki.cs.tmc.langs.java.maven.MavenStudentFilePolicy;
import fi.helsinki.cs.tmc.langs.java.testscanner.TestScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GradlePlugin extends AbstractJavaPlugin {

    private static final Logger log = LoggerFactory.getLogger(GradlePlugin.class);

    private static final Path BUILD_FILE = Paths.get("build.gradle");
    private static final Path TEST_FOLDER = Paths.get("src");

    public GradlePlugin() {
        super(TEST_FOLDER, new StudentFileAwareSubmissionProcessor(), new TestScanner());
    }

    @Override
    protected ClassPath getProjectClassPath(Path path) throws IOException {
        return null;
    }

    @Override
    protected CompileResult build(Path projectRootPath) {
        return null;
    }

    @Override
    protected File createRunResultFile(Path path) throws TestRunnerException, TestScannerException {
        return null;
    }

    @Override
    public String getPluginName() {
        return "gradle";
    }

    @Override
    public boolean isExerciseTypeCorrect(Path path) {
        return Files.exists(path.toAbsolutePath().resolve(BUILD_FILE));
    }

    @Override
    protected StudentFilePolicy getStudentFilePolicy(Path projectPath) {
        return new MavenStudentFilePolicy(projectPath);
    }
}
