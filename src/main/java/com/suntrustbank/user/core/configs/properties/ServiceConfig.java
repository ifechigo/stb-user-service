package com.suntrustbank.user.core.configs.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Generated
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "service.user")
public class ServiceConfig {
    private String authServiceUrl;
    private String transactionServiceUrl;
    private String walletServiceUrl;
}
