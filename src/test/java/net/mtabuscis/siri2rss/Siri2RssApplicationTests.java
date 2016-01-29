package net.mtabuscis.siri2rss;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Siri2RssApplication.class)
public class Siri2RssApplicationTests {

	@Test
	public void contextLoads() {
		assertTrue(true);
	}

}
