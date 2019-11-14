package com.newrelic.fit.instrumentation.spring.web.nrutil;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AsyncClientHttpRequest;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.OutboundHeaders;

public class AsyncOutboundWrapper implements OutboundHeaders {
	
	private AsyncClientHttpRequest request = null;
	
	public AsyncOutboundWrapper(AsyncClientHttpRequest r) {
		request = r;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Setting header {0} to {1}",name,value);
		HttpHeaders headers = request.getHeaders();
		headers.add(name, value);
	}

	public void dumpHeaders() {
		Logger logger = NewRelic.getAgent().getLogger();
		logger.log(Level.FINE, "Request Headers");
		HttpHeaders headers = request.getHeaders();
		if(headers.isEmpty()) {
			logger.log(Level.FINE, "\tNo Headers have been set");
		}
		Set<String> keys = headers.keySet();
		for(String key : keys) {
			List<String> values = headers.get(key);
			int size = values.size();
			if(size > 1) {
				int length = key.length();
				int i = 1;
				StringBuffer sb = new StringBuffer();
				sb.append('\t');
				sb.append(key);
				sb.append(": ");
				for(String value : values) {
					if(i==1) {
						sb.append(value);
					} else {
						sb.append('\t');
						for(int j=0;j<length;j++) {
							sb.append(' ');
						}
						sb.append(": ");
						sb.append(value);
					}
					i++;
					if(i < size -1) {
						sb.append('\n');
					}
				}
				logger.log(Level.FINE, sb.toString());
			} else if(size == 1) {
				logger.log(Level.FINE, "\t{0}: {1})",key,values.get(0));
			} else {
				logger.log(Level.FINE, "\t{0}: No values)",key);
			}
		}

	}
}
