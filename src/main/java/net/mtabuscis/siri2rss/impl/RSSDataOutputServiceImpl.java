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
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

@Component
public class RSSDataOutputServiceImpl implements DataOutputService {
	
	private static Logger _log = LoggerFactory.getLogger(RSSDataOutputServiceImpl.class);
	
	@Autowired
	private RSSWebInterface _rssWebStore;
	
	private ThreadPoolTaskExecutor _executor;
	
	//for threads
	private static SyndFeed _latestRSSFeed = null;
	
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
				_latestRSSFeed = new SyndFeedImpl();
			}
			fillMetaData();
			fillContent();
			
			String feedString = "";
			SyndFeedOutput output = new SyndFeedOutput();
			try {
				feedString = output.outputString(_latestRSSFeed);
			} catch (FeedException e) {
				e.printStackTrace();
				throw new RuntimeException("FeedException with converting the rss feed to an output string");
			}

			_web.setRSSData(feedString);
			
			_log.info("Feed is now: "+feedString);
			
			_log.info("Feed fully read... RSS Creation Thread Ending");
		}
		
		private void fillMetaData(){
			_latestRSSFeed.setTitle("MTA Alerts");
			_latestRSSFeed.setDescription("Information from SIRI regarding NYC Public Transit");
			_latestRSSFeed.setLink("http://www.mta.info");
			_latestRSSFeed.setLanguage("en");
			_latestRSSFeed.setFeedType("rss_2.0");
		}
		
		private void fillContent(){
			
			List<SyndEntry> items = new ArrayList<SyndEntry>();
			
			for(SiriPayload siriRow : _data ){
				
				_log.info("Setting data for item "+siriRow.getSummary());
			
				SyndEntry rssRow = new SyndEntryImpl();
				rssRow.setTitle(siriRow.getTitle());
				rssRow.setLink(siriRow.getUrl());
				
				SyndContent description = new SyndContentImpl();
				description.setType("text/plain");
				description.setValue(siriRow.getComments());
				rssRow.setDescription(description);
				//desc.set
				
				List<SyndContent> sContents = new ArrayList<SyndContent>();
				SyndContent sc = new SyndContentImpl();
				sc.setType("text/plain");
				sc.setValue(siriRow.getSummary());
				rssRow.setContents(sContents);
				
				rssRow.setPublishedDate(siriRow.getCreatedDate());
				
				items.add(rssRow);
			}
			
			_latestRSSFeed.setEntries(items);
		}
		
	}
	
}
