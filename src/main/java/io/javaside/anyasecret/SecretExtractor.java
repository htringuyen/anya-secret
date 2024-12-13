package io.javaside.anyasecret;

import java.io.InputStream;
import java.util.List;

public interface SecretExtractor extends Pluggable {

    List<String> extractFrom(InputStream inStream);

    List<String> extractFrom(String s);
}
