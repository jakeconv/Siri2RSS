package net.mtabuscis.siri2rss.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//Frontend data storage
@Controller
public class RSSWebInterface {

	private static Logger _log = LoggerFactory.getLogger(RSSWebInterface.class);
	
	private String _data = "test";
	
	public void setRSSData(String out){
		_log.info("RSS Controller received new data set: "+out);
		_data = out;
	}
	
	@RequestMapping(value="/rss", method = RequestMethod.GET)
	@ResponseBody
	public String rssPublish(){
		_log.info("Call to /rss made via web");
		return _data;
	}
	
}
