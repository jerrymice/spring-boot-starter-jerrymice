package com.github.jerrymice.spring.boot.mvc.bean;

import com.github.jerrymice.spring.boot.EnableJerryMice;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author tumingjian
 * 说明:根据order的优先级来注册或替换已有的RequestMappingInfo,order值越小优先级越高
 * 你可以在Controller的Method方法上添加Order注解,当@RequestMapping中的path重复时,
 * spring mvc 会将RequestMapping中的Path路径映射到Order优先级最高的Method方法上.
 * @see Order
 * @see EnableJerryMice
 * @see org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations
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
            Order currentOrder = method.getAnnotation(Order.class);
            int currentOrderValue = currentOrder != null ? currentOrder.value() : Ordered.HIGHEST_PRECEDENCE;
            Order alreadyOrder = handlerMethod.getMethodAnnotation(Order.class);
            int alreadyOrderValue = alreadyOrder != null ? alreadyOrder.value() : Ordered.HIGHEST_PRECEDENCE;
            if (currentOrderValue < alreadyOrderValue) {
                super.unregisterMapping(mapping);
                super.registerMapping(mapping, handler, method);
                logger.info("map "+mapping+"replace method,old method:"+handlerMethod+",newMethod:"+method);
            } else if (currentOrderValue > alreadyOrderValue) {
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
