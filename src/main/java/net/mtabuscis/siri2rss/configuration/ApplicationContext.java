package net.mtabuscis.siri2rss.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.mtabuscis.siri2rss.input.GenericFileReader;
import net.mtabuscis.siri2rss.input.services.RawDataService;

@Configuration
public class ApplicationContext {

	@Bean
    public RawDataService getRawDataService() {
        return new GenericFileReader();
    }
	
}
