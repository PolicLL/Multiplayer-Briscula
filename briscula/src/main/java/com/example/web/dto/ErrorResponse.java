package com.example.web.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}