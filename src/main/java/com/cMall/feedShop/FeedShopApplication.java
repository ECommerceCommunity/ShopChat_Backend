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
		Dotenv dotenv = Dotenv.load(); // .env 파일을 로드합니다.
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue()) // .env의 키-값 쌍을 시스템 프로퍼티로 설정합니다.
		);
		SpringApplication.run(FeedShopApplication.class, args);
	}
}