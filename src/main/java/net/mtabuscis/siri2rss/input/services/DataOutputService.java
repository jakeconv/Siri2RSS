package net.mtabuscis.siri2rss.input.services;

import java.util.List;
import net.mtabuscis.siri2rss.model.SiriPayload;

public interface DataOutputService {

	//cannot block parent calling!
	public void publish(List<SiriPayload> data);
	
}
