package com.newrelic.fit.instrumentation.spring.web.nrutil;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;

public class InboundWrapper implements InboundHeaders {
	
	private ClientHttpResponse response;
	
	public InboundWrapper(ClientHttpResponse resp) {
		response = resp;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		if(response != null) {
			HttpHeaders headers = response.getHeaders();
			List<String> values = headers.get(name);
			if(values != null && !values.isEmpty()) {
				return values.get(0);
			}
			return null;
		}
		return null;
	}

	
	public void dumpHeaders() {
		Logger logger = NewRelic.getAgent().getLogger();
		logger.log(Level.FINE, "Response Headers");
		HttpHeaders headers = response.getHeaders();
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
