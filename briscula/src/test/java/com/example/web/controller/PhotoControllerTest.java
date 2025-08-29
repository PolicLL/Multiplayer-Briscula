package com.example.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.Constants.getMultipartFileImage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class PhotoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void uploadPhotoSuccess() throws Exception {
    mockMvc.perform(multipart("/api/photo")
            .file(getMultipartFileImage())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
  }

  @Test
  void uploadPhotoWithSameNameSuccess() throws Exception {
    String sameName = "sample.jpg";

    mockMvc.perform(multipart("/api/photo")
                    .file(getMultipartFileImage(sameName))
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

    mockMvc.perform(multipart("/api/photo")
                    .file(getMultipartFileImage(sameName))
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());
  }

  @Test
  void getPhotoSuccess() throws Exception {
    String photoId = mockMvc.perform(multipart("/api/photo")
            .file(getMultipartFileImage())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc.perform(get("/api/photo/{photoId}", photoId))
        .andExpect(status().isOk())
        .andExpect(header().exists("Content-Disposition"))
        .andExpect(content().contentType(MediaType.IMAGE_JPEG));
  }

  @Test
  void getPhotoNotFound() throws Exception {
    mockMvc.perform(get("/api/photo/NON_EXISTING_ID"))
        .andExpect(status().isNotFound());
  }
}
