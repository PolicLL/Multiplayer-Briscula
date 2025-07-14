package com.example.web.mapper;

import com.example.web.dto.match.CreateMatchDto;
import com.example.web.dto.match.MatchDetailsDto;
import com.example.web.dto.match.MatchDto;
import com.example.web.model.Match;
import com.example.web.model.MatchDetails;
import com.example.web.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MatchMapper {

  Match toMatch(CreateMatchDto createMatchDto);

  @Mapping(target = "userIds", source = "users", qualifiedByName = "mapUsersToIds")
  MatchDto toMatchDto(Match match);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "matchId", source = "match.id")
  MatchDetailsDto toMatchDetailsDto(MatchDetails matchDetails);


  @Named("mapUsersToIds")
  default Set<String> mapUsersToIds(Set<User> users) {
    if (users == null) return Set.of();
    return users.stream()
        .map(User::getId)
        .collect(Collectors.toSet());
  }

}
