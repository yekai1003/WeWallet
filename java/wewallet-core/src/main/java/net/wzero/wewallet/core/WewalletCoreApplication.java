package net.wzero.wewallet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;

import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.serv.ThreadLocalService;
import net.wzero.wewallet.serv.impl.SessionServiceImpl;
import net.wzero.wewallet.serv.impl.SysParamService;

@SpringCloudApplication
public class WewalletCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(WewalletCoreApplication.class, args);
	}

	@Bean
	public ThreadLocalService sysParamService() {
		return new SysParamService();
	}

	@Bean
	public SessionService sessionService() {
		return new SessionServiceImpl();
	}
}

