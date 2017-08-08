package com.newrelic.fit.instrumentation;

import java.util.logging.Level;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "javax.servlet.http.HttpServlet", type = MatchType.BaseClass)
public abstract class HttpServlet_CustomParams_Instrumentation {
	
	@NewField
	private static String[] headerNames = null;
	
	@NewField
	private static String[] parameterNames = null;
	
	@NewField
	private static String[] cookieNames = null;
	
	public HttpServlet_CustomParams_Instrumentation() {
		Logger nrLogger = NewRelic.getAgent().getLogger();
		Object headerNameObj = NewRelic.getAgent().getConfig().getValue("custom_request_header_names");
		
		if(headerNameObj != null) {
			String headerName = (String) headerNameObj;
			try {
				headerNames = headerName.split("\\s*,\\s*");
			} catch (Throwable t) {
				nrLogger.log(Level.SEVERE, "Custom Instrumentation - Error setting up request headers " + t.getMessage());
			}
			nrLogger.log(Level.FINER, "Custom Instrumentation - Getting request headers for these following headers");
			for (int i = 0; i < headerNames.length; i++) {
				String name = headerNames[i];
				nrLogger.log(Level.FINER, "Custom Instrumentation - adding header name: " + name);
			}
		} else {
			nrLogger.log(Level.FINER, "Custom Instrumentation - custom_request_header_names not defined.");
			nrLogger.log(Level.FINER, "Custom Instrumentation - use \"custom_request_header_names: [comma separated header names]\" in newrelic.yml");
		}
		
		Object paramNameObj = NewRelic.getAgent().getConfig().getValue("custom_request_parameter_names");
		if(paramNameObj != null) {
			String parameterName = (String) paramNameObj;
			try {
				parameterNames = parameterName.split("\\s*,\\s*");
			} catch (Throwable t) {
				nrLogger.log(Level.SEVERE, "Custom Instrumentation - Error setting up request parameters " + t.getMessage());
			}
			nrLogger.log(Level.FINER, "Custom Instrumentation - Getting request parameters for the following parameters ");
			for (int i = 0; i < parameterNames.length; i++) {
				String name = parameterNames[i];
				nrLogger.log(Level.FINER, "Custom Instrumentation - adding parameter name: " + name);
			}
		} else {
			nrLogger.log(Level.FINER, "Custom Instrumentation - custom_request_parameter_names not defined.");
			nrLogger.log(Level.FINER, "Custom Instrumentation - use \"custom_request_parameter_names: [comma separated parameter names]\" in newrelic.yml");
		}
		
		Object cookieNameObj = NewRelic.getAgent().getConfig().getValue("custom_request_cookie_names");
		if(cookieNameObj != null) {
			String cookieName = (String) cookieNameObj;
			try {
				cookieNames = cookieName.split("\\s*,\\s*");
			} catch (Throwable t) {
				nrLogger.log(Level.SEVERE, "Custom Instrumentation - Error setting up request cookies " + t.getMessage());
			}
			nrLogger.log(Level.FINER, "Custom Instrumentation - Getting request cookies for the following cookies ");
			for (int i = 0; i < cookieNames.length; i++) {
				String name = cookieNames[i];
				nrLogger.log(Level.FINER, "Custom Instrumentation - adding cookie name: " + name);
			}
		} else {
			nrLogger.log(Level.FINER, "Custom Instrumentation - custom_request_cookie_names not defined.");
			nrLogger.log(Level.FINER, "Custom Instrumentation - use \"custom_request_cookie_names: [comma separated cookie names]\" in newrelic.yml");
		}
	}
	
	@Trace(dispatcher = true)
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		Logger nrLogger = NewRelic.getAgent().getLogger();
		if (headerNames != null) {
			for (int i = 0; i < headerNames.length; i++) {
				String headerName = headerNames[i];
				if(headerName.equals("URL")) {
					String requestURL = request.getRequestURL().toString();
					if(request.getQueryString() != null) {
						requestURL += "?" + request.getQueryString();
					}
					nrLogger.log(Level.FINER, "Custom Instrumentation - Reading request URL value " + requestURL);
					NewRelic.addCustomParameter("request-URL", requestURL);						
				} else {
					String headerValue = request.getHeader(headerName); 
					if (headerValue != null) {
						nrLogger.log(Level.FINER, "Custom Instrumentation - Reading request header value " + headerValue);
						NewRelic.addCustomParameter("request-header-" + headerName, headerValue);
					}
				}
			}
		}
		if (parameterNames != null) {
			for (int i = 0; i < parameterNames.length; i++) {
				String parameterName = parameterNames[i];
				String parameterValue = request.getParameter(parameterName); 
				if (parameterValue != null) {
					nrLogger.log(Level.FINER, "Custom Instrumentation - Reading request parameter value " + parameterValue);
					NewRelic.addCustomParameter("request-parameter-" + parameterName, parameterValue);
				}
			}
		}
		if (cookieNames != null) {
			Cookie[] cookies = request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie != null) {
					String cookieName = cookie.getName();
					for (int j = 0; j < cookieNames.length; j++) {
						if (cookieNames[j].equalsIgnoreCase(cookieName)) {
							String cookieValue = cookie.getValue();
							if (cookieValue != null) {
								nrLogger.log(Level.FINER, "Custom Instrumentation - Reading request cookie value " + cookieValue);
								NewRelic.addCustomParameter("request-cookie-" + cookieName, cookieValue);
							}	
						}	
					}
				}
			}
		}
		nrLogger.log(Level.FINER, "Custom Instrumentation - Calling original HttpServlet.service method");
		Weaver.callOriginal();
	}
}