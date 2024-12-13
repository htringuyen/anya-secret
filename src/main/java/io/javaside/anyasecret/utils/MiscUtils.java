package io.javaside.anyasecret.utils;

import io.javaside.anyasecret.exception.AnyaSecretException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Properties;

public class MiscUtils {

    private static final Logger logger = LoggerFactory.getLogger(MiscUtils.class);

    public MiscUtils() {
        throw new RuntimeException("Utility class");
    }

    public static String stringValueFrom(Properties props, ConfigDef configDef) {
        var value = props.getProperty(configDef.getName());
        if (value == null && configDef.isRequired()) {
            throw new AnyaSecretException("Configuration " + configDef.getName() + " is required but unset");
        }
        return value != null ? value : configDef.getDefaultValue();
    }

    public static String normalizeDirPath(String rawPath) {
        var result = rawPath.strip();
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        if (!result.endsWith("/")) {
            result = result + "/";
        }
        return result;
    }

    public static String consumeAsString(InputStream inStream) {
        var content = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(inStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new AnyaSecretException("Error while consume input stream", e);
        }
        return content.toString();
    }

    public static String base64Decode(String value) {
        return new String(Base64.getDecoder().decode(value));
    }
}























