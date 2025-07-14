package com.cMall.feedShop.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Test configuration for JavaMailSender.
 * Provides a dummy/mock JavaMailSender for test environments.
 */
@TestConfiguration
@Profile("test")
public class TestMailConfig {

    /**
     * Provides a dummy JavaMailSender for tests.
     * This prevents actual emails from being sent during testing.
     */
    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Set dummy configuration that won't actually send emails
        mailSender.setHost("localhost");
        mailSender.setPort(1025); // Common test SMTP port
        mailSender.setUsername("test@example.com");
        mailSender.setPassword("testpassword");
        
        // Configure properties to prevent actual sending
        mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", "false");
        mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "false");
        mailSender.getJavaMailProperties().setProperty("mail.debug", "false");
        
        return mailSender;
    }
} 