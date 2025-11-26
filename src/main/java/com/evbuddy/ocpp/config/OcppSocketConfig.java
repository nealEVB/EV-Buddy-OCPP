package com.evbuddy.ocpp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.evbuddy.ocpp.ws.OcppWebSocketHandler;

@Configuration
@EnableWebSocket
public class OcppSocketConfig implements WebSocketConfigurer {

    @Autowired
    private OcppWebSocketHandler ocppWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ocppWebSocketHandler, "/ocpp/{stationId}")
                .setAllowedOrigins("*");
    }
}
