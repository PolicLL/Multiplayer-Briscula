package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class BrisculaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrisculaApplication.class, args);
    }

}

// TODO

/*

- when editing user, country that is currently selected should be shown
- handle error on frontend side for anonymous user when he chooses the name that is used on backend
- after tournament is finished, number of it's default players should again be increased by number of bots
- check timers
- add indexes to the players so that you can know what is the order of throwing and then u can know if you are the last person

 */