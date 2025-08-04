package utils;

import static com.example.web.utils.Constants.getRandomNumber;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.example.briscula.user.player.Bot;
import com.example.briscula.user.player.Player;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.user.player.RoomPlayerId;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.user.UserDto;
import com.example.web.model.ConnectedPlayer;
import java.util.List;
import java.util.Random;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.socket.WebSocketSession;

public class EntityUtils {
  private static final Random RANDOM = new Random();

  private static int counter = 0;

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
    return "user" + RANDOM.nextInt(100000) + "@example.com";
  }

  public static String randomPassword() { return "password" +  RANDOM.nextInt(100000); };

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

  public static UserDto generateValidUserDtoWithoutPhoto() {
    return UserDto.builder()
        .username(randomUsername())
        .age( randomAge())
        .country(randomCountry())
        .email(randomEmail())
        .password(randomPassword())
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

  public static RoomPlayerId getRoomPlayerId() {
    return new RoomPlayerId("roomId" + RANDOM.nextInt(500), RANDOM.nextInt(500));
  }

  public static String getUserId() {
    return "UserId " + RANDOM.nextInt(100000);
  }

  public static String getPlayerId() {
    return String.valueOf(RANDOM.nextInt(100000));
  }

  public static String getTournamentId() {
    return String.valueOf(RANDOM.nextInt(100000));
  }


  public static String getPlayerName() {
    return "Player + " + RANDOM.nextInt(100000);
  }

  public static RealPlayer getRealPlayer() {
    return new RealPlayer(getRoomPlayerId(), List.of(),
        "Nickname" + RANDOM.nextInt(500), getWebSocketSession()
    );
  }

  public static RealPlayer getRealPlayer(WebSocketSession webSocketSession) {
    return new RealPlayer(getRoomPlayerId(), List.of(),
        "Nickname" + RANDOM.nextInt(500), webSocketSession
    );
  }

  //  Connected Player

  public static ConnectedPlayer getConnectedPlayer() {
    WebSocketSession webSocketSession = getWebSocketSession();
    return new ConnectedPlayer(
        webSocketSession, getRealPlayer(webSocketSession), true
    );
  }

  public static ConnectedPlayer getConnectedPlayer(Player player) {
    return new ConnectedPlayer(
       getWebSocketSession(), player, true
    );
  }

  public static ConnectedPlayer getConnectedPlayer(Bot bot) {
    return new ConnectedPlayer(bot);
  }

  public static ConnectedPlayer getConnectedPlayer(String userId) {
    return ConnectedPlayer.builder()
        .webSocketSession(getWebSocketSession())
        .player(getRealPlayer())
        .userId(userId)
        .build();
  }

  public static ConnectedPlayer getConnectedPlayersBots() {
    return new ConnectedPlayer(getWebSocketSession(), new Bot(null, "Bot " + getRandomNumber(100000)),
        true);
  }

  // WebSocketSession

  public static WebSocketSession getWebSocketSession() {
    return new SimpleWebSocketSession("webSocketSessionId" + RANDOM.nextInt(10000));
  }

  // Tournament

  public static TournamentCreateDto createTournamentCreateDto() {
    return TournamentCreateDto.builder()
        .name("Tournament." + counter++)
        .numberOfPlayers(4)
        .roundsToWin(1)
        .build();
  }

  public static String getTournamentName() {
    return "Tournament Name " + RANDOM.nextInt(1000000);
  }
}