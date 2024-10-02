package org.website.steez.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfiguration {

    @Value("${stripe.privateKey}")
    private String secretKey;

    @PostConstruct
    public void initSecretKey() {
        Stripe.apiKey = secretKey;
    }
}
