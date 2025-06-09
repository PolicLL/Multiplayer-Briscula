package com.example.web.service;

import com.example.web.model.ConnectedPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameEndService {

  public void update(ConnectedPlayer connectedPlayer) {
    log.info("Update.");
  }

}
