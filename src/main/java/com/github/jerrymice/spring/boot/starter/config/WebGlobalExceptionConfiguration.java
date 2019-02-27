package com.github.jerrymice.spring.boot.starter.config;

import com.github.jerrymice.spring.boot.starter.EnableJerryMiceSpringMvcConfiguration;
import com.github.jerrymice.spring.boot.starter.bean.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author kexl
 * 统一异常处理
 */
@ConditionalOnProperty(name=EnableJerryMiceSpringMvcConfiguration.WEB_GLOBAL_EXCEPTION_ENABLED,havingValue = "true",matchIfMissing = true)
public class WebGlobalExceptionConfiguration {

	@Bean
	@ConditionalOnMissingBean(GlobalExceptionHandler.class)
	public GlobalExceptionHandler globalExceptionHandler(){
		return new GlobalExceptionHandler();
	}
}
