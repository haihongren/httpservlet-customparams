package com.newrelic.fit.instrumentation.spring.web.nrutil;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

public class NRCallback implements SuccessCallback<ClientHttpResponse>, FailureCallback {
	
	private ClientHttpResponse response = null;
	private Throwable error = null;
	private boolean isDone = false;
	
	public NRCallback() {
	}

	@Override
	public void onFailure(Throwable ex) {
		error = ex;
		isDone = true;
	}

	@Override
	public void onSuccess(ClientHttpResponse result) {
		response = result;
		isDone = true;
	}

	public ClientHttpResponse getResponse() {
		return response;
	}

	public Throwable getError() {
		return error;
	}

	public boolean isDone() {
		return isDone;
	}

	
}
