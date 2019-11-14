package com.newrelic.fit.instrumentation.spring.web.web.client;

import java.net.URI;
import java.util.logging.Level;

import com.newrelic.fit.instrumentation.spring.web.nrutil.InboundWrapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

@Weave
public abstract class RestTemplate {
	
	@NewField
	private InboundWrapper inboundWrapper = null;
	

	@Trace(leaf=true)
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Inside RestTemplate:doExecute, inboundWrapper set to {0}",inboundWrapper);
		T retValue = Weaver.callOriginal();
		String procedure = method.name();
		if(inboundWrapper != null) {
			inboundWrapper.dumpHeaders();
			HttpParameters params = HttpParameters.library("SpringRest").uri(url).procedure(procedure).inboundHeaders(inboundWrapper).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		} else {
			NewRelic.getAgent().getLogger().log(Level.FINE, "inboundWrapper has not been set");
			HttpParameters params = HttpParameters.library("SpringRest").uri(url).procedure(procedure).noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		return retValue;
	}
	
	protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Inside RestTemplate:handleResponse, inboundWrapper has been set to {0} using response {1}",inboundWrapper,response);
		inboundWrapper = new InboundWrapper(response);
		NewRelic.getAgent().getLogger().log(Level.FINE, "inboundWrapper has been set to {0} using response {1}",inboundWrapper,response);
		Weaver.callOriginal();
	}
	
	
}
