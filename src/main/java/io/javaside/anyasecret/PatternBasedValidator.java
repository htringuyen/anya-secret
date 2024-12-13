package io.javaside.anyasecret;

import io.javaside.anyasecret.utils.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Pattern;

import static io.javaside.anyasecret.utils.MiscUtils.stringValueFrom;

class PatternBasedValidator implements SecretValidator {

    private static final Logger logger = LoggerFactory.getLogger(PatternBasedValidator.class);

    private static final ConfigDef VALIDATION_PATTERN = ConfigDef.of("validation.regex", true);

    private Pattern pattern;

    @Override
    public void configure(Properties props) {
        var regex = stringValueFrom(props, VALIDATION_PATTERN);
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean validate(String input) {
        var isValid = pattern.matcher(input).matches();
        logger.debug("Validation {} for input: {}", isValid, input);
        return isValid;
    }
}




























