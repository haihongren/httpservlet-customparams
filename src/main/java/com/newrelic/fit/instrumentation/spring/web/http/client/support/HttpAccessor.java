package com.newrelic.fit.instrumentation.spring.web.http.client.support;

import java.net.URI;
import java.util.logging.Level;

import com.newrelic.fit.instrumentation.spring.web.nrutil.OutboundWrapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.BaseClass)
public abstract class HttpAccessor {

	@Trace(excludeFromTransactionTrace=true)
	protected ClientHttpRequest createRequest(URI url, HttpMethod method) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Inside HttpAccessor:createRequest, url is {0} and HttpMethod:name is {1}", url, method.name());

		ClientHttpRequest req = Weaver.callOriginal();
		OutboundWrapper wrapper = new OutboundWrapper(req);
//		AgentBridge.getAgent().getTransaction().getCrossProcessState().processOutboundRequestHeaders(wrapper, NewRelic.getAgent().getTracedMethod());
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		wrapper.dumpHeaders();
		return req;
	}
}
