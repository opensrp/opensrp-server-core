package org.utils;

import java.nio.file.Paths;

/**
 * Created by Vincent Karuri on 30/10/2019
 */
public class TestUtils {
    public static String getBasePackageFilePath() {
        return Paths.get(".").toAbsolutePath().normalize().toString();
    }
}
