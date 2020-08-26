package com.test.demo;

import com.test.demo.controllers.WmqTestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:test-application.properties")
class WmbTestServiceRunTests {

    @LocalServerPort
	private int port;

    @Autowired
    private WmqTestController controllerUnderTest;

    private static final String ENV = System.getProperty("test.env", "undefined").toUpperCase();
    private static final String REPORT_TITLE = "wmb-testing-service-api (karate) " + ENV;
    private static final String GLOBAL_IGNORE_ALL = "~@ignore";
    private static final String GLOBAL_INCLUDE = "@" + System.getProperty("test.type", "reg").toLowerCase();

    @Test
    public void runKarateTests() {
        assertThat(controllerUnderTest).isNotNull();
        Results results = Runner.parallel(1, "classpath:features", GLOBAL_IGNORE_ALL, GLOBAL_INCLUDE);
        // fast-fail if no tests were found
        Assertions.assertTrue(results.getFeatureCount() > 0, "Did not find any cucumber tests to execute.");
        generateReport(results.getReportDir());
        Assertions.assertEquals(0, results.getFailCount(), "Had at least one test failure.");
    }

    static void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);
        List<String> jsonPaths = jsonFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList());
        Configuration config = new Configuration(new File("target"), REPORT_TITLE);
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

}
