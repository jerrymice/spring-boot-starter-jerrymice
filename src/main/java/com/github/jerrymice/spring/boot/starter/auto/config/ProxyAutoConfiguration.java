package com.github.jerrymice.spring.boot.starter.auto.config;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.jerrymice.spring.boot.starter.auto.properties.ProxyProperties;

import javax.annotation.PostConstruct;

/**
 * @author yanglei
 * 将代理信息注入到System.properties中
 */
public class ProxyAutoConfiguration {
	/**
	 * 注入代理
	 * @return
	 */
	@Autowired
	private ProxyProperties proxyProperties;

	@PostConstruct
	public void injectSystemProperty(){
		if(proxyProperties.getHttpHost()!=null){
			System.setProperty("jerrymice.spring.boot.config.proxy.http-host", proxyProperties.getHttpHost());
		}
		if(proxyProperties.getHttpPort()!=null){
			System.setProperty("jerrymice.spring.boot.config.proxy.http-port", String.valueOf(proxyProperties.getHttpPort()));
		}
		if(proxyProperties.getHeart()!=null){
			System.setProperty("jerrymice.spring.boot.config.proxy.heart", proxyProperties.getHeart());
		}
	}
}
