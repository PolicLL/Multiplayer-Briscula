package com.example.web.dto.match;

import com.example.web.model.enums.MatchType;
import java.util.Set;

public record MatchDto(
    String id,
    MatchType type,
    int tournamentId,
    Set<String> userIds
) {

}
