package io.javaside.anyasecret.utils;

import io.javaside.anyasecret.exception.AnyaSecretException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class IoUtils {

    private IoUtils() {
        throw new RuntimeException("Utility class");
    }

    public static InputStream getResourceAsStream(String resourcePath, ClassLoader classLoader,
                                                  Class<?> clazz, String resourceDesc, Consumer<String> logger) {
        if (resourcePath == null) {
            throw new IllegalArgumentException("resourcePath may not be null");
        }
        if (resourceDesc == null && logger != null) {
            resourceDesc = resourcePath;
        }
        InputStream result = null;

        try {
            // try absolute path...
            var absolutePath = FileSystems.getDefault().getPath(resourcePath).toAbsolutePath();
            var file = absolutePath.toFile();

            if (file.exists() && file.isFile() && file.canRead()) {
                result = new BufferedInputStream(new FileInputStream(file));
            }
            logMessage(result, logger, resourceDesc, "on file system at " + absolutePath);
        }
        catch (InvalidPathException e) {
            // just continue...
        }
        catch (FileNotFoundException e) {
            // again just continue...
        }
        if (result == null) {
            try {
                // try relative path to current working directory
                var currentPath = FileSystems.getDefault().getPath(".").toAbsolutePath();
                var absoluatePath = currentPath.resolve(Path.of(resourcePath)).toAbsolutePath();
                var file = absoluatePath.toFile();

                if (file.exists() && file.isFile() && file.canRead()) {
                    result = new BufferedInputStream(new FileInputStream(file));
                }

                logMessage(result, logger, resourceDesc,
                        "on filesystem relative to '" + currentPath + "' at '" + absoluatePath + "'");
            }
            catch (InvalidPathException e) {
                // just continue...
            }
            catch (FileNotFoundException e) {
                // again just continue...
            }
        }
        if (result == null && classLoader != null) {
            // try using the class loader
            result = classLoader.getResourceAsStream(resourcePath);
            logMessage(result, logger, resourceDesc, "on classpath");
        }
        if (result == null && clazz != null) {
            // try using the class
            result = clazz.getResourceAsStream(resourcePath);
            if (result == null) {
                result = clazz.getClassLoader().getResourceAsStream(resourcePath);
            }
            logMessage(result, logger, resourceDesc, "on class path");
        }
        if (result == null) {
            // try open url from the path
            try {
                var url = URI.create(resourcePath).toURL();
                result = url.openStream();
            }
            catch (MalformedURLException e) {
                // just continue...
            }
            catch (IOException e) {
                // again just continue...
            }
        }
        // may be null
        return result;
    }

    private static void logMessage(InputStream stream, Consumer<String> logger, String resourceDesc, String msg) {
        if (stream != null) {
            logger.accept("Found " + resourceDesc + " " + msg);
        }
    }

    public static File getDirectoryFrom(String dirPath) {
        File result;

        // try absolute path
        var absolutePath = FileSystems.getDefault().getPath(dirPath).toAbsolutePath();
        result = absolutePath.toFile();

        if (result.exists() && result.isDirectory() && result.canWrite()) {
           return result;
        }

        // try relative path
        var relativePath = Paths.get("").toAbsolutePath();
        result = relativePath.toFile();

        if (result.exists() && result.isDirectory() && result.canWrite()) {
            return result;
        }
        return null;
    }

    public static void writeToFile(File file, String content) {
        try (var writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        }
        catch (IOException e) {
            throw new AnyaSecretException("Error when writing file");
        }
    }
}




































