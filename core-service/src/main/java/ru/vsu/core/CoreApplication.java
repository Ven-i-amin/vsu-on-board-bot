package ru.vsu.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.vsu.contract.util.EnvFileLoader;

@SpringBootApplication
@EnableScheduling
public class CoreApplication {

	public static void main(String[] args) {
		EnvFileLoader.load("core");
		SpringApplication.run(CoreApplication.class, args);
	}

}
