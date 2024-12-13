package io.javaside.anyasecret;

import io.javaside.anyasecret.utils.Configuration;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class JseUrlBasedFinderTest {

    private static Properties getProperties() {
        var props = new Properties();

        props.put("finder.anya.vulnerable.url", "https://hello-anya.fly.dev/");
        props.put("finder.anya.secret.decoding.algo", "BASE64");
        props.put("finder.anya.root.backtrack.path", "....//");
        props.put("finder.anya.potential.secret.dirs", "db, app, app/sus, config/hmm, anya/very_secured_secret, bin, config, storage, test, vendor");
        props.put("finder.anya.file.param.key", "img");
        props.put("finder.anya.secret.file.name", "secret_file");

        props.put("extractor.pbx.extract.regex", "data:image\\/[a-zA-Z]+;base64,([a-zA-Z0-9+/=]+)");

        props.put("validator.pbv.validation.regex", "19b.*");

        return props;
    }

    @Test
    void testFindSecrets() {
        var props = getProperties();

        var configuration = Configuration.from(props);
        
        var extractor = new PatternBasedExtractor();
        extractor.configure(configuration.subset("extractor.pbx", true).asProperties());
        
        var validator = new PatternBasedValidator();
        validator.configure(configuration.subset("validator.pbv", true).asProperties());

        var finder = new JseUrlBasedFinder();
        finder.setExtractor(extractor);
        finder.setValidator(validator);
        finder.configure(configuration.subset("finder.anya", true).asProperties());
        
        var secret = finder.findSecrets();
        assertFalse(secret.isEmpty());
    }
}



























