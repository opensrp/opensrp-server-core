package org.opensrp.service.multimedia;

import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.utils.TestUtils.getBasePackageFilePath;

/**
 * Created by Vincent Karuri on 30/10/2019
 */

@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public abstract class BaseMultimediaFileManagerTest {

    public void setUp() throws Exception {
        deleteTestFile();
        createTestFile();
    }

    protected void createTestFile() throws IOException {
        if (!testFileExists()) {
            PrintWriter writer = new PrintWriter(getTestFilePath(), "UTF-8");
            writer.println("The first line");
            writer.close();
        }
    }

    private boolean testFileExists() {
        return new File(getTestFilePath()).exists();
    }

    protected String getTestFilePath() {
        return getTestFileFolder() + File.separator + "test_file";
    }

    protected String getDataFilePath() {
        return getTestFileFolder() + File.separator + "data_file";
    }

    protected String getTestFileFolder() {
        return getBasePackageFilePath() + "/src/test/java/org/opensrp/service/multimedia";
    }

    protected void deleteTestFile() {
        File testFile = new File(getTestFilePath());
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}
