package com.newrelic.fit.instrumentation.spring.web.nrutil;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;

public class NRListener<T> implements FailureCallback, SuccessCallback<T> {

	private Token token = null;
	private Segment segment = null;
	private HttpParams httpParams = null;
	private NRCallback callback = null;


	public NRListener(Token token, Segment segment, HttpParams httpParams,NRCallback c) {
		super();
		this.token = token;
		this.segment = segment;
		this.httpParams = httpParams;
		callback = c;
	}

	@Override
	public void onSuccess(T result) {
		ClientHttpResponse response = null;
		if(callback != null) {
			if(callback.isDone()) {
				response = callback.getResponse();
			}
		}
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(segment != null) {
			if(httpParams != null) {
				InboundWrapper wrapper = null;
				HttpParameters params = null; 
				if(response != null) {
					wrapper = new InboundWrapper(response);
					params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).inboundHeaders(wrapper).build();
				} else {
					params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
				}
				segment.reportAsExternal(params);
				segment.end();
			}
		} else if(httpParams != null) {
			InboundWrapper wrapper = null;
			HttpParameters params = null; 
			if(response != null) {
				wrapper = new InboundWrapper(response);
				params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).inboundHeaders(wrapper).build();
			} else {
				params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
			}
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
	}

	@Override
	public void onFailure(Throwable ex) {
		Throwable t = null;
		if(callback != null) {
			if(callback.isDone()) {
				t = callback.getError();
			}
		}
		if(t != null && !t.equals(ex) && !t.equals(ex.getCause())) {
			NewRelic.noticeError(ex);
		}
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(segment != null) {
			if(httpParams != null) {
				HttpParameters params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
				segment.reportAsExternal(params);
				segment.end();
			}
		} else if(httpParams != null) {
			HttpParameters params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}

	}

}
