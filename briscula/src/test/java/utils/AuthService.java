package utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class AuthService {

  private final MockMvc mockMvc;

  @Autowired
  public AuthService(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  public String getAuthToken(String username, String password) throws Exception {
    String jsonRequestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

    return mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

}
