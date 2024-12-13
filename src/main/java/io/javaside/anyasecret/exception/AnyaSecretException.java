package io.javaside.anyasecret.exception;

public class AnyaSecretException extends RuntimeException {

    public AnyaSecretException(String message) {
        super(message);
    }

    public AnyaSecretException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnyaSecretException() {
        super();
    }

    public AnyaSecretException(Throwable cause) {
        super(cause);
    }
}
