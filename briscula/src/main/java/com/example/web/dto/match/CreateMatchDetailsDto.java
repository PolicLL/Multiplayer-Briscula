package com.example.web.dto.match;

import com.example.web.model.Match;
import lombok.Builder;

@Builder
public record CreateMatchDetailsDto(
        String userId,
        Match match,
        int group
) {

}
