package ru.vsu.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.vsu.contract.util.EnvFileLoader;

@SpringBootApplication
public class CoreApplication {

	public static void main(String[] args) {
		EnvFileLoader.load("core");
		SpringApplication.run(CoreApplication.class, args);
	}

}
