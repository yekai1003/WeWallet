package net.wzero.wewallet.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Hello {

	@Value("${my.hello}")  
	private String world;
	@RequestMapping("/hello")
	public String hello() {
		return "Hello "+world;
	}
}
