# Address Validation 

This is an implementation of the code exercise described [here](CodeExerciseDescription.md)

It is written in Java, using [Spring Boot](https://spring.io/projects/spring-boot) for DI and as the runner.
This is not an optimal choice for a command line tool, but allows for easy extension should this transform into a web based service.

## Directory Tour
Once again working under the assumption that the reviewer might not be familiar with the Java ecosystem, a brief guide of what's in this repo.

### Gradle stuff
* `build.gradle`: the main build config file.  Adds build plugins, defines dependencies and where to pull them from.
* `settings.gradle`: project level gradle config
* `gradlew<.bat>`: bootstrap script that downloads/runs gradle without requiring a local install
* `gradle/`: gradle bootstrapping dependencies
* `build/`: gradle build directory. The final generated artifact is placed in `build/lib/

### Other stuff
* `README.me`: That's this file. It's for reading
* `src/test/java`: Java test source root. All the tests live here. `AddressValidationApplicationTest` is an autogenerated integration-ish test.
* `src/main/resources`: Contains a sample file and Spring Boot config. Currently configured to make the app less chatty.
* `src/main/java`: Main source root. This is where the main app source lives.


## Building
In order to be able to build this application you need to install JDK17. I suggest installing [sdkman](https://sdkman.io/install) and using that to install the required JDK 17 version by running:
```
sdk env install
```

build the application by running (this runs all tests as well):
```
./gradlew build
```
To run just tests:
```
./gradlew test
```

run the application:
```
java run:-jar  ./build/libs/address-validation-0.0.1-SNAPSHOT.jar
```
or
```
java -jar ./build/libs/address-validation-0.0.1-SNAPSHOT.jar <input-file>
```

## Design
The main entry point lives in `AddressValidationApplication.java`.
A top level `main` function kicks off the Spring boot machinery, which executes the `CommandLineRunner` defined in `AddressValidationApplication`.

A bit of high level glue code in the `run` method looks for a command line argument, and calls the UserInputParser which is responsible for reading the contents of the file using the  
file name provided. Once all user provided addresses have been parsed, the AddressValidationService is used to validate all addresses. This results in a map of the provided addresses and the result of the validation.
Lastly, a simple method parses over the results and prints the user provided addresses and the validated addresses.

1. The AddressValidationApplication receives the input data file.
2. App converts the data file to a stream of Strings and sends it to the UserInputParser
3. UserInputParser iterates through the stream and converts non-blank lines into the Address POJO.
4. User provided addresses are then validated by the AddressValidator.
5. Results are then printed by a function defined in the App. (If more complex post-processing needs to be performed on the results this could be extracted into its own service(s))


## Assumptions
1. Fail fast: as there was little guidance on how to manage invalid/surprising input, I opted to fail immediately upon invalid input.
This includes:
* unparseable input lines 
* any unexpected results returned by the Smarty API. (for eg, if any error occurs while calling the API I chose to fail immediately)
2. The Smarty API accepts multiple addresses in the request but I chose to send a single address in each request to simulate a use case where a user's address needs to be verified instantly even though the input is a file which contains a batch of addresses. In a real world scenario, we would optimize based on the type of user experience we need to support, the cost of each request to the Smarty API, etc.
3. For the sake of simplicity I haven't implemented a database or a store to lookup previously encountered addresses and results before performing a request to the Smarty API. 

## Features
* Atomic components which can be independently scaled.
* Used functional programming principles to be stateless and immutable so as not to have side-effects. 
