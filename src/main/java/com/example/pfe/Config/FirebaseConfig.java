package com.example.pfe.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = Logger.getLogger(FirebaseConfig.class.getName());

    @Value("${firebase.service.account.file}")
    private String serviceAccountFilePath;

    private final ResourceLoader resourceLoader;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        Resource resource = resourceLoader.getResource(serviceAccountFilePath);
        try (InputStream serviceAccount = resource.getInputStream()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize Firebase: " + e.getMessage(), e);
            throw e; // Rethrow the exception after logging
        }
    }
}
