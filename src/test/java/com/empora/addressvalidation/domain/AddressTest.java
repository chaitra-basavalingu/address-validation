package com.empora.addressvalidation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressTest {
  @Test
  @DisplayName("toString formats the address as expected")
  public void toStringTest() {
    Address address = new Address("123 Main St", "Columbus", "43021");
    assertThat(address.toString()).isEqualTo("123 Main St, Columbus, 43021");
  }
}
