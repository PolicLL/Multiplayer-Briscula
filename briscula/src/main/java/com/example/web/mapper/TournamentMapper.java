package com.example.web.mapper;

import com.example.web.dto.tournament.JoinTournamentResponse;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.dto.tournament.TournamentUpdateDto;
import com.example.web.model.Tournament;
import com.example.web.model.User;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    @Mapping(target = "status", expression = "java(TournamentStatus.INITIALIZING)")
    Tournament toEntity(TournamentCreateDto dto);

    Tournament toEntity(TournamentUpdateDto dto);


    @Mapping(target = "currentNumberOfPlayers", source = "currentPlayersCount")
    TournamentResponseDto toResponseDto(Tournament entity, int currentPlayersCount);


    @Mapping(target = "userIds", source = "users", qualifiedByName = "mapUsersToIds")
    JoinTournamentResponse toJoinTournamentResponseDto(Tournament entity);

    @Named("mapUsersToIds")
    default Set<String> mapUsersToIds(Set<User> users) {
        if (users == null) return Set.of();
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}