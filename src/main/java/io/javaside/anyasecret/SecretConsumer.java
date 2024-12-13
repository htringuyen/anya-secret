package io.javaside.anyasecret;

import java.util.Map;

public interface SecretConsumer extends Pluggable {

    void consume(Map<String, String> secrets);

}
