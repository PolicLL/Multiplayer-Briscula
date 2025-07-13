package com.example.web.service;

import com.example.web.dto.match.CreateMatchDto;
import com.example.web.model.Match;
import com.example.web.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;

  public Match createMatch(CreateMatchDto createMatchDto) {



    return null;
  }

}
