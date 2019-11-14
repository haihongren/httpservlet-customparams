package com.newrelic.fit.instrumentation.spring.web.web.client;

import java.net.URI;

//import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.fit.instrumentation.spring.web.nrutil.HttpParams;
import com.newrelic.fit.instrumentation.spring.web.nrutil.NRCallback;
import com.newrelic.fit.instrumentation.spring.web.nrutil.NRListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
//import com.nr.instrumentation.fit.spring.web.HttpParams;
//import com.nr.instrumentation.fit.spring.web.NRCallback;
//import com.nr.instrumentation.fit.spring.web.NRListener;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.ResponseExtractor;

@Weave
public abstract class AsyncRestTemplate {

	@Trace
	protected <T> ListenableFuture<T> doExecute(URI url, HttpMethod method, AsyncRequestCallback requestCallback, ResponseExtractor<T> responseExtractor)  {
		ListenableFuture<T> f = Weaver.callOriginal();
		return f;
	}
	
	@Weave
	private static class ResponseExtractorFuture<T> extends ListenableFutureAdapter<T, ClientHttpResponse> {
		@NewField
		private Token token = null;
		
		public ResponseExtractorFuture(HttpMethod method, URI url,ListenableFuture<ClientHttpResponse> clientHttpResponseFuture,ResponseExtractor<T> responseExtractor) {
			super(clientHttpResponseFuture);
			token = NewRelic.getAgent().getTransaction().getToken();
			Segment segment = NewRelic.getAgent().getTransaction().startSegment("SpringRestRequest");
			HttpParams httpParams = new HttpParams(url, method.name(), "SpringRest");
			NRCallback callback = new NRCallback();
			clientHttpResponseFuture.addCallback(callback, callback);
			NRListener<T> futureListener = new NRListener<T>(token, segment, httpParams,callback);
			addCallback(futureListener, futureListener);
		}
		
		@Trace(async=true,leaf=true)
		protected final T adapt(ClientHttpResponse response) {
			if(token != null) {
				token.linkAndExpire();
				token = null;
			}
			T result = Weaver.callOriginal();
			
			return result;
		}
	}
}
