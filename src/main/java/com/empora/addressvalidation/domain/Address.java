package com.empora.addressvalidation.domain;

import java.io.Serializable;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Address implements Serializable {
  private String street;
  private String city;
  private String zipcode;

  public String toString() {
    return String.format("%s, %s, %s", this.street, this.city, this.zipcode);
  }
}
