package com.example.web.dto.match;

import java.util.List;
import lombok.Builder;

@Builder
public record MatchesCreatedResponse(
    String tournamentId,
    List<MatchDetailsDto> matchDetailsDtoList
) {

}
