package com.example.web.dto.match;

import com.example.web.model.enums.MatchType;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateMatchDto(
    MatchType type,
    int tournamentId,
    List<String> userIds
) {

}
