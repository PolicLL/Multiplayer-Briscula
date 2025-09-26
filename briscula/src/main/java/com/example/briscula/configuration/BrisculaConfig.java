package com.example.briscula.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "briscula")
@Getter
@Setter
public class BrisculaConfig {

    private int waitingTimeInSecondsForChoosingCard;

    private int waitingTimeAfterRoundInSeconds;

    @Getter
    private static int waitingTimeForChoosingCard;

    @Getter
    private static int waitingTimeAfterRoundFinishes;

    @PostConstruct
    public void init() {
        waitingTimeForChoosingCard = waitingTimeInSecondsForChoosingCard;
        waitingTimeAfterRoundFinishes = waitingTimeAfterRoundInSeconds;
    }
}
