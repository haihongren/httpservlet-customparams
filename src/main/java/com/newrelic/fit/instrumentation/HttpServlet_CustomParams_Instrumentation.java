package com.newrelic.fit.instrumentation;

import java.io.IOException;
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
	
	@NewField
	private static String prefix = "";
	
	@NewField
	private static boolean allowRequestWrapper = false;
	
	public HttpServlet_CustomParams_Instrumentation() {
		Logger nrLogger = NewRelic.getAgent().getLogger();
		
		Object prefixParam = NewRelic.getAgent().getConfig().getValue("prefix");
		if (prefixParam != null) {
			prefix = (String) prefixParam + "-";
		} else {
			prefix = "";
		}
		
		allowRequestWrapper = NewRelic.getAgent().getConfig().getValue("allowRequestWrapper", false);
		
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
					NewRelic.addCustomParameter(prefix + "URL", requestURL);						
				} else {
					String headerValue = request.getHeader(headerName); 
					if (headerValue != null) {
						nrLogger.log(Level.FINER, "Custom Instrumentation - Reading request header value " + headerValue);
						NewRelic.addCustomParameter(prefix + headerName, headerValue);
					}
				}
			}
		}
		if (parameterNames != null) {
			if (request.getMethod().equalsIgnoreCase("POST")) {
				if  (request.getContentType().equalsIgnoreCase("application/x-www-form-urlencoded")) {
					if (allowRequestWrapper) {
						// Introduced for Great American Insurance who send POST data with form content type but really send a JSON that they want to be able to access through request stream later on
						HttpServletRequest originalRequest = request;
						try {
							request = new RequestWrapper((HttpServletRequest) request);
							nrLogger.log(Level.FINER, "Custom Instrumentation - Created Wrapper Request for downstream code to be able to access request input stream again ");
						} catch (IOException e) {
							request = originalRequest;
						}	
						originalRequest = null;
						
						for (int i = 0; i < parameterNames.length; i++) {
							String parameterName = parameterNames[i];
							String parameterValue = request.getParameter(parameterName); 
							if (parameterValue != null) {
								nrLogger.log(Level.FINER, "Custom Instrumentation - Reading request parameter value " + parameterValue);
								NewRelic.addCustomParameter(prefix + parameterName, parameterValue);
							} 
						}
					} else {
						//also add any parameters from query string
						String queryString = request.getQueryString();
						
						if(queryString != null) {
							String[] queryParameters = queryString.split("&");
						    for (String queryParameter : queryParameters) {
						    	nrLogger.log(Level.FINER, "Custom Instrumentation - Reading URL query parameter " + queryParameter);
								String[] keyValuePair = queryParameter.split("=");
						        if (keyValuePair.length > 1) {
						        		for (int i = 0; i < parameterNames.length; i++) {
						        			if (parameterNames[i].equalsIgnoreCase(keyValuePair[0])) {
						        				NewRelic.addCustomParameter(prefix + keyValuePair[0], keyValuePair[1]);
						        			}
						        		}
								}
						    }
						}
					}
				} else {
					//also add any parameters from query string
					String queryString = request.getQueryString();
					
					if(queryString != null) {
						String[] queryParameters = queryString.split("&");
					    for (String queryParameter : queryParameters) {
					    	nrLogger.log(Level.FINER, "Custom Instrumentation - Reading URL query parameter " + queryParameter);
							String[] keyValuePair = queryParameter.split("=");
					        if (keyValuePair.length > 1) {
					        		for (int i = 0; i < parameterNames.length; i++) {
					        			if (parameterNames[i].equalsIgnoreCase(keyValuePair[0])) {
					        				NewRelic.addCustomParameter(prefix + keyValuePair[0], keyValuePair[1]);
					        			}
					        		}
							}
					    }
					}	
				}
			} else {
				for (int i = 0; i < parameterNames.length; i++) {
					String parameterName = parameterNames[i];
					String parameterValue = request.getParameter(parameterName); 
					if (parameterValue != null) {
						nrLogger.log(Level.FINER, "Custom Instrumentation - Reading request parameter value " + parameterValue);
						NewRelic.addCustomParameter(prefix + parameterName, parameterValue);
					} 
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
								NewRelic.addCustomParameter(prefix + cookieName, cookieValue);
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