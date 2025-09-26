package utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class Constants {

    private static int imageNumber = 1;

    public static MockMultipartFile getMultipartFileImage() throws IOException {
        return getMultipartFileImage("sample.jpg");
    }

    public static MockMultipartFile getMultipartFileImage(String name) throws IOException {
        ClassPathResource imageResource = new ClassPathResource(name);
        InputStream imageStream = imageResource.getInputStream();
        return new MockMultipartFile(
                "photo", String.format("%s%d.jpg", name, ++imageNumber), MediaType.IMAGE_JPEG_VALUE, imageStream);
    }

}
