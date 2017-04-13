package com.sumavision.talktv2.http.eventbus;

import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * http event
 */
public final class HttpEvent {
	BaseJsonParser parser;

	public HttpEvent(BaseJsonParser parser) {
		this.parser = parser;
	}

	public BaseJsonParser getParser() {
		return parser;
	}
}
