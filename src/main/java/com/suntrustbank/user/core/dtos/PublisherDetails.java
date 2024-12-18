package com.suntrustbank.user.core.dtos;

import lombok.Getter;

@Getter
public enum PublisherDetails {
    NOTIFICATION_EXCHANGE_NAME("notification.exchange"),
    SMS_ROUTING_KEY("sms");

    private String value;

    PublisherDetails(String value) {
        this.value = value;
    }

}
