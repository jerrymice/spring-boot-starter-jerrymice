package com.github.jerrymice.spring.boot.mvc.bean;

import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tumingjian
 * 创建时间: 2019-11-01 10:33
 * 功能说明:
 */
public class SuperHeaderHttpSessionIdResolver implements HttpSessionIdResolver {
    /**
     * sessionId 在各个环境下的key名称
     */
    private String sessionAliasParamName;
    /**
     * 是否启http header 存取sessionId
     */
    private boolean supportHttpHeader;
    /**
     * 是否启用http queryString 查询sessionId
     */
    private boolean supportQueryString;
    /**
     * 支持cookie中存取sessionId
     */
    private boolean supportCookie;

    /**
     * 默认的cookies
     */
    private CookieHttpSessionIdResolver defaultHttpSessionIdResolver = new CookieHttpSessionIdResolver();

    public SuperHeaderHttpSessionIdResolver(String sessionAliasParamName, boolean supportHttpHeader, boolean supportQueryString, boolean supportCookie) {
        this.sessionAliasParamName = sessionAliasParamName;
        this.supportHttpHeader = supportHttpHeader;
        this.supportQueryString = supportQueryString;
        this.supportCookie = supportCookie;
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        List<String> sessionId = new ArrayList<>();
        if (supportCookie) {
            sessionId.addAll(defaultHttpSessionIdResolver.resolveSessionIds(request));
        }
        if (sessionId.size()==0 && supportHttpHeader) {
            String headSessionId;
            if((headSessionId=request.getHeader(this.sessionAliasParamName))!=null){
                sessionId.add(headSessionId);
            }
        }
        if (sessionId.size()==0 && supportQueryString) {
            //从get参数中获取sessionId
            String queryString;
            if (sessionId.size()==0 && (queryString = request.getQueryString()) != null) {
                Pattern compile = Pattern.compile("(^|[\\s\\S]*?&)" + sessionAliasParamName + "=([\\s\\S]*?)($|&[\\s\\S]*$)");
                Matcher matcher = compile.matcher(queryString);
                if (matcher.matches()) {
                    sessionId.add(matcher.group(2));
                }
            }
        }
        return sessionId;
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        if (supportHttpHeader) {
            response.setHeader(this.sessionAliasParamName, sessionId);
        }
        if (supportCookie) {
            defaultHttpSessionIdResolver.setSessionId(request, response,sessionId);
        }
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        if (supportHttpHeader) {
            response.setHeader(this.sessionAliasParamName, "");
        }
        if (supportCookie) {
            defaultHttpSessionIdResolver.expireSession(request, response);
        }
    }
}
