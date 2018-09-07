package com.github.jerrymice.spring.boot.starter.auto.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.Session;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tumingjian
 * @date 2018/1/5
 * 说明:支持在header中读取token,支持在请求参数中读取token
 */
public class SupperHeaderHttpSessionStrategy implements HttpSessionStrategy {
    private String headerName = "access-token";
    @Value("${debug:false}")
    private Boolean debug;

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader(this.headerName);
        //从get参数中获取token
        String queryString = request.getQueryString();
        if (sessionId == null && queryString != null) {
            Pattern compile = Pattern.compile("(^|[\\s\\S]*?&)" + headerName + "=([\\s\\S]*?)($|&[\\s\\S]*$)");
            Matcher matcher = compile.matcher(queryString);
            if (matcher.matches()) {
                sessionId = matcher.group(2);
            }
        }
        //从cookies中获取sessionId
        if (sessionId == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(headerName)) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return sessionId;
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request,
                             HttpServletResponse response) {
        response.setHeader(this.headerName, session.getId());
        if (debug) {
            Cookie cookie = new Cookie(headerName, session.getId());
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request,
                                    HttpServletResponse response) {
        response.setHeader(this.headerName, "");
        if (debug) {
            Cookie cookie = new Cookie(headerName, request.getSession().getId());
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * The name of the header to obtain the session id from. Default is "x-auth-token".
     *
     * @param headerName the name of the header to obtain the session id from.
     */
    public void setHeaderName(String headerName) {
        Assert.notNull(headerName, "headerName cannot be null");
        this.headerName = headerName;
    }

    public void setDebug(Boolean debug){
        this.debug=debug;
    }
}
