package utils;

import com.example.web.dto.UserDto;
import com.example.web.utils.JsonUtils;
import java.util.Random;

public class EntityUtils {
  private static final Random RANDOM = new Random();
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
        .build();
  }

  public static String generateValidUserDtoInJson() {
    return JsonUtils.toJson(generateValidUserDto());
  }
}