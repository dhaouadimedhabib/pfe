package com.example.pfe;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class PfeApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PfeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
