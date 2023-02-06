package com.newrelic.fit.instrumentation;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

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

    @NewField
    private static boolean allowRequestBodyScan = false;

    @NewField
    private static boolean allowResponseBodyScan = false;

    @NewField
    private static boolean isResponseWrapped = false;

    @NewField
    private static String[] bodyNames = null;

    @NewField
    private static String[] respbodyNames = null;

    @NewField
    private static boolean SOAPActionAsTransactionName = false;

    public HttpServlet_CustomParams_Instrumentation() {
        Logger nrLogger = NewRelic.getAgent().getLogger();

        Object prefixParam = NewRelic.getAgent().getConfig().getValue("prefix");
        if (prefixParam != null) {
            prefix = (String) prefixParam + "-";
        } else {
            prefix = "";
        }

        allowRequestWrapper = NewRelic.getAgent().getConfig().getValue("allowRequestWrapper", false);
        allowRequestBodyScan = NewRelic.getAgent().getConfig().getValue("allowRequestBodyScan", false);
        allowResponseBodyScan = NewRelic.getAgent().getConfig().getValue("allowResponseBodyScan", false);
        SOAPActionAsTransactionName = NewRelic.getAgent().getConfig().getValue("SOAPActionAsTransactionName", false);

        Object headerNameObj = NewRelic.getAgent().getConfig().getValue("custom_request_header_names");
        if (headerNameObj != null) {
            String headerName = (String) headerNameObj;
            try {
                headerNames = headerName.split("\\s*,\\s*");
            } catch (Throwable t) {
                nrLogger.log(Level.SEVERE, "Custom Instrumentation httpServlet - Error setting up request headers " + t.getMessage());
            }
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Getting request headers for these following headers");
            for (int i = 0; i < headerNames.length; i++) {
                String name = headerNames[i];
                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - adding header name: " + name);
            }
        } else {
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - custom_request_header_names not defined.");
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - use \"custom_request_header_names: [comma separated header names]\" in newrelic.yml");
        }

        Object paramNameObj = NewRelic.getAgent().getConfig().getValue("custom_request_parameter_names");
        if (paramNameObj != null) {
            String parameterName = (String) paramNameObj;
            try {
                parameterNames = parameterName.split("\\s*,\\s*");
            } catch (Throwable t) {
                nrLogger.log(Level.SEVERE, "Custom Instrumentation httpServlet - Error setting up request parameters " + t.getMessage());
            }
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Getting request parameters for the following parameters ");
            for (int i = 0; i < parameterNames.length; i++) {
                String name = parameterNames[i];
                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - adding parameter name: " + name);
            }
        } else {
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - custom_request_parameter_names not defined.");
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - use \"custom_request_parameter_names: [comma separated parameter names]\" in newrelic.yml");
        }

        Object cookieNameObj = NewRelic.getAgent().getConfig().getValue("custom_request_cookie_names");
        if (cookieNameObj != null) {
            String cookieName = (String) cookieNameObj;
            try {
                cookieNames = cookieName.split("\\s*,\\s*");
            } catch (Throwable t) {
                nrLogger.log(Level.SEVERE, "Custom Instrumentation httpServlet - Error setting up request cookies " + t.getMessage());
            }
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Getting request cookies for the following cookies ");
            for (int i = 0; i < cookieNames.length; i++) {
                String name = cookieNames[i];
                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - adding cookie name: " + name);
            }
        } else {
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - custom_request_cookie_names not defined.");
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - use \"custom_request_cookie_names: [comma separated cookie names]\" in newrelic.yml");
        }

        Object bodyNameObj = NewRelic.getAgent().getConfig().getValue("custom_request_body_names");
        if (bodyNameObj != null) {
            String bodyname = (String) bodyNameObj;
            try {
                bodyNames = bodyname.split("\\s*,\\s*");
            } catch (Throwable t) {
                nrLogger.log(Level.SEVERE, "Custom Instrumentation httpServlet - Error setting up request body key value pairs " + t.getMessage());
            }
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Search request body for the following keys");
            for (int i = 0; i < bodyNames.length; i++) {
                String name = bodyNames[i];
                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - body key name: " + name);
            }
        } else {
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - custom_request_body_names not defined.");
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - use \"custom_request_body_names: [comma separated body names]\" in newrelic.yml");
        }

        Object respbodyNameObj = NewRelic.getAgent().getConfig().getValue("custom_response_body_names");
        if (respbodyNameObj != null) {
            String respbodyname = (String) respbodyNameObj;
            try {
                respbodyNames = respbodyname.split("\\s*,\\s*");
            } catch (Throwable t) {
                nrLogger.log(Level.SEVERE, "Custom Instrumentation httpServlet - Error setting up response body key value pairs " + t.getMessage());
            }
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Search response body for the following keys");
            for (int i = 0; i < respbodyNames.length; i++) {
                String name = respbodyNames[i];
                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - body key name: " + name);
            }
        } else {
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - custom_response_body_names not defined.");
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - use \"custom_response_body_names: [comma separated body names]\" in newrelic.yml");
        }


    }

    @Trace(dispatcher = true)
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        Logger nrLogger = NewRelic.getAgent().getLogger();
        if (headerNames != null) {
            for (int i = 0; i < headerNames.length; i++) {
                String headerName = headerNames[i];
                if (headerName.equals("URL")) {
                    String requestURL = request.getRequestURL().toString();
                    if (request.getQueryString() != null) {
                        requestURL += "?" + request.getQueryString();
                    }
                    nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading request URL value " + requestURL);
                    NewRelic.addCustomParameter(prefix + "URL", requestURL);
                } else {
                    String headerValue = request.getHeader(headerName);
                    if (headerValue != null) {
                        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading request header value " + headerValue);
                        NewRelic.addCustomParameter(prefix + headerName, headerValue);
                        if ( headerName.equals("SOAPAction") && SOAPActionAsTransactionName ) {

                            AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH,true, "SOAPWebService",headerValue);
                            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Set transaction name " + headerValue);
                        }
                    }
                }
            }
        }
        if (parameterNames != null) {
            if (request.getMethod().equalsIgnoreCase("POST")) {
                if (request.getContentType().equalsIgnoreCase("application/x-www-form-urlencoded")) {
                    if (allowRequestWrapper) {
                        // Introduced for Great American Insurance who send POST data with form content type but really send a JSON that they want to be able to access through request stream later on
                        HttpServletRequest originalRequest = request;
                        try {
                            request = new RequestWrapper((HttpServletRequest) request);
                            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Created Wrapper Request for downstream code to be able to access request input stream again ");
                        } catch (IOException e) {
                            request = originalRequest;
                        }
                        originalRequest = null;

                        for (int i = 0; i < parameterNames.length; i++) {
                            String parameterName = parameterNames[i];
                            String parameterValue = request.getParameter(parameterName);
                            if (parameterValue != null) {
                                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading request parameter value " + parameterValue);
                                NewRelic.addCustomParameter(prefix + parameterName, parameterValue);
                            }
                        }
                    } else {
                        //also add any parameters from query string
                        String queryString = request.getQueryString();

                        if (queryString != null) {
                            String[] queryParameters = queryString.split("&");
                            for (String queryParameter : queryParameters) {
                                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading URL query parameter " + queryParameter);
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

                    if (queryString != null) {
                        String[] queryParameters = queryString.split("&");
                        for (String queryParameter : queryParameters) {
                            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading URL query parameter " + queryParameter);
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
                        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading request parameter value " + parameterValue);
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
                                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading request cookie value " + cookieValue);
                                NewRelic.addCustomParameter(prefix + cookieName, cookieValue);
                            }
                        }
                    }
                }
            }
        }

        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - requestBodyNames: " + Arrays.toString(bodyNames));
        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - responseBodyNames: " + Arrays.toString(respbodyNames));
        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - allowRequestBodyScan: " + allowRequestBodyScan);
        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - allowResponseBodyScan: " + allowResponseBodyScan);

        if (bodyNames != null && allowRequestBodyScan) {
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - ContentType: " + request.getContentType());
                if (request.getContentType() !=null && request.getContentType().equalsIgnoreCase("application/json")) {
                    boolean hasJsonbody = false;
                    HttpServletRequest originalRequest = request;
                    try {
                        request = new RequestWrapper((HttpServletRequest) request);
                        hasJsonbody = ((RequestWrapper) request).hasJsonBody();
                        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - wrapped request successfully" );
                        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - json body:"+((RequestWrapper) request).getJsonBody() );
                    } catch (IOException e) {
                        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - wrap request FAILED" );

                        request = originalRequest;
                    }
                    nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - is request body json format: " + hasJsonbody);
                    nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - request body json: " + hasJsonbody);
                    originalRequest = null;

                    if (hasJsonbody) {
                        for (int i = 0; i < bodyNames.length; i++) {
                            String bodyName = bodyNames[i];
                            String bodyValue = ((RequestWrapper) request).bodyHasKey(bodyName);
                            if (bodyValue != null) {
                                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Reading request body value " + bodyValue);
                                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Added custom attribute: " + bodyName+":"+ bodyValue);

                                NewRelic.addCustomParameter(prefix + bodyName, bodyValue);
                            }
                        }
                    }
                }
        }

        if (respbodyNames != null && allowResponseBodyScan) {
            nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - response ContentType: " + response.getContentType());
            HttpServletResponse originalResponse = response;
            try {
                response = new ResponseWrapper((HttpServletResponse) response);
                isResponseWrapped=true;
                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - wrap response SUCCESSFUL" );
            } catch (IOException e) {
                nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - wrap response FAILED" );
                response = originalResponse;
            }
        }

        nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Calling original HttpServlet.service method");
        Weaver.callOriginal();

        if (isResponseWrapped && (response.getContentType() != null && request.getContentType().equalsIgnoreCase("application/json"))) {
            ResponseWrapper responseCopier = (ResponseWrapper) response;
            for (int i = 0; i < respbodyNames.length; i++) {
                String bodyName = respbodyNames[i];
                String bodyValue = responseCopier.bodyHasKey(bodyName);
                if (bodyValue != null) {
                    nrLogger.log(Level.FINER, "Custom Instrumentation httpServlet - Added custom attribute: " + bodyName + ":" + bodyValue);
                    NewRelic.addCustomParameter(prefix + bodyName, bodyValue);
                }
            }
        }
    }
}