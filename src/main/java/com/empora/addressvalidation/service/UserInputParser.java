package com.empora.addressvalidation.service;

import com.empora.addressvalidation.domain.Address;
import java.io.*;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

class UnparseableInputException extends RuntimeException {

  public UnparseableInputException(String inputString) {
    super(String.format("Unparseable string in input file: %s", inputString));
  }
}

@Service
public class UserInputParser {
  private final InputStream systemIn = System.in;
  private ByteArrayInputStream testIn;

  public List<Address> parseUserProvidedInput(Stream<String> inputStream) {

    return inputStream
        .filter(userInput -> !userInput.isBlank() && !userInput.equalsIgnoreCase("Street, City, Zip Code"))
        .map(UserInputParser::parseAddress)
        .toList();
  }

  private static Address parseAddress(String str) {
    String[] values = str.split(",");
    if (values.length == 3) {
      return Address.builder().street(values[0]).city(values[1]).zipcode(values[2]).build();
    } else {
      throw new UnparseableInputException(str);
    }
  }
}
