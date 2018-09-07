package com.github.jerrymice.spring.boot.starter.auto.bean;

import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

import java.io.IOException;
import java.util.Map;

/**
 * @author kexl
 * @Title: spring-boot-starter-zhongjin-web
 * @Package com.zhongjin.base.config.starter.auto.bean
 * @Description: 支持非XML方式配置mybatis SqlSessionFactory的几个基本属性.
 * @date 2018/8/6 14:57
 */
@Deprecated
public class SuperSqlSessionFactoryBean extends SqlSessionFactoryBean {

	private Map<String,Boolean> configurationProperty;
	public static final String CONFIGURATIONPROPERTY_SAFEROWBOUNDSENABLED="safeRowBoundsEnabled";
	public static final String CONFIGURATIONPROPERTY_SAFERESULTHANDLERENABLED="safeResultHandlerEnabled";
	public static final String CONFIGURATIONPROPERTY_MAPUNDERSCORETOCAMELCASE="mapUnderscoreToCamelCase";
	public static final String CONFIGURATIONPROPERTY_AGGRESSIVELAZYLOADING="aggressiveLazyLoading";
	public static final String CONFIGURATIONPROPERTY_MULTIPLERESULTSETSENABLED="multipleResultSetsEnabled";
	public static final String CONFIGURATIONPROPERTY_USEGENERATEDKEYS="useGeneratedKeys";
	public static final String CONFIGURATIONPROPERTY_USECOLUMNLABEL="useColumnLabel";
	public static final String CONFIGURATIONPROPERTY_CACHEENABLED="cacheEnabled";
	public static final String CONFIGURATIONPROPERTY_CALLSETTERSONNULLS="callSettersOnNulls";
	@Override
	public SqlSessionFactory buildSqlSessionFactory()throws IOException {
		SqlSessionFactory sqlSessionFactory = super.buildSqlSessionFactory();
		Configuration configuration = sqlSessionFactory.getConfiguration();
		if(!MapUtils.isEmpty(configurationProperty)){
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_SAFEROWBOUNDSENABLED)){
				configuration.setSafeRowBoundsEnabled(configurationProperty.get(CONFIGURATIONPROPERTY_SAFEROWBOUNDSENABLED));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_SAFERESULTHANDLERENABLED)){
				configuration.setSafeResultHandlerEnabled(configurationProperty.get(CONFIGURATIONPROPERTY_SAFERESULTHANDLERENABLED));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_MAPUNDERSCORETOCAMELCASE)){
				configuration.setMapUnderscoreToCamelCase(configurationProperty.get(CONFIGURATIONPROPERTY_MAPUNDERSCORETOCAMELCASE));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_AGGRESSIVELAZYLOADING)){
				configuration.setAggressiveLazyLoading(configurationProperty.get(CONFIGURATIONPROPERTY_AGGRESSIVELAZYLOADING));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_MULTIPLERESULTSETSENABLED)){
				configuration.setMultipleResultSetsEnabled(configurationProperty.get(CONFIGURATIONPROPERTY_MULTIPLERESULTSETSENABLED));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_USEGENERATEDKEYS)){
				configuration.setUseGeneratedKeys(configurationProperty.get(CONFIGURATIONPROPERTY_USEGENERATEDKEYS));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_USECOLUMNLABEL)){
				configuration.setUseColumnLabel(configurationProperty.get(CONFIGURATIONPROPERTY_USECOLUMNLABEL));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_CACHEENABLED)){
				configuration.setCacheEnabled(configurationProperty.get(CONFIGURATIONPROPERTY_CACHEENABLED));
			}
			if(configurationProperty.containsKey(CONFIGURATIONPROPERTY_CALLSETTERSONNULLS)){
				configuration.setCallSettersOnNulls(configurationProperty.get(CONFIGURATIONPROPERTY_CALLSETTERSONNULLS));
			}
		}
		return sqlSessionFactory;
	}

	public Map<String, Boolean> getConfigurationProperty() {
		return configurationProperty;
	}

	public void setConfigurationProperty(Map<String, Boolean> configurationProperty) {
		this.configurationProperty = configurationProperty;
	}
}
