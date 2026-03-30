package ru.vsu.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.vsu.contract.util.EnvFileLoader;

@SpringBootApplication
public class TgBotApplication {

    public static void main(String[] args) {
        EnvFileLoader.load("tg_bot");
        SpringApplication.run(TgBotApplication.class, args);
    }

}
