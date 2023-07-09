package com.empora.addressvalidation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.empora.addressvalidation.domain.Address;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserInputParserTest {

  private final UserInputParser userInputParser = new UserInputParser();

  @Test
  @DisplayName("Parses valid csv input stream and creates Address list")
  public void parsesCsvFileAndCreatesDrivers() {
    Stream<String> inputStream =
        Stream.of(
            "Street, City, Zip Code",
            "143 e Maine Street, Columbus, 43215",
            "1 Empora St, Title, 11111");
    List<Address> addresses = userInputParser.parseUserProvidedInput(inputStream);

    assertThat(addresses).isNotNull();
    assertThat(addresses.size()).isEqualTo(2);
    assertThat(addresses.get(0).getCity()).isEqualTo(" Columbus");
    assertThat(addresses.get(0).getStreet()).isEqualTo("143 e Maine Street");
    assertThat(addresses.get(0).getZipcode()).isEqualTo(" 43215");
    assertThat(addresses.get(1).getCity()).isEqualTo(" Title");
    assertThat(addresses.get(1).getStreet()).isEqualTo("1 Empora St");
    assertThat(addresses.get(1).getZipcode()).isEqualTo(" 11111");
  }

  @Test
  @DisplayName("throws Exception when there is an invalid string in the provided input")
  public void parsesCommandLineInputAndCreatesDrivers() {
    Stream<String> inputStream =
        Stream.of("Street, City, Zip Code", "143 e Maine Street, Columbus");
    Throwable exception =
        assertThrows(
            UnparseableInputException.class,
            () -> userInputParser.parseUserProvidedInput(inputStream));
    assertThat("Unparseable string in input file: 143 e Maine Street, Columbus")
        .isEqualTo(exception.getMessage());
  }
}
