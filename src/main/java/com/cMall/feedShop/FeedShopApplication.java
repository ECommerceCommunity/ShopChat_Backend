package com.cMall.feedShop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class FeedShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedShopApplication.class, args);
	}
}

