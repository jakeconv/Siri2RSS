package net.mtabuscis.siri2rss.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

//Import Twitter things
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

import net.mtabuscis.siri2rss.input.services.DataOutputService;
import net.mtabuscis.siri2rss.model.SiriPayload;

@Component
@Profile("Has Twitter")
public class TwitterDataOutputServiceImpl implements DataOutputService{
	
	private static Logger _log = LoggerFactory.getLogger(TwitterDataOutputServiceImpl.class);
	
	private Twitter _twitterFeed;
	
	private ThreadPoolTaskExecutor _executor;
	
	@Override
	public void publish(List<SiriPayload> data) {
		TwitterFeedCreator tweet=new TwitterFeedCreator(_twitterFeed, data);
		_executor.execute(tweet);
	}
	
	//Configure the twitter account
	@PostConstruct
	private void setupTwitter(){
		//First, set up the twitter account.  Gonna want to put these keys in a config file.
		ConfigurationBuilder config=new ConfigurationBuilder();
		config.setDebugEnabled(true)
		  .setOAuthConsumerKey("2TZ71q8VFL3mjF2N1RIfRU2CN")
		  .setOAuthConsumerSecret("kPE0XndrBgD3fsDWU8YxLdkNVazk2dfstk2PM9E2kCtbbqOkxX")
		  .setOAuthAccessToken("4814539408-bEuJ1soVfDYEqrcMhA4OdTYcT6UnO5Vcv1L61PY")
		  .setOAuthAccessTokenSecret("bF5RPu9TWHpwa1SFdF5YCUygxpgJIqUH0jVcIDSAk1gqy");
		//Set the authorization
		OAuthAuthorization auth=new OAuthAuthorization(config.build());
		_twitterFeed=new TwitterFactory().getInstance(auth);
		//Now, set up a thread
		_executor = new ThreadPoolTaskExecutor();
		_executor.setCorePoolSize(1);
		_executor.setMaxPoolSize(1);
		_executor.setQueueCapacity(5);//5 tasks should be too many...
		_executor.initialize();
		_log.info("The twitter feed has been set up.");
	}
	


	private class TwitterFeedCreator implements Runnable{
		private List <String> _published=new ArrayList<String>();
		private Twitter _twitterFeed;
		private List <SiriPayload> _data;
	
		public TwitterFeedCreator(Twitter twitterFeed, List <SiriPayload> data){
			_data=data;
			_twitterFeed=twitterFeed;
		}
	
		@Override
		public void run() {
			for (int i=0; i<_data.size(); i++){
				SiriPayload current=_data.get(i);
				if (checkTwitterPublishing(current)==false){
					//If this is true, then the report has not been published.
					try{
						String summary=shortSummary(current);
						_log.info("Summary: "+summary);
						_twitterFeed.updateStatus(summary);
					}
					catch (TwitterException e){
						_log.info("Error updating the twitter feed!");
					}
				}
			}
			_log.info("Finished updating the twitter feed.");
		}
		
	
	
		//Check to see if an event has been published
		private boolean checkTwitterPublishing(SiriPayload currentReport){
			String id=currentReport.getIdentification();
			if (_published.size()==0){
				//Nothing has been published.  Return false.
				_published.add(id);
				_log.info("Publishing "+id+" to Twitter.");
				return false;
			}
			//Check and see if the report has been published.
			else if (_published.contains(id)){
				_log.info(id + " has already been published to Twitter.");
				return true;
			}
			//If it hasn't been found at this point, it needs to be published.
			else{
				_published.add(id);
				_log.info("Publishing "+id+" to Twitter.");
				return false;
			}
		}
	
		//Make the summary 140 characters for Twitter
		private String shortSummary(SiriPayload currentReport){
			String shortened=currentReport.getSummary();
			shortened=shortened.replace("\n", "");
			if (shortened.length()>140){
				shortened=shortened.substring(0,136);
				shortened=shortened+"...";
			}
			return shortened;
		}
	}	
}
