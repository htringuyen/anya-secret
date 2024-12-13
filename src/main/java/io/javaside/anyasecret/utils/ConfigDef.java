package io.javaside.anyasecret.utils;

public class ConfigDef {

    private final String name;

    private final boolean isRequired;

    private final String defaultValue;

    private ConfigDef(String name, boolean isRequired, String defaultValue) {
        this.name = name;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
    }

    public static ConfigDef of(String name) {
        return new ConfigDef(name, false, null);
    }

    public static ConfigDef of(String name, boolean isRequired) {
        return new ConfigDef(name, isRequired, null);
    }

    public static ConfigDef of(String name, String defaultValue) {
        return new ConfigDef(name, false, defaultValue);
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
