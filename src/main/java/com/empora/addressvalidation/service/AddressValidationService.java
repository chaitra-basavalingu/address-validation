package com.empora.addressvalidation.service;

import com.empora.addressvalidation.domain.Address;
import com.empora.addressvalidation.domain.AddressValidationResponse;
import com.empora.addressvalidation.domain.InvalidAddress;
import com.empora.addressvalidation.domain.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

class SmartyAPIException extends RuntimeException {
  public SmartyAPIException(String statusCode) {
    super(
        String.format(
            "Error occurred while validating user provided addresses. API returned %s status code",
            statusCode));
  }
}

@Service
public class AddressValidationService {

  private final RestTemplate restTemplate;

  @Value("${smarty.api.url}")
  private String restApiUrl;

  @Value("${smarty.api.auth-id}")
  private String authId;

  @Value("${smarty.api.auth-token}")
  private String authToken;

  public AddressValidationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @SneakyThrows
  public ValidationResult validateAddress(Address address) {
    var responseBody = submitAddressForValidation(address);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode result = objectMapper.readTree(responseBody).get(0);
    if (result == null) {
      return new InvalidAddress();
    } else {
      JsonNode components = result.path("components");
      return AddressValidationResponse.builder()
          .primaryNumber(getFieldValue(components, "primary_number"))
          .streetPredirection(getFieldValue(components, "street_predirection"))
          .streetName(getFieldValue(components, "street_name"))
          .streetSuffix(getFieldValue(components, "street_suffix"))
          .cityName(getFieldValue(components, "city_name"))
          .defaultCityName(getFieldValue(components, "default_city_name"))
          .stateAbbreviation(getFieldValue(components, "state_abbreviation"))
          .zipcode(getFieldValue(components, "zipcode"))
          .plus4Code(getFieldValue(components, "plus4_code"))
          .build();
    }
  }

  private static String getFieldValue(JsonNode components, String fieldName) {
    return components.hasNonNull(fieldName) ? components.get(fieldName).asText() : "";
  }

  private String submitAddressForValidation(Address address) {
    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("auth-id", authId);
    uriParams.put("auth-token", authToken);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<List<Address>> request =
        new HttpEntity<>(Collections.singletonList(address), headers);

    try {
      ResponseEntity<String> response =
          restTemplate.postForEntity(restApiUrl, request, String.class, uriParams);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new SmartyAPIException(response.getStatusCode().toString());
      }
      return response.getBody();
    } catch (HttpStatusCodeException e) {
      throw new SmartyAPIException(e.getStatusCode().toString());
    }
  }

  public Map<Address, ValidationResult> validateAddresses(List<Address> userProvidedAddresses) {
    Map<Address, ValidationResult> addressValidationResultMap = new LinkedHashMap<>();
    userProvidedAddresses.forEach(
        address -> addressValidationResultMap.put(address, validateAddress(address)));
    return addressValidationResultMap;
  }
}
