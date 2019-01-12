package net.wzero.wewallet.core;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.client.RestTemplate;

import net.wzero.wewallet.core.stream.CoreMessage;
import net.wzero.wewallet.core.stream.WorkerMessage;
import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.serv.SmsService;
import net.wzero.wewallet.serv.ThreadLocalService;
import net.wzero.wewallet.serv.impl.SessionServiceImpl;
import net.wzero.wewallet.serv.impl.SmsServiceImpl;
import net.wzero.wewallet.serv.impl.SysParamService;
import net.wzero.wewallet.utils.DateConverterConfig;

@EnableBinding(value= {CoreMessage.class,WorkerMessage.class})
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

	@Bean
    @LoadBalanced//开启负载均衡的能力
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
	@Bean
	public SmsService smsService() {
		return new SmsServiceImpl();
	}
	
	@Bean
	public Converter<String, Date> dateConverter(){
		return new DateConverterConfig();
	}
}

