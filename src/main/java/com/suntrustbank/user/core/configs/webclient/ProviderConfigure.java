package com.suntrustbank.user.core.configs.webclient;

import io.netty.channel.ChannelHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@Builder
public class ProviderConfigure {

    private String baseUrl;
    private HashMap<String, String> headers;
    private ArrayList<ChannelHandler> channelHandlers;
    private ArrayList<ExchangeFilterFunction> filterFunctions;

    @Builder.Default
    private boolean shouldUseBaseUrl = true;

    public void validate() {
        if (!StringUtils.hasText(baseUrl) && shouldUseBaseUrl) {
            throw new IllegalStateException("ProviderConfigure.baseUrl invalid");
        }
    }
}
