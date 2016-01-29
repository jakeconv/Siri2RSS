package net.mtabuscis.siri2rss.input.services;

import java.net.URI;

public interface RawDataService {

	//use this to load the url/file location/etc (previous load is cleared)
	public void load(URI uri);
	
	//use this to get the actual data from that location
	public String getContentsAsString();
	
}
