package com.suntrustbank.user.core.configs.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OtpDevConfig {

    @Value("${development.phone.otp}")
    private String phoneOtp;

}