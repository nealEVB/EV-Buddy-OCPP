package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import handler.CentralSystemHandler;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Using @Autowired on the field simplifies the dependency injection
    // and resolves the startup order issue.
    @Autowired
    private CentralSystemHandler centralSystemHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(centralSystemHandler, "/ocpp/{chargePointId}")
                .setAllowedOrigins("*");
    }
}