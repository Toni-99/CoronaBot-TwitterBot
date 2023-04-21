package dev.twagner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
    Made with <3 and 🦠 by Toni W.
 */

@EnableScheduling
@SpringBootApplication
public class CoronaBot {

    public static void main(String[] args) {
        SpringApplication.run(CoronaBot.class, args);
    }
}