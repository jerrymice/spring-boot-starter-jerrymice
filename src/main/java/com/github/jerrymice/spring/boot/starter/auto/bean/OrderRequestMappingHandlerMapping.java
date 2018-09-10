package com.github.jerrymice.spring.boot.starter.auto.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author tumingjian
 * 说明:根据order的优先级来注册或替换已有的RequestMappingInfo,order值越小优先级越高
 * @see Order
 */
public class OrderRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    /**
     * 根据order的优先级来注册或替换已有的RequestMappingInfo,order值越小优先级越高
     *
     * @param mapping RequestMappingInfo  @RequestMapping
     * @param handler Controller
     * @param method  Controller Mapping Method
     */
    private void selectiveOrderRegister(RequestMappingInfo mapping, Object handler, Method method) {
        HandlerMethod handlerMethod = super.getHandlerMethods().get(mapping);

        if (handlerMethod != null) {
            Order newOrder = method.getAnnotation(Order.class);
            int newOrderIndex = newOrder != null ? newOrder.value() : Ordered.HIGHEST_PRECEDENCE;
            Order order = handlerMethod.getMethodAnnotation(Order.class);
            int orderIndex = order != null ? order.value() : Ordered.HIGHEST_PRECEDENCE;
            if (newOrderIndex < orderIndex) {
                super.unregisterMapping(mapping);
                super.registerMapping(mapping, handler, method);
                logger.info("map "+mapping+"replace method,old method:"+handlerMethod+",newMethod:"+method);
            } else if (newOrderIndex > orderIndex) {
                logger.info("map "+mapping+"skip method,already method:"+handlerMethod+"skip method:"+method);
                return;
            } else {
                throw new IllegalStateException(
                        "order value same,Ambiguous mapping. Cannot map '" + mapping.getPatternsCondition().toString() + "' method \n" +
                                method + "\nto " + mapping + ": There is already '" +
                                handlerMethod.getBean() + "' bean method\n" + handlerMethod + " mapped.");
            }
        } else {
            super.registerMapping(mapping, handler, method);
        }
    }

    @Override
    public void registerMapping(RequestMappingInfo mapping, Object handler, Method method) {
        this.selectiveOrderRegister(mapping, handler, method);
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        this.selectiveOrderRegister(mapping, handler, method);
    }
}
