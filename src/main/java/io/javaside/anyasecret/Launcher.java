package io.javaside.anyasecret;

public final class Launcher {

    private Launcher() {
        throw new RuntimeException();
    }

    public static void main(String... args) {
        var app = new AnyaSecretApplication();
        app.run();
    }
}
