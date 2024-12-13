package io.javaside.anyasecret;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AnyaSecretApplicationTest {

    @Test
    void successfullyRunWithSecretFound() {
        var app = new AnyaSecretApplication();
        assertDoesNotThrow(app::run);
    }
}
