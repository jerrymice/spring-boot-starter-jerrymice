package com.github.jerrymice.spring.boot.mvc.config;

import com.github.jerrymice.spring.boot.EnableJerryMice;
import com.github.jerrymice.spring.boot.mvc.bean.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kexl
 * 统一异常处理类
 * 当同时启用了统一返回值处理器时,所有异常的HttpStatus都变为200,方便前台统一处理
 */
@ConditionalOnWebApplication
@ConditionalOnProperty(name= EnableJerryMice.WEB_GLOBAL_EXCEPTION_ENABLED,havingValue = "true",matchIfMissing = true)
@Configuration
public class WebGlobalExceptionConfiguration {

	@Bean
	@ConditionalOnMissingBean(GlobalExceptionHandler.class)
	public GlobalExceptionHandler globalExceptionHandler(){
		return new GlobalExceptionHandler();
	}

}
