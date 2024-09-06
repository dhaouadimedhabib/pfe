package com.example.pfe;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@PropertySource(value = "google-client.properties")
@EnableWebSecurity
public class PfeApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PfeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
