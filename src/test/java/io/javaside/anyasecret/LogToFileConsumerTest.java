package io.javaside.anyasecret;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogToFileConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(LogToFileConsumerTest.class);

    @Test
    void writeSamples() {
        var samples = Map.of(
                "subdir1/file1", "hello1",
                "subdir2/file2", "hello2",
                "subdir3/file3", "hello3");

        var props = new Properties();

        var pathFlattening = true;
        var dirPath = getClass().getResource("").getPath() + "_dummy_dir" + UUID.randomUUID();

        props.put("log.dir", dirPath);
        props.put("path.flattening", String.valueOf(pathFlattening));

        var consumer = new LogToFileConsumer();
        consumer.configure(props);

        consumer.consume(samples);

        var logDir = Path.of(dirPath).toFile();

        for (var e : samples.entrySet()) {
            var resolvePath = pathFlattening ? e.getKey().replace("/", ".") : e.getKey();
            var file = logDir.toPath().resolve(resolvePath).toFile();
            assertTrue(file.exists());
            assertTrue(file.isFile());
            assertTrue(file.delete());
        }

        if (!logDir.delete()) {
            logger.info("Cannot delete dir {}", logDir.getAbsolutePath());
        }
    }
}
