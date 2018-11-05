package com.github.jerrymice.spring.boot.starter.auto.config;

import com.github.jerrymice.spring.boot.starter.EnableJerryMiceSpringMvcConfiguration;
import com.github.jerrymice.spring.boot.starter.auto.bean.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author kexl
 * @Title: spring-boot-starter-jerrymice
 * @Package com.github.jerrymice.spring.boot.starter.auto.config
 * @Description:统一异常处理
 * @date 2018/11/6 10:55
 */
@ConditionalOnProperty(name=EnableJerryMiceSpringMvcConfiguration.WEB_GLOBAL_EXCEPTION_ENABLED,havingValue = "true")
public class WebGlobalExceptionConfiguration {

	@Bean
	@ConditionalOnMissingBean(GlobalExceptionHandler.class)
	public GlobalExceptionHandler globalExceptionHandler(){
		return new GlobalExceptionHandler();
	}
}
