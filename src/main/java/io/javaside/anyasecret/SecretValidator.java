package io.javaside.anyasecret;

public interface SecretValidator extends Pluggable {

    boolean validate(String secret);
}
