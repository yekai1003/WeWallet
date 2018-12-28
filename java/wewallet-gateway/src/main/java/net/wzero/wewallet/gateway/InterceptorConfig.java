package net.wzero.wewallet.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig {

	@Autowired
	private SystemParamInterceptor systemParamInterceptor;
	@Bean
	public WebMvcConfigurer authorizeConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				//系统参数拦截器
				registry.addInterceptor(systemParamInterceptor).addPathPatterns("/**");
				super.addInterceptors(registry);
			}
		};
	}
}
