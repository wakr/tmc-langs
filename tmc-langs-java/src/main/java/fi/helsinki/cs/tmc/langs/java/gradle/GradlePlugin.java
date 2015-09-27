package fi.helsinki.cs.tmc.langs.java.gradle;

import fi.helsinki.cs.tmc.langs.domain.CompileResult;
import fi.helsinki.cs.tmc.langs.io.StudentFilePolicy;
import fi.helsinki.cs.tmc.langs.io.sandbox.StudentFileAwareSubmissionProcessor;
import fi.helsinki.cs.tmc.langs.java.AbstractJavaPlugin;
import fi.helsinki.cs.tmc.langs.java.ClassPath;
import fi.helsinki.cs.tmc.langs.java.exception.TestRunnerException;
import fi.helsinki.cs.tmc.langs.java.exception.TestScannerException;
import fi.helsinki.cs.tmc.langs.java.maven.MavenStudentFilePolicy;
import fi.helsinki.cs.tmc.langs.java.testscanner.TestScanner;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GradlePlugin extends AbstractJavaPlugin {

    private static final Logger log = LoggerFactory.getLogger(GradlePlugin.class);

    private static final Path BUILD_FILE = Paths.get("build.gradle");
    private static final Path RESULT_FILE = Paths.get("build", "test_output.txt");
    private static final Path TEST_FOLDER = Paths.get("src");

    public GradlePlugin() {
        super(TEST_FOLDER, new StudentFileAwareSubmissionProcessor(), new TestScanner());
    }

    @Override
    protected ClassPath getProjectClassPath(Path path) throws IOException {
        // build/classes/*
        return null;
    }

    @Override
    protected CompileResult build(Path projectRootPath) {
        log.info("Building gradle project at {}", projectRootPath);

        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(projectRootPath.toFile())
                .connect();

        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuf = new ByteArrayOutputStream();

        int buildResult = getBuildResult(connection, outBuf, errBuf);

        if (buildResult == 0) {
            log.info("Built maven project at {}", projectRootPath);
        } else {
            log.info("Failed to build maven project at {}", projectRootPath);
        }

        return new CompileResult(buildResult, outBuf.toByteArray(), errBuf.toByteArray());
    }

    private int getBuildResult(ProjectConnection connection, ByteArrayOutputStream outBuf, ByteArrayOutputStream errBuf) {
        int buildResult = 1;

        try {
            runBuild(connection, outBuf, errBuf);
            buildResult = 0;
        } catch (Exception ex){
            buildResult = 1;
        } finally {
            if(connection != null) {
                connection.close();
            }
        }
        return buildResult;
    }

    private void runBuild(ProjectConnection connection, ByteArrayOutputStream outBuf, ByteArrayOutputStream errBuf) {
        BuildLauncher build = connection.newBuild();

        build.forTasks("clean", "build");
        build.withArguments("-x", "test");

        build.setStandardOutput(outBuf);
        build.setStandardError(errBuf);
        build.run();
    }

    @Override
    protected File createRunResultFile(Path path) throws TestRunnerException, TestScannerException {

        log.info("Running tests for gradle project at {}", path);

        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(path.toFile())
                .connect();

        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuf = new ByteArrayOutputStream();
        int buildResult = 1;

        try {
            BuildLauncher build = connection.newBuild();

            build.forTasks("clean", "test");

            build.setStandardOutput(outBuf);
            build.setStandardError(errBuf);
            build.run();




            buildResult = 0;
        } catch (Exception ex){
            buildResult = 1;
        } finally {
            if(connection != null) {
                connection.close();
            }
        }

        System.out.println(new String(outBuf.toByteArray()));
        System.out.println(new String(errBuf.toByteArray()));


        if (buildResult != 0) {
            log.error("Could not run tests for gradle project at {}", path);
            throw new TestRunnerException();
        }
        else{
            log.info("Successfully ran tests for gradle project at {}", path);
        }



        return path.toAbsolutePath().resolve("test_output.txt").toFile();
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
