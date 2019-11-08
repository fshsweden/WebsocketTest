package com.fsh.websocktest;

import com.fsh.websocktest.messages.BagOfKeyValues;
import com.fsh.websocktest.models.Future;
import com.fsh.websocktest.services.MyJsonMessageBroker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				System.out.println("Adding global CORS mapping");
//				registry.addMapping("/adx").allowedOrigins("*")
//						.allowedOrigins("*").allowedMethods("POST, GET, HEAD, OPTIONS")
//						.allowCredentials(true)
//						.allowedHeaders("Content-Type","X-Requested-With","accept","Origin","Access-Control-Request-Method","Access-Control-Request-Headers")
//						.exposedHeaders("Access-Control-Allow-Origin","Access-Control-Allow-Credentials")
//						.maxAge(10);
//			}
//		};
//	}
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

	@Autowired
	MyJsonMessageBroker myJsonMessageBroker;
	private Gson gson = new Gson();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("WebSocketHandler.afterConnectionEstablished with Session ID " + session.getId());
		myJsonMessageBroker.registerSession(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("WebSocketHandler.afterConnectionClosed");
		myJsonMessageBroker.unregisterSession(session);
		super.afterConnectionClosed(session, status);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		System.out.println("New Text Message from session " + session.getId() + " Received:" + message.getPayload());
		JsonObject jsonObject = JsonParser.parseString(message.getPayload()).getAsJsonObject();

		// convert
		BagOfKeyValues bokv = new BagOfKeyValues();
		jsonObject.entrySet().forEach((e) -> {
			System.out.println("Setting key " + e.getKey() + " to " + e.getValue());
			bokv.setKeyValue(e.getKey(), e.getValue().getAsString());
		});
		myJsonMessageBroker.handleIncomingMessage(session, bokv);
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
		System.out.println("New Binary Message Received");
	}
}



enum MonthName {
	January ("JAN", 1),
	February ("FEB", 2),
	March ("MAR", 3),
	April ("APR", 4),
	May ("MAY", 5),
	June ("JUN", 6),
	July ("JUL", 7),
	August ("AUG", 8),
	September ("SEP", 9),
	October ("OCT", 10),
	November ("NOV", 11),
	December ("DEC", 12);

	private final String monthcode;
	private final Integer monthnumber;

	public String getMonthcode() {
		return monthcode;
	}

	public Integer getMonthNumber() {
		return monthnumber;
	}

	private MonthName(String s, Integer num) {
		monthcode = s;
		monthnumber = num;
	}

	public boolean equalsName(String otherName) {
		// (otherName == null) check is not needed because name.equals(null) returns false
		return monthcode.equals(otherName);
	}

	public String toString() {
		return this.monthcode;
	}
}



@Component
class DataInit implements ApplicationRunner {

	@Autowired
	public DataInit() {

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("Setting up a few products!");

		Arrays.asList("001","002","003","004","005").forEach(cls -> {
			Arrays.asList(2020, 2021, 2022).forEach(year -> {
				makeFuture("AFT", "Aftonbladet", cls, MonthName.January, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.February, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.March, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.April, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.May, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.June, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.July, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.August, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.September, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.October, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.November, year);
				makeFuture("AFT", "Aftonbladet", cls, MonthName.December, year);
			});
		});
	}

	private int productId = 1;
	private void makeFuture(String underlying, String longname, String cls, MonthName monthname, int year) {
		String symbol = String.format("%s%s%s%d", underlying, cls,monthname.getMonthcode(), year);
		String name = String.format("%s klass %s %s %d", longname, cls, monthname.getMonthcode(), year);
		String productIdStr = String.format("%d",productId++);

		Future f = new Future(symbol, name);
	}
}