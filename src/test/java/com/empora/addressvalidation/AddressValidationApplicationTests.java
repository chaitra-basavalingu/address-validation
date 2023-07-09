package com.empora.addressvalidation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AddressValidationApplicationTests {

  @Test
  public void contextLoads() {}

  private PrintStream originalSystemOut;
  private ByteArrayOutputStream systemOutContent;

  private PrintStream originalSystemErr;
  private ByteArrayOutputStream systemErrContent;

  @BeforeEach
  void redirectSystemOutStream() {
    originalSystemOut = System.out;
    originalSystemErr = System.err;

    systemOutContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(systemOutContent));

    systemErrContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(systemErrContent));
  }

  @AfterEach
  void restoreSystemOutStream() {
    System.setOut(originalSystemOut);
    System.setErr(originalSystemErr);
  }

  @Test
  @DisplayName(
      "App receives filePath for input and it calls InputStreamProcessor to process the file")
  public void testGoodSampleFile() throws URISyntaxException {
    Path path =
        Paths.get(
            Objects.requireNonNull(getClass().getClassLoader().getResource("input.csv")).toURI());

    AddressValidationApplication.main(new String[] {path.toAbsolutePath().toString()});

    assertThat(systemOutContent.toString().trim())
        .isEqualTo(
            "143 e Maine Street,  Columbus,  43215 -> 143 E Main St, Columbus, 43215-5370\n"
                + "1 Empora St,  Title,  11111 -> Invalid Address\n" +
                    "3608 Sparrow Ct,  Hilliard,  43026 -> 3608  Sparrow Ct, Hilliard, 43026-2581");
  }

  @Test
  @DisplayName("App throws Exception when file is not found")
  public void testException() {
    var exception =
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> AddressValidationApplication.main(new String[] {"some-junk-file-path"}));

    exception.printStackTrace(new PrintStream(systemErrContent));

    assertThat(systemErrContent.toString())
        .contains("java.nio.file.NoSuchFileException: some-junk-file-path");
  }
}
