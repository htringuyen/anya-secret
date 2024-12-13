package io.javaside.anyasecret;

import io.javaside.anyasecret.utils.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static io.javaside.anyasecret.utils.MiscUtils.consumeAsString;
import static io.javaside.anyasecret.utils.MiscUtils.stringValueFrom;

class PatternBasedExtractor implements SecretExtractor {

    private static final Logger logger = LoggerFactory.getLogger(PatternBasedExtractor.class);

    private static final ConfigDef EXTRACT_PATTERN = ConfigDef.of("extract.regex", true);

    private Pattern pattern;

    @Override
    public void configure(Properties props) {
        var regex = stringValueFrom(props, EXTRACT_PATTERN);
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public List<String> extractFrom(InputStream inStream) {
        return extractFrom(consumeAsString(inStream));
    }

    @Override
    public List<String> extractFrom(String s) {
        //logger.debug("Extracting secrets from input: {}", s);
        var result = new LinkedList<String>();
        var matcher = pattern.matcher(s);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                result.add(matcher.group(i));
            }
        }
        return result;
    }
}






























