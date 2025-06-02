package utils;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class Constants {

  private static int imageNumber = 1;

  public static MockMultipartFile getMultipartFileImage() throws IOException {
    ClassPathResource imageResource = new ClassPathResource("sample.jpg");
    InputStream imageStream = imageResource.getInputStream();
    return new MockMultipartFile(
        "photo", String.format("sample%d.jpg", ++imageNumber), MediaType.IMAGE_JPEG_VALUE, imageStream);
  }

}
