package com.github.jerrymice.spring.boot.mvc.bean;


import com.github.jerrymice.spring.boot.EnableJerryMice;
import com.github.jerrymice.spring.boot.mvc.config.WebAutoConfiguration;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tumingjian
 * 说明:更强的HttpSessionStrategy策略.
 * @see EnableJerryMice
 * @see WebAutoConfiguration.BeanConfiguration
 */
public class SuperHeaderHttpSessionStrategy implements HttpSessionStrategy {
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
    private CookieHttpSessionStrategy defaultSessionStrategy = new CookieHttpSessionStrategy();

    public SuperHeaderHttpSessionStrategy() {
    }

    public SuperHeaderHttpSessionStrategy(String sessionAliasParamName, boolean supportHttpHeader, boolean supportQueryString, boolean supportCookie) {
        this.sessionAliasParamName = sessionAliasParamName;
        this.supportHttpHeader = supportHttpHeader;
        this.supportQueryString = supportQueryString;
        this.defaultSessionStrategy.setSessionAliasParamName(sessionAliasParamName);
        this.supportCookie = supportCookie;
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        String sessionId = null;
        if (supportCookie) {
            sessionId = defaultSessionStrategy.getRequestedSessionId(request);
        }
        if (sessionId == null && supportHttpHeader) {
            sessionId = request.getHeader(this.sessionAliasParamName);
        }
        if (sessionId == null && supportQueryString) {
            //从get参数中获取sessionId
            String queryString;
            if (sessionId == null && (queryString = request.getQueryString()) != null) {
                Pattern compile = Pattern.compile("(^|[\\s\\S]*?&)" + sessionAliasParamName + "=([\\s\\S]*?)($|&[\\s\\S]*$)");
                Matcher matcher = compile.matcher(queryString);
                if (matcher.matches()) {
                    sessionId = matcher.group(2);
                }
            }
        }
        return sessionId;
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request,
                             HttpServletResponse response) {
        if (supportHttpHeader) {
            response.setHeader(this.sessionAliasParamName, session.getId());
        }
        if (supportCookie) {
            defaultSessionStrategy.onNewSession(session, request, response);
        }
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request,
                                    HttpServletResponse response) {
        if (supportHttpHeader) {
            response.setHeader(this.sessionAliasParamName, "");
        }
        if (supportCookie) {
            defaultSessionStrategy.onInvalidateSession(request, response);
        }
    }

    public String getSessionAliasParamName() {
        return sessionAliasParamName;
    }

    public void setSessionAliasParamName(String sessionAliasParamName) {
        this.sessionAliasParamName = sessionAliasParamName;
    }

    public boolean isSupportCookie() {
        return supportCookie;
    }

    public void setSupportCookie(boolean supportCookie) {
        this.supportCookie = supportCookie;
    }

    public boolean isSupportHttpHeader() {
        return supportHttpHeader;
    }

    public void setSupportHttpHeader(boolean supportHttpHeader) {
        this.supportHttpHeader = supportHttpHeader;
    }

    public boolean isSupportQueryString() {
        return supportQueryString;
    }

    public void setSupportQueryString(boolean supportQueryString) {
        this.supportQueryString = supportQueryString;
    }
}
