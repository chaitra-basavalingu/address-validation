package com.empora.addressvalidation.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AddressValidationResponse implements ValidationResult {

  private String primaryNumber;
  private String streetPredirection;
  private String streetName;
  private String streetSuffix;
  private String cityName;
  private String defaultCityName;
  private String stateAbbreviation;
  private String zipcode;
  private String plus4Code;

  public String fullStreetName() {
    return String.format(
        "%s %s %s %s",
        this.primaryNumber, this.streetPredirection, this.streetName, this.streetSuffix);
  }

  public String longZipcode() {
    return String.format("%s-%s", this.zipcode, this.plus4Code);
  }

  @Override
  public String print() {
    return String.format("%s, %s, %s", this.fullStreetName(), this.cityName, this.longZipcode());
  }
}
