package com.empora.addressvalidation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationResponseTest {
  @Test
  @DisplayName("when Result is AddressValidationResponse prints the validated address")
  public void printsAddressValidationResponse() {
    ValidationResult validationResult =
        new AddressValidationResponse(
            "123", "NE", "Roosevelt", "Wy", "Columbus", "OH", null, "43021", "1234");
    assertThat(validationResult.print()).isEqualTo("123 NE Roosevelt Wy, Columbus, 43021-1234");
  }

  @Test
  @DisplayName("when Result is InvalidAddress prints Invalid Address")
  public void printsInvalidAddress() {
    ValidationResult validationResult = new InvalidAddress();
    assertThat(validationResult.print()).isEqualTo("Invalid Address");
  }
}
