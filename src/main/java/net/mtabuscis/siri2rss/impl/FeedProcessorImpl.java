package net.mtabuscis.siri2rss.impl;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import net.mtabuscis.siri2rss.input.services.DataOutputService;
import net.mtabuscis.siri2rss.input.services.RawDataService;
import net.mtabuscis.siri2rss.input.services.SiriParserService;
import net.mtabuscis.siri2rss.model.SiriPayload;

@Component
public class FeedProcessorImpl {

	private static Logger _log = LoggerFactory.getLogger(FeedProcessorImpl.class);
	
	@Autowired
	private RawDataService _dataService;
	
	@Autowired
	private SiriParserService[] _parsers;
	
	@Autowired 
	private DataOutputService[] _dataOutput;
	
	@PostConstruct
	private void init(){
		//TODO set up a timer to trigger the raw data service reading process on occasion
		_log.info("Starting read");
		String data = getFileContents("siri_sx_20160111.xml"); // Sample input
		_log.info("File read, data="+data);
		List<SiriPayload> payload = getPayload(data);
		_log.info("Payload done, size "+payload.size());
		for(DataOutputService o : _dataOutput){
			//do not blocking publish
			o.publish(payload);
		}
	}
	
	private List<SiriPayload> getPayload(String data) {
		for(SiriParserService ps : _parsers){
			if(ps.isHandler(data)){
				return ps.parse(data);
			}
		}
		return new ArrayList<SiriPayload>();
	}
	
	private String getFileContents(String filename){
		//test file input
		URL resourceDirPath = this.getClass().getClassLoader().getResource("/samples/"+filename);
		if(resourceDirPath == null)
			return "";
		try {
			_dataService.load(resourceDirPath.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException("URI Syntax Exception trying to get the resources directory for the files.");
		}
		String data = _dataService.getContentsAsString();
		if(data == null)
			return "";
		return data;
	}
	
}
