package utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.example.web.dto.UserDto;
import java.util.Random;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

public class EntityUtils {
  private static final Random RANDOM = new Random();

  public static final String PHOTO_ID = "00000000-0000-0000-0000-000000000000";
  private static final String[] COUNTRIES = {"USA", "Canada", "UK", "Germany", "France", "India"};

  private static final String[] NAMES = {"John", "Tom", "Pera", "Steve", "Vladimir", "Peter"};

  public static String randomUsername() {
    return NAMES[RANDOM.nextInt(NAMES.length)] + RANDOM.nextInt(1000000);
  }

  public static int randomAge() {
    return RANDOM.nextInt(70) + 18;
  }

  public static String randomCountry() {
    return COUNTRIES[RANDOM.nextInt(COUNTRIES.length)];
  }

  public static String randomEmail() {
    return "user" + RANDOM.nextInt(1000) + "@example.com";
  }

  public static String randomPassword() { return "password" +  RANDOM.nextInt(1000); };

  public static UserDto generateValidUserDto() {
    return UserDto.builder()
        .username(randomUsername())
        .age( randomAge())
        .country(randomCountry())
        .email(randomEmail())
        .password(randomPassword())
        .photoId(PHOTO_ID)
        .build();
  }

  public static MockMultipartHttpServletRequestBuilder buildValidUserDtoMultipartRequest(String url) {
    return buildUserDtoMultipartRequest(url, generateValidUserDto())
        .file(new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "fake-image-content".getBytes()));
  }

  public static MockMultipartHttpServletRequestBuilder buildUserDtoMultipartRequest(String url, UserDto userDto) {
    MockMultipartHttpServletRequestBuilder builder = multipart(url);

    addParamIfNotNull(builder, "id", userDto.id());
    builder.param("username", userDto.username())
        .param("password", userDto.password())
        .param("age", String.valueOf(userDto.age()))
        .param("country", userDto.country())
        .param("email", userDto.email());
    addParamIfNotNull(builder, "photoId", userDto.photoId());

    return builder;
  }

  private static void addParamIfNotNull(MockMultipartHttpServletRequestBuilder builder, String key, String value) {
    if (value != null) {
      builder.param(key, value);
    }
  }

}