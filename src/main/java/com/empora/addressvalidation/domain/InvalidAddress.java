package com.empora.addressvalidation.domain;

public class InvalidAddress implements ValidationResult {
  @Override
  public String print() {
    return "Invalid Address";
  }
}
