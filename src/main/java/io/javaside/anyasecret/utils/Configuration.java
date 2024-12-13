package io.javaside.anyasecret.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Configuration {

    Logger logger = LoggerFactory.getLogger(Configuration.class);

    static Configuration from(Properties properties) {
        var props = new Properties();
        if (properties != null) {
            props.putAll(properties);
        }
        return new Configuration() {
            @Override
            public String getString(String key) {
                return props.getProperty(key);
            }

            @Override
            public Set<String> keys() {
                return props.stringPropertyNames();
            }

            @Override
            public String toString() {
                return asProperties().toString();
            }
        };
    }

    static Configuration load(InputStream inStream) throws IOException {
        try {
            var properties = new Properties();
            properties.load(inStream);
            return from(properties);
        }
        finally {
            inStream.close();
        }
    }

    static Configuration load(String resourcePath) throws IOException {
        return load(resourcePath, null, null, logger::debug);
    }

    static Configuration load(String resourcePath, Class<?> clazz) throws IOException {
        return load(resourcePath, null, clazz, logger::debug);
    }

    static Configuration load(String resourcePath, ClassLoader classLoader) throws IOException {
        return load(resourcePath, classLoader, null, logger::debug);
    }

    static Configuration load(String resourcePath, ClassLoader classLoader, Class<?> clazz, Consumer<String> logger) throws IOException {
        var stream = IoUtils.getResourceAsStream(resourcePath, classLoader, clazz, null, logger);
        return load(stream);
    }


    String getString(String key);

    Set<String> keys();

    default Properties asProperties() {
        var props = new Properties();
        keys().stream()
                .filter(Objects::nonNull)
                .forEach(key -> {
                    props.put(key, getString(key));
                });
        return props;
    }

    default Configuration subset(String prefix, boolean removePrefix) {
        if (prefix == null) {
            return this;
        }
        prefix = prefix.strip();
        if (prefix.isEmpty()) {
            return this;
        }
        var extendPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
        Predicate<String> matcher = key -> key != null && key.startsWith(extendPrefix);
        int minLength = extendPrefix.length();
        Function<String, String> prefixRemover = key -> removePrefix ? key.substring(minLength) : key;
        return filter(matcher).map(prefixRemover);
    }

    default Configuration filter(Predicate<String> matcher) {
        if (matcher == null) {
            return this;
        }
        return new Configuration() {
            @Override
            public String getString(String key) {
                return matcher.test(key) ? Configuration.this.getString(key) : null;
            }

            @Override
            public Set<String> keys() {
                return Configuration.this.keys()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(matcher)
                        .collect(Collectors.toUnmodifiableSet());
            }

            @Override
            public String toString() {
                return asProperties().toString();
            }
        };
    }

    default Configuration map(Function<String, String> mapper) {
        if (mapper == null) {
            return this;
        }
        var newToOld = new HashMap<String, String>();
        keys().stream()
                .filter(Objects::nonNull)
                .forEach(oldKey -> {
                    var newKey = mapper.apply(oldKey);
                    if (newKey != null) {
                        newToOld.put(newKey, oldKey);
                    }
                });
        return new Configuration() {
            @Override
            public String getString(String key) {
                var oldKey = newToOld.get(key);
                return Configuration.this.getString(oldKey);
            }

            @Override
            public Set<String> keys() {
                return newToOld.keySet();
            }

            @Override
            public String toString() {
                return asProperties().toString();
            }
        };
    }
}



























