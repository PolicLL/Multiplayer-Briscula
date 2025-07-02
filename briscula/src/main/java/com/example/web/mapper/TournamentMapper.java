package com.example.web.mapper;

import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.model.Tournament;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

  Tournament toEntity(TournamentCreateDto dto);

  TournamentResponseDto toResponseDto(Tournament entity);
}