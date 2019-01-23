package net.wzero.wewallet.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class WewalletEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WewalletEurekaServerApplication.class, args);
	}

}

