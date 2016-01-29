package net.mtabuscis.siri2rss.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import net.mtabuscis.siri2rss.controller.RSSWebInterface;
import net.mtabuscis.siri2rss.input.services.DataOutputService;
import net.mtabuscis.siri2rss.model.SiriPayload;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;

@Component
public class RSSDataOutputServiceImpl implements DataOutputService {
	
	private static Logger _log = LoggerFactory.getLogger(RSSDataOutputServiceImpl.class);
	
	@Autowired
	private RSSWebInterface _rssWebStore;
	
	private ThreadPoolTaskExecutor _executor;
	
	//for threads
	private static Channel _latestRSSFeed = null;
	
	@Override
	//non blocking method... can't block
	public void publish(List<SiriPayload> data) {
		RSSCreator rss = new RSSCreator(data, _rssWebStore);
		_executor.execute(rss);
	}
	
	@PostConstruct
	public void createTaskExecutor() {
		_executor = new ThreadPoolTaskExecutor();
		_executor.setCorePoolSize(1);
		_executor.setMaxPoolSize(1);
		_executor.setQueueCapacity(5);//5 tasks should be too many...
		_executor.initialize();
	}
	
	private class RSSCreator implements Runnable{
		
		List<SiriPayload> _data;
		private RSSWebInterface _web;//TODO pull this out
		
		public RSSCreator(List<SiriPayload> data, RSSWebInterface web){
			_data = data;
			_web = web;
			_log.info("RSS Creation Thread Started");
		}
		
		@Override
		public void run(){
			if(_latestRSSFeed == null){
				_latestRSSFeed = new Channel();
			}
			fillMetaData();
			fillContent();
			
			String latestFeed = _latestRSSFeed.getGenerator();
			
			_web.setRSSData(latestFeed);
			
			_log.info("Feed is now: "+latestFeed);
			
			_log.info("Feed fully read... RSS Creation Thread Ending");
		}
		
		private void fillMetaData(){
			_latestRSSFeed.setTitle("MTA Alerts");
			_latestRSSFeed.setDescription("Information from SIRI regarding NYC Public Transit");
			_latestRSSFeed.setLink("http://www.mta.info");
			_latestRSSFeed.setLanguage("en");
		}
		
		private void fillContent(){
			
			List<Item> items = new ArrayList<Item>();
			
			for(SiriPayload siriRow : _data ){
				
				_log.info("Setting data for item "+siriRow.getSummary());
			
				Item rssRow = new Item();
				rssRow.setTitle(siriRow.getTitle());
				rssRow.setLink(siriRow.getUrl());
				rssRow.setComments(siriRow.getComments());
				Description d = new Description();
				d.setValue(siriRow.getSummary());
				rssRow.setDescription(d);
				rssRow.setPubDate(siriRow.getCreatedDate());
				
				items.add(rssRow);
			}
			
			_latestRSSFeed.setItems(items);
		}
		
	}
	
}
