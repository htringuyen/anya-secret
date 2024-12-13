package io.javaside.anyasecret;

import org.junit.jupiter.api.Test;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternBasedExtractorTest {

    @Test
    void samplePattern() {
        // Sample HTML content
        var sampleSecret = "aBcDeFgHi==123456789";

        var html = "<img class=\"styled-image\" src=\"data:image/png;base64," + sampleSecret + "\"/>";

        var props = new Properties();
        props.put("extract.regex", "data:image\\/[a-zA-Z]+;base64,([a-zA-Z0-9+/=]+)");

        var extractor = new PatternBasedExtractor();
        extractor.configure(props);
        
        var result = extractor.extractFrom(html);
        
        assertEquals(1, result.size());
        assertEquals(sampleSecret, result.getFirst());
    }
}
