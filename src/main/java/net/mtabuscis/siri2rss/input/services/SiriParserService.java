package net.mtabuscis.siri2rss.input.services;

import java.util.List;

import net.mtabuscis.siri2rss.model.SiriPayload;

public interface SiriParserService {

	public boolean isHandler(String data);
	public List<SiriPayload> parse(String xmlString);
	
}
