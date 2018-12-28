package net.wzero.wewallet.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.serv.SmsService;
import net.wzero.wewallet.serv.ThreadLocalService;
import net.wzero.wewallet.serv.impl.SessionServiceImpl;
import net.wzero.wewallet.serv.impl.SmsServiceImpl;
import net.wzero.wewallet.serv.impl.SysParamService;

@EnableZuulProxy
@SpringCloudApplication
public class WewalletGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(WewalletGatewayApplication.class, args);
	}

	@Bean
    @LoadBalanced//开启负载均衡的能力
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

	@Bean
	public ThreadLocalService sysParamService() {
		return new SysParamService();
	}
	@Bean
	public SessionService sessionService() {
		return new SessionServiceImpl();
	}
	@Bean
	public SmsService smsService() {
		return new SmsServiceImpl();
	}
}

