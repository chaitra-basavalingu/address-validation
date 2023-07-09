package com.empora.addressvalidation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.empora.addressvalidation.domain.Address;
import com.empora.addressvalidation.domain.AddressValidationResponse;
import com.empora.addressvalidation.domain.InvalidAddress;
import com.empora.addressvalidation.domain.ValidationResult;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class AddressValidationServiceTest {

  @Autowired private RestTemplate restTemplate;

  @Autowired private AddressValidationService addressValidationService;

  private MockRestServiceServer mockRestServiceServer;

  @BeforeEach
  public void setUp() {
    mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  @DisplayName("when Smarty Address Validation API returns 500 status code throws Exception")
  public void addressValidationApiFailure() throws URISyntaxException {
    mockRestServiceServer
        .expect(
            ExpectedCount.once(),
            requestTo(
                new URI(
                    "https://us-street.api.smartystreets.com/street-address?auth-id=3ca4ff57-5a2f-f401-c7e1-09313cad6a38&auth-token=fiY2eBHm7RgeSWmA5p6l")))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body("error occurred"));
    Throwable exception =
        assertThrows(
            SmartyAPIException.class,
            () ->
                addressValidationService.validateAddresses(
                    List.of(new Address("123 Main St", "Columbus", "43081"))));
    assertThat(exception.getMessage())
        .isEqualTo(
            "Error occurred while validating user provided addresses. API returned 500 INTERNAL_SERVER_ERROR status code");
  }

  @Test
  @DisplayName("when Smarty address validation API finds no matches returns Invalid Address result")
  public void addressValidationReturnsNoMatches() throws URISyntaxException {
    mockRestServiceServer
        .expect(
            ExpectedCount.once(),
            requestTo(
                new URI(
                    "https://us-street.api.smartystreets.com/street-address?auth-id=3ca4ff57-5a2f-f401-c7e1-09313cad6a38&auth-token=fiY2eBHm7RgeSWmA5p6l")))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("[]"));
    Address inputAddress = new Address("123 Main St", "Columbus", "43081");
    Map<Address, ValidationResult> result =
        addressValidationService.validateAddresses(List.of(inputAddress));
    assertThat(result.get(inputAddress)).isInstanceOf(InvalidAddress.class);
  }

  @Test
  @DisplayName(
      "when Smarty address validation API finds a valid address returns the Validated Address")
  public void addressValidationReturnsAMatch() throws URISyntaxException {
    mockRestServiceServer
        .expect(
            ExpectedCount.once(),
            requestTo(
                new URI(
                    "https://us-street.api.smartystreets.com/street-address?auth-id=3ca4ff57-5a2f-f401-c7e1-09313cad6a38&auth-token=fiY2eBHm7RgeSWmA5p6l")))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    "[\n"
                        + "\t{\n"
                        + "\t\t\"input_index\": 0,\n"
                        + "\t\t\"candidate_index\": 0,\n"
                        + "\t\t\"delivery_line_1\": \"1 Santa Claus Ln\",\n"
                        + "\t\t\"last_line\": \"North Pole AK 99705-9901\",\n"
                        + "\t\t\"delivery_point_barcode\": \"997059901010\",\n"
                        + "\t\t\"smarty_key\": \"1144020281\",\n"
                        + "\t\t\"components\": {\n"
                        + "\t\t\t\"primary_number\": \"1\",\n"
                        + "\t\t\t\"street_name\": \"Santa Claus\",\n"
                        + "\t\t\t\"street_predirection\": \"Santa Claus\",\n"
                        + "\t\t\t\"street_suffix\": \"Ln\",\n"
                        + "\t\t\t\"city_name\": \"North Pole\",\n"
                        + "\t\t\t\"default_city_name\": \"North Pole\",\n"
                        + "\t\t\t\"state_abbreviation\": \"AK\",\n"
                        + "\t\t\t\"zipcode\": \"99705\",\n"
                        + "\t\t\t\"plus4_code\": \"9901\",\n"
                        + "\t\t\t\"delivery_point\": \"01\",\n"
                        + "\t\t\t\"delivery_point_check_digit\": \"0\"\n"
                        + "\t\t},\n"
                        + "\t\t\"metadata\": {\n"
                        + "\t\t\t\"record_type\": \"S\",\n"
                        + "\t\t\t\"zip_type\": \"Standard\",\n"
                        + "\t\t\t\"county_fips\": \"02090\",\n"
                        + "\t\t\t\"county_name\": \"Fairbanks North Star\",\n"
                        + "\t\t\t\"carrier_route\": \"C004\",\n"
                        + "\t\t\t\"congressional_district\": \"AL\",\n"
                        + "\t\t\t\"rdi\": \"Commercial\",\n"
                        + "\t\t\t\"elot_sequence\": \"0001\",\n"
                        + "\t\t\t\"elot_sort\": \"A\",\n"
                        + "\t\t\t\"latitude\": 64.75233,\n"
                        + "\t\t\t\"longitude\": -147.35297,\n"
                        + "\t\t\t\"coordinate_license\": 1,\n"
                        + "\t\t\t\"precision\": \"Rooftop\",\n"
                        + "\t\t\t\"time_zone\": \"Alaska\",\n"
                        + "\t\t\t\"utc_offset\": -9,\n"
                        + "\t\t\t\"dst\": true\n"
                        + "\t\t},\n"
                        + "\t\t\"analysis\": {\n"
                        + "\t\t\t\"dpv_match_code\": \"Y\",\n"
                        + "\t\t\t\"dpv_footnotes\": \"AABB\",\n"
                        + "\t\t\t\"dpv_cmra\": \"N\",\n"
                        + "\t\t\t\"dpv_vacant\": \"N\",\n"
                        + "\t\t\t\"dpv_no_stat\": \"Y\",\n"
                        + "\t\t\t\"active\": \"Y\",\n"
                        + "\t\t\t\"footnotes\": \"L#\"\n"
                        + "\t\t}\n"
                        + "\t}]"));
    Address inputAddress = new Address("123 Main St", "Columbus", "43081");
    Map<Address, ValidationResult> result =
        addressValidationService.validateAddresses(List.of(inputAddress));
    assertThat(result.get(inputAddress)).isInstanceOf(AddressValidationResponse.class);
    AddressValidationResponse response = (AddressValidationResponse) result.get(inputAddress);
    assertThat(response.print()).isEqualTo("1 Santa Claus Santa Claus Ln, North Pole, 99705-9901");
  }
}
