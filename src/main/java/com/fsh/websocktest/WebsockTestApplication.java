package com.fsh.websocktest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class WebsockTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebsockTestApplication.class, args);
	}
}

@Configuration
@EnableWebSocket
class WebSocketConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		System.out.println("WebSocketConfig.registerWebSocketHandlers");
		registry.addHandler(myHandler(), "/adx").setAllowedOrigins("*"); // .withSockJS();;
	}

	@Bean
	public WebSocketHandler myHandler() {
		return new MyWebSocketHandler();
	}
}

@Component
class MyWebSocketHandler extends AbstractWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("WebSocketHandler.afterConnectionEstablished with Session ID " + session.getId());
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("WebSocketHandler.afterConnectionClosed");
		super.afterConnectionClosed(session, status);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		System.out.println("New Text Message from session " + session.getId() + " Received:" + message.getPayload());
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
		System.out.println("New Binary Message Received");
	}
}



