package io.javaside.anyasecret;

import io.javaside.anyasecret.exception.AnyaSecretException;
import io.javaside.anyasecret.utils.ConfigDef;
import io.javaside.anyasecret.utils.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static io.javaside.anyasecret.utils.MiscUtils.stringValueFrom;

class AnyaSecretApplication {

    private static final Logger logger = LoggerFactory.getLogger(AnyaSecretApplication.class);

    private static final String CONFIGURATION_PATH = "config/app.properties";

    private static final ConfigDef PLUGGABLE_TYPE = ConfigDef.of("type", true);
    private static final ConfigDef PLUGGABLE_NAME = ConfigDef.of("name", true);

    private static final String PLUGGABLE_SELECTION_PREFIX = "use";

    private static final String FINDER_PREFIX = "finder";
    private static final String CONSUMER_PREFIX = "consumer";
    private static final String VALIDATOR_PREFIX = "validator";
    private static final String EXTRACTOR_PREFIX = "extractor";

    private final Configuration configuration;

    private final SecretFinder finder;
    private final SecretExtractor extractor;
    private final SecretValidator validator;
    private final SecretConsumer consumer;

    AnyaSecretApplication() {
        this.configuration = loadConfiguration();
        this.extractor = createAndConfigExtractor();
        this.validator = createAndConfigValidator();
        this.finder = createAndConfigFinder();
        this.consumer = createAndConfigConsumer();
    }

    public void run() {
        logConfiguration();
        var secrets = finder.findSecrets();
        if (secrets.isEmpty()) {
            throw new AnyaSecretException("No secrets found");
        }
        logFoundSecrets(secrets);
        consumer.consume(secrets);
    }

    private Configuration loadConfiguration() {
        try {
            return Configuration.load(CONFIGURATION_PATH);
        }
        catch (IOException e) {
            throw new AnyaSecretException("Failed to load configurations");
        }
    }

    private void logConfiguration() {
        var props = configuration.asProperties();
        logger.info("Loaded configuration:");
        props.forEach((key, value) -> logger.info("{}: {}", key, value));
    }

    private void logFoundSecrets(Map<String, String> secrets) {
        logger.info("Found secrets:");
        secrets.forEach((key, value) -> logger.info("{}: {}", key, value));
    }

    private SecretFinder createAndConfigFinder() {
        var props = configuration.subset(concat(PLUGGABLE_SELECTION_PREFIX, FINDER_PREFIX), true).asProperties();
        var finderType = stringValueFrom(props, PLUGGABLE_TYPE);
        var finderName = stringValueFrom(props, PLUGGABLE_NAME);

        var finder = loadObject(finderType, SecretFinder.class);
        finder.setExtractor(extractor);
        finder.setValidator(validator);

        var finderProps = configuration.subset(concat(FINDER_PREFIX, finderName), true).asProperties();
        finder.configure(finderProps);
        return finder;
    }

    private SecretExtractor createAndConfigExtractor() {
        var props = configuration.subset(concat(PLUGGABLE_SELECTION_PREFIX, EXTRACTOR_PREFIX), true).asProperties();
        var extractorType = stringValueFrom(props, PLUGGABLE_TYPE);
        var extractorName = stringValueFrom(props, PLUGGABLE_NAME);

        var extractor = loadObject(extractorType, SecretExtractor.class);
        var extractorProps = configuration.subset(concat(EXTRACTOR_PREFIX, extractorName), true).asProperties();
        extractor.configure(extractorProps);
        return extractor;
    }

    private SecretValidator createAndConfigValidator() {
        var props = configuration.subset(concat(PLUGGABLE_SELECTION_PREFIX, VALIDATOR_PREFIX), true).asProperties();
        var validatorType = stringValueFrom(props, PLUGGABLE_TYPE);
        var validatorName = stringValueFrom(props, PLUGGABLE_NAME);

        var validator = loadObject(validatorType, SecretValidator.class);
        var validatorProps = configuration.subset(concat(VALIDATOR_PREFIX, validatorName), true).asProperties();
        validator.configure(validatorProps);
        return validator;
    }

    private SecretConsumer createAndConfigConsumer() {
        var props = configuration.subset(concat(PLUGGABLE_SELECTION_PREFIX, CONSUMER_PREFIX), true).asProperties();
        var consumerType = stringValueFrom(props, PLUGGABLE_TYPE);
        var consumerName = stringValueFrom(props, PLUGGABLE_NAME);

        var consumer = loadObject(consumerType, SecretConsumer.class);
        var consumerProps = configuration.subset(concat(CONSUMER_PREFIX, consumerName), true).asProperties();
        consumer.configure(consumerProps);
        return consumer;
    }

    private String concat(String first, String second) {
        return first + "." + second;
    }

    public static <T> T loadObject(String className, Class<T> type) {
        try {
            return type.cast(Class.forName(className).getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new AnyaSecretException("Failed to load object of class " + className, e);
        }
    }
}












