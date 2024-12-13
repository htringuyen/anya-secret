package io.javaside.anyasecret;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternBasedValidatorTest {

    @Test
    void samplePattern() {
        var sampleSecret = "aBcDeFgHi==123456789";

        var props = new Properties();
        props.put("validation.regex", "aBc.*");

        var validator = new PatternBasedValidator();
        validator.configure(props);
        assertTrue(validator.validate(sampleSecret));

    }
}
