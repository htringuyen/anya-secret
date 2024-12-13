package io.javaside.anyasecret;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternBasedValidatorTest {

    @Test
    void samplePattern() {
        var sampleSecret = "MTliZWRiZTY4MWM4MDgyYjNhNTBhMmFjNDQ2NmU2NjJlMzU4ZjRiMTQ0NDAwZjNhNjY4ZWJlZDNmNzVjZjhhNw";

        var props = new Properties();
        props.put("validation.regex", "MTl.*");

        var validator = new PatternBasedValidator();
        validator.configure(props);
        assertTrue(validator.validate(sampleSecret));

    }
}
