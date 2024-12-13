package io.javaside.anyasecret;

import io.javaside.anyasecret.exception.AnyaSecretException;
import io.javaside.anyasecret.utils.ConfigDef;
import io.javaside.anyasecret.utils.MiscUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static io.javaside.anyasecret.utils.MiscUtils.base64Decode;
import static io.javaside.anyasecret.utils.MiscUtils.stringValueFrom;

abstract class AnyaSecretFinder implements SecretFinder {

    private static final Logger logger = LoggerFactory.getLogger(AnyaSecretFinder.class);

    private static final ConfigDef VULNERABLE_URL_DEF = ConfigDef.of("vulnerable.url", true);
    private static final ConfigDef SECRET_DECODING_ALGO_DEF = ConfigDef.of("secret.decoding.algo", "PLAIN");
    private static final ConfigDef ROOT_BACKTRACK_PATH_DEF = ConfigDef.of("root.backtrack.path", true);
    private static final ConfigDef POTENTIAL_SECRET_DIRS = ConfigDef.of("potential.secret.dirs", true);
    private static final ConfigDef FILE_PARAM_KEY_DEF = ConfigDef.of("file.param.key", true);
    private static final ConfigDef SECRET_FILE_NAME_DEF = ConfigDef.of("secret.file.name", true);

    private String vulnerableUrl;

    private EncryptionAlgo secretDecodingAlgo;

    private String rootBacktrackPath;

    private List<String> potentialSecretDirs;

    private String fileParamKey;

    private String secretFileName;

    private SecretExtractor extractor;

    private SecretValidator validator;


    @Override
    public void configure(Properties props) {
        this.vulnerableUrl = stringValueFrom(props, VULNERABLE_URL_DEF);
        this.secretDecodingAlgo = encryptionAlgoFrom(props);
        this.rootBacktrackPath = stringValueFrom(props, ROOT_BACKTRACK_PATH_DEF);
        this.potentialSecretDirs = potentialSecretDirsFrom(props);
        this.fileParamKey = stringValueFrom(props, FILE_PARAM_KEY_DEF);
        this.secretFileName = stringValueFrom(props, SECRET_FILE_NAME_DEF);
    }

    private EncryptionAlgo encryptionAlgoFrom(Properties props) {
        var algoName = stringValueFrom(props, SECRET_DECODING_ALGO_DEF);
        try {
            return EncryptionAlgo.valueOf(algoName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AnyaSecretException("Unknown encryption algorithms " + algoName);
        }
    }

    private List<String> potentialSecretDirsFrom(Properties props) {
        return Arrays.stream(stringValueFrom(props, POTENTIAL_SECRET_DIRS).split(","))
                .map(MiscUtils::normalizeDirPath)
                .collect(Collectors.toList());
    }

    @Override
    public void setExtractor(SecretExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void setValidator(SecretValidator validator) {
        this.validator = validator;
    }

    protected SecretExtractor getExtractor() {
        return extractor;
    }

    protected SecretValidator getValidator() {
        return validator;
    }

    protected String getVulnerableUrl() {
        return vulnerableUrl;
    }

    protected EncryptionAlgo getSecretDecodingAlgo() {
        return secretDecodingAlgo;
    }

    protected String getRootBacktrackPath() {
        return rootBacktrackPath;
    }

    protected List<String> getPotentialSecretDirs() {
        return potentialSecretDirs;
    }

    protected String getFileParamKey() {
        return fileParamKey;
    }

    protected String getSecretFileName() {
        return secretFileName;
    }

    protected String decodeSecret(String secret) {
        String result;
        switch (secretDecodingAlgo) {
            case PLAIN:
                result = secret;
                break;
            case BASE64:
                result = base64Decode(secret);
                break;
            default:
                throw new AnyaSecretException("Cannot decode secret as decoding algo is not set");
        }
        return result;
    }
}

































