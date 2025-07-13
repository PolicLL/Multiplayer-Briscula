package com.example.web.mapper;

import com.example.web.dto.match.CreateMatchDto;
import com.example.web.model.Match;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchMapper {

  Match toResponseDto(CreateMatchDto createMatchDto);

}
