package com.empora.addressvalidation;

import com.empora.addressvalidation.domain.Address;
import com.empora.addressvalidation.domain.ValidationResult;
import com.empora.addressvalidation.service.AddressValidationService;
import com.empora.addressvalidation.service.UserInputParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AddressValidationApplication implements CommandLineRunner {

  private final AddressValidationService addressValidationService;
  private final UserInputParser userInputParser;

  public AddressValidationApplication(
      AddressValidationService addressValidationService, UserInputParser userInputParser) {
    this.addressValidationService = addressValidationService;
    this.userInputParser = userInputParser;
  }

  public static void main(String[] args) {
    SpringApplication.run(AddressValidationApplication.class, args);
  }

  @Override
  public void run(String... args) throws IOException {
    if (args.length == 1) {
      try (Stream<String> stream = Files.lines(Paths.get(args[0]))) {
        List<Address> userProvidedAddresses = userInputParser.parseUserProvidedInput(stream);

        Map<Address, ValidationResult> addressValidationResultMap =
            addressValidationService.validateAddresses(userProvidedAddresses);

        printAddressValidationResults(addressValidationResultMap);
      }
    }
  }

  private static void printAddressValidationResults(
      Map<Address, ValidationResult> addressValidationResultMap) {
    addressValidationResultMap.forEach(
        (address, validationResult) ->
            System.out.printf("%s -> %s%n", address, validationResult.print()));
  }
}
