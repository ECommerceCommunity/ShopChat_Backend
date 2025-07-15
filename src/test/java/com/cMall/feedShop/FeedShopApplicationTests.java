package com.cMall.feedShop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {MailSenderAutoConfiguration.class})
class FeedShopApplicationTests {

    @MockBean
    private JavaMailSender javaMailSender;
    @Test
    void contextLoads() {
    }
} 