package com.example.web.dto.match;

import com.example.web.model.Match;

import java.util.List;

import lombok.Builder;

@Builder
public record MatchesCreatedResponse(
        String tournamentId,
        List<Match> matches,
        List<MatchDetailsDto> matchDetailsDtoList
) {

}
