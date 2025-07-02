package com.cMall.feedShop;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class FeedShopApplication {

	public static void main(String[] args) {
		// Dotenv 로드
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Dotenv entries -> System properties 로 등록
		dotenv.entries().forEach(entry -> {
			if (System.getProperty(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		});

		SpringApplication.run(FeedShopApplication.class, args);
	}
}
