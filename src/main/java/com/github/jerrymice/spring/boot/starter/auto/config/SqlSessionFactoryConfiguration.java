package com.github.jerrymice.spring.boot.starter.auto.config;

import com.github.jerrymice.spring.boot.starter.EnableJerrymiceSpringBootConfiguration;
import com.github.jerrymice.spring.boot.starter.auto.bean.SuperSqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author kexl
 * spring boot
 *
 * 在新版mybatis boot starter中,这个功能已经鸡肋
 */
@Deprecated()
@ConditionalOnProperty(name = EnableJerrymiceSpringBootConfiguration.WEB_SQL_SESSIONF_FACTORY_ENABLE,havingValue ="true",matchIfMissing = true)
public class SqlSessionFactoryConfiguration {

	@Bean
	@ConditionalOnMissingBean(SqlSessionFactoryBean.class)
	@ConditionalOnClass(org.apache.ibatis.session.Configuration.class)
    /**
     * 在有mybatis plus的情况下不注入.因为这个SqlSessionFactoryBean与mybatis plus 的SqlSessionFactory不兼容
     */
    @ConditionalOnMissingClass(value = "com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean")
    public SqlSessionFactoryBean superSqlSessionFactoryBean(DataSource dataSource) {
		SuperSqlSessionFactoryBean superSqlSessionFactoryBean = new SuperSqlSessionFactoryBean();
		HashMap<String, Boolean> config = new HashMap<>();
		config.put(SuperSqlSessionFactoryBean.CONFIGURATIONPROPERTY_CALLSETTERSONNULLS, true);
		superSqlSessionFactoryBean.setConfigurationProperty(config);
		superSqlSessionFactoryBean.setDataSource(dataSource);
		return superSqlSessionFactoryBean;
	}
}
