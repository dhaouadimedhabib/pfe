package com.example.pfe.Config;


import com.stripe.Stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StripeConfig {

    @Value("${stripe.apiKey}")
    public String secretKey;

    @Bean
    public Stripe stripe() {
        Stripe.apiKey = secretKey;
        return new Stripe() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
    }


}