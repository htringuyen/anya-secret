package io.javaside.anyasecret;

import java.util.List;
import java.util.Map;

public interface SecretFinder extends Pluggable {

    Map<String, String> findSecrets();

    void setExtractor(SecretExtractor extractor);

    void setValidator(SecretValidator validator);

}
