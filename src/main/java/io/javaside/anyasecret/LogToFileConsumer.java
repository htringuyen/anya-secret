package io.javaside.anyasecret;

import io.javaside.anyasecret.exception.AnyaSecretException;
import io.javaside.anyasecret.utils.ConfigDef;
import io.javaside.anyasecret.utils.IoUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static io.javaside.anyasecret.utils.MiscUtils.stringValueFrom;

class LogToFileConsumer implements SecretConsumer {

    private static final ConfigDef LOG_DIR_DEF = ConfigDef.of("log.dir", true);

    private static final ConfigDef PATH_FLATTENING = ConfigDef.of("path.flattening", "true");

    private boolean pathFlattening;

    private File logDir;

    @Override
    public void configure(Properties props) {
        this.pathFlattening = checkPathFlattening(stringValueFrom(props, PATH_FLATTENING));
        var dirPath = stringValueFrom(props, LOG_DIR_DEF);
        this.logDir = Path.of(dirPath).toFile();
        if (!logDir.exists() && !logDir.mkdir() && logDir.canWrite()) {
            throw new AnyaSecretException("Cannot access and/or create dir with path " + dirPath);
        }
    }

    @Override
    public void consume(Map<String, String> secrets) {
        secrets.forEach(this::resolveSecret);
    }

    private boolean checkPathFlattening(String option) {
        if (option.equals("true")) {
            return true;
        } else if (option.equals("false")) {
            return false;
        } else {
            throw new AnyaSecretException("Unknown option for path flattening: " + option);
        }
    }

    private void resolveSecret(String path, String secret) {
        var resolvePath = pathFlattening ? path.replace("/", ".") : path;
        var file = logDir.toPath().resolve(resolvePath).toFile();
        IoUtils.writeToFile(file, secret);
    }

}















